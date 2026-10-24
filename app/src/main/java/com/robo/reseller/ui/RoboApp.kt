
package com.robo.reseller.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robo.reseller.data.ApiClient
import com.robo.reseller.data.ApiUser
import com.robo.reseller.data.KeyRecord
import com.robo.reseller.data.PanelUpdate
import com.robo.reseller.data.Product
import kotlinx.coroutines.launch
import java.util.Locale

private val Black = Color(0xFF000000)
private val SurfaceDark = Color(0xFF101113)
private val SurfaceLight = Color(0xFF17191D)
private val Blue = Color(0xFF168FFF)
private val White = Color(0xFFF7F8FA)
private val Gray = Color(0xFF9A9DA5)
private val BorderGray = Color(0xFF292C32)
private val Green = Color(0xFF59D69A)

@Composable
fun RoboApp(ctx: Context) {
    var loggedIn by remember { mutableStateOf(ApiClient.loggedIn()) }

    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Black,
            surface = SurfaceDark,
            primary = Blue,
            onPrimary = White,
            onBackground = White,
            onSurface = White,
            secondary = Gray,
            error = Color(0xFFFF7777)
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Black
        ) {
            if (loggedIn) {
                MainTabs(
                    ctx = ctx,
                    onLogout = {
                        ApiClient.logout(ctx)
                        loggedIn = false
                    }
                )
            } else {
                Login { loggedIn = true }
            }
        }
    }
}

@Composable
private fun Page(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Black)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        content = content
    )
}

@Composable
private fun Section(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceDark)
            .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
            .padding(15.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        content = content
    )
}

@Composable
private fun Heading(text: String) {
    Text(
        text = text,
        fontSize = 21.sp,
        fontWeight = FontWeight.SemiBold,
        color = White
    )
}

@Composable
private fun Secondary(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        color = Gray,
        lineHeight = 19.sp
    )
}

@Composable
private fun PrimaryButton(
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.heightIn(min = 44.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Blue,
            contentColor = White,
            disabledContainerColor = Color(0xFF30343A),
            disabledContentColor = Gray
        )
    ) {
        Text(text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun QuietButton(
    text: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
            brush = androidx.compose.ui.graphics.SolidColor(BorderGray)
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(text, color = White, fontSize = 12.sp)
    }
}

@Composable
private fun ErrorText(message: String) {
    if (message.isNotBlank()) {
        Text(
            text = message,
            color = Color(0xFFFF8585),
            fontSize = 13.sp,
            lineHeight = 19.sp
        )
    }
}

@Composable
private fun LoadingLine() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            color = Blue,
            strokeWidth = 2.dp
        )
        Spacer(Modifier.width(10.dp))
        Secondary("Loading...")
    }
}

@Composable
private fun TelegramSection(
    title: String,
    subtitle: String,
    url: String
) {
    val context = LocalContext.current

    Section(
        modifier = Modifier.clickable {
            context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(url))
            )
        }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title, color = White, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Secondary(subtitle)
            }
            Icon(
                imageVector = Icons.Default.OpenInNew,
                contentDescription = "Open link",
                tint = Blue
            )
        }
    }
}

@Composable
fun Login(onLogin: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    Page(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        Spacer(Modifier.height(28.dp))

        Text(
            "ROBO",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            color = White
        )
        Text("RESELLER PORTAL", color = Blue, fontSize = 11.sp, letterSpacing = 2.sp)

        Spacer(Modifier.height(18.dp))

        Heading("Welcome back")
        Secondary("Sign in to access your reseller account.")

        Section {
            Text("USERNAME", color = Gray, fontSize = 11.sp)
            OutlinedTextField(
                value = username,
                onValueChange = { username = it; error = "" },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Enter username", color = Gray) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                shape = RoundedCornerShape(8.dp),
                colors = loginFieldColors()
            )

            Text("PASSWORD", color = Gray, fontSize = 11.sp)
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; error = "" },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Enter password", color = Gray) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(8.dp),
                colors = loginFieldColors()
            )

            ErrorText(error)

            PrimaryButton(
                text = if (loading) "SIGNING IN..." else "LOGIN",
                enabled = username.isNotBlank() &&
                    password.isNotBlank() && !loading,
                onClick = {
                    scope.launch {
                        loading = true
                        error = ""
                        try {
                            val user = ApiClient.login(context, username.trim(), password)
                            if (user.role.lowercase(Locale.US) != "reseller") {
                                ApiClient.logout(context)
                                throw Exception("Please sign in with a reseller account.")
                            }
                            onLogin()
                        } catch (e: Exception) {
                            error = e.message ?: "Login failed. Please try again."
                        } finally {
                            loading = false
                        }
                    }
                }
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            "Don't have a username and password?",
            color = White,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
        Secondary(
            "Contact the admin to get your reseller account and password. " +
                "You can also get keys at affordable prices."
        )

        Text(
            "CONTACT ADMIN  ↗",
            color = Blue,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .clickable {
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/robomods"))
                    )
                }
                .padding(vertical = 10.dp, horizontal = 4.dp)
        )
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun loginFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Blue,
    unfocusedBorderColor = BorderGray,
    focusedTextColor = White,
    unfocusedTextColor = White,
    cursorColor = Blue,
    focusedLabelColor = Blue,
    unfocusedLabelColor = Gray,
    focusedContainerColor = SurfaceLight,
    unfocusedContainerColor = SurfaceLight
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTabs(
    ctx: Context,
    onLogout: () -> Unit
) {
    var tab by remember { mutableIntStateOf(0) }
    var username by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            username = ApiClient.me().username
        } catch (_: Exception) {
            username = ""
        }
    }

    val labels = listOf("Home", "Products", "My Keys", "Updates")
    val icons = listOf(
        Icons.Default.Home,
        Icons.Default.GridView,
        Icons.Default.Key,
        Icons.Default.Download
    )

    Scaffold(
        containerColor = Black,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "ROBO",
                        color = White,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                },
                actions = {
                    if (username.isNotBlank()) {
                        Text(
                            username,
                            color = Gray,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                        Spacer(Modifier.width(4.dp))
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, "Logout", tint = Gray)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Black,
                    titleContentColor = White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Black,
                contentColor = White,
                tonalElevation = 0.dp
            ) {
                labels.forEachIndexed { index, label ->
                    NavigationBarItem(
                        selected = tab == index,
                        onClick = { tab = index },
                        icon = {
                            Icon(
                                icons[index],
                                contentDescription = label,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = { Text(label, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Blue,
                            selectedTextColor = Blue,
                            unselectedIconColor = Gray,
                            unselectedTextColor = Gray,
                            indicatorColor = SurfaceLight
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Black)
        ) {
            when (tab) {
                0 -> Home(ctx)
                1 -> Products(ctx)
                2 -> Keys()
                3 -> Updates(ctx)
            }
        }
    }
}

@Composable
fun Home(ctx: Context) {
    var user by remember { mutableStateOf<ApiUser?>(null) }
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var keys by remember { mutableStateOf<List<KeyRecord>>(emptyList()) }
    var error by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        loading = true
        try {
            user = ApiClient.me()
            products = ApiClient.products()
            keys = ApiClient.keys()
        } catch (e: Exception) {
            error = e.message ?: "Could not load account data."
        } finally {
            loading = false
        }
    }

    Page {
        Heading("Dashboard")
        Secondary("Your reseller account at a glance.")

        ErrorText(error)
        if (loading) LoadingLine()

        user?.let {
            Section {
                Secondary("USERNAME")
                Text(it.username, color = White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                HorizontalDivider(color = BorderGray)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Secondary("ACCOUNT STATUS")
                        Text(it.role.replaceFirstChar { c -> c.uppercase() }, color = Green)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Secondary("BALANCE")
                        Text(
                            "$" + String.format(Locale.US, "%.2f", it.balance),
                            color = White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Section(Modifier.weight(1f)) {
                Secondary("PRODUCTS")
                Text(products.size.toString(), fontSize = 26.sp, color = White, fontWeight = FontWeight.SemiBold)
            }
            Section(Modifier.weight(1f)) {
                Secondary("MY KEYS")
                Text(keys.size.toString(), fontSize = 26.sp, color = White, fontWeight = FontWeight.SemiBold)
            }
        }

        Heading("Recent keys")
        if (keys.isEmpty() && !loading) {
            Section { Secondary("Your purchased keys will appear here.") }
        } else {
            keys.take(3).forEach { key ->
                Section {
                    Text(key.category_name, color = White, fontWeight = FontWeight.Medium)
                    Text(
                        key.key_value,
                        color = Blue,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    )
                    Secondary("${key.product_name} · ${key.status}")
                }
            }
        }

        TelegramSection(
            title = "ROBO Resellers Channel",
            subtitle = "Join us on Telegram",
            url = "https://t.me/roboresellers"
        )
    }
}

@Composable
fun Products(ctx: Context) {
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf("") }
    var buyingId by remember { mutableStateOf<String?>(null) }
    var purchasedKey by remember { mutableStateOf("") }
    var purchaseMessage by remember { mutableStateOf("") }
    var refresh by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(refresh) {
        loading = true
        error = ""
        try {
            products = ApiClient.products()
        } catch (e: Exception) {
            error = e.message ?: "Could not load products."
        } finally {
            loading = false
        }
    }

    Page {
        Heading("Products")
        Secondary("Choose a product and category to get your key.")

        if (loading) LoadingLine()
        ErrorText(error)

        if (!loading && error.isEmpty() && products.isEmpty()) {
            Section { Secondary("No products are currently available.") }
        }

        if (purchasedKey.isNotBlank()) {
            Section {
                Text(
                    "Congratulations, We are good to go.",
                    color = Green,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Text("You purchased a key.", color = White)
                Text(
                    purchasedKey,
                    color = White,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp
                )
                PrimaryButton("COPY KEY") {
                    val clipboard = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(
                        ClipData.newPlainText("Purchased key", purchasedKey)
                    )
                    purchaseMessage = "Key copied to clipboard."
                }
                Secondary(purchaseMessage)
            }
        }

        products.forEach { product ->
            Section {
                Text(
                    product.name,
                    color = White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                product.description?.takeIf { it.isNotBlank() }?.let {
                    Secondary(it)
                }
                product.version?.takeIf { it.isNotBlank() }?.let {
                    Secondary("Version $it")
                }

                if (product.categories.isEmpty()) {
                    Secondary("No purchase categories are currently available.")
                }

                product.categories.forEach { category ->
                    HorizontalDivider(color = BorderGray)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(category.name, color = White, fontWeight = FontWeight.Medium)
                            val price = "$" + String.format(
                                Locale.US,
                                "%.2f",
                                category.price_cents / 100.0
                            )
                            Secondary("$price · ${category.available_count} available")
                        }

                        PrimaryButton(
                            text = if (buyingId == category.id) "PLEASE WAIT" else "GET KEY",
                            enabled = buyingId == null && category.available_count > 0
                        ) {
                            scope.launch {
                                buyingId = category.id
                                error = ""
                                purchasedKey = ""
                                purchaseMessage = ""
                                try {
                                    val result = ApiClient.getKey(category.id)
                                    purchasedKey = result.key
                                    purchaseMessage = "Purchase completed successfully."
                                    refresh++
                                } catch (e: Exception) {
                                    error = e.message ?: "Purchase failed. Please try again."
                                } finally {
                                    buyingId = null
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Keys() {
    var keys by remember { mutableStateOf<List<KeyRecord>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf("") }
    var refresh by remember { mutableIntStateOf(0) }

    LaunchedEffect(refresh) {
        loading = true
        error = ""
        try {
            keys = ApiClient.keys()
        } catch (e: Exception) {
            error = e.message ?: "Could not load keys."
        } finally {
            loading = false
        }
    }

    Page {
        Heading("My Keys")
        Secondary("Your purchased reseller keys.")

        if (loading) LoadingLine()
        ErrorText(error)

        if (!loading && error.isEmpty() && keys.isEmpty()) {
            Section { Secondary("No keys found for your account.") }
        }

        keys.forEach { key ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        key.category_name,
                        color = White,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(key.status, color = Blue, fontSize = 12.sp)
                }

                Spacer(Modifier.height(8.dp))
                Text(
                    key.key_value,
                    color = White,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp
                )
                Spacer(Modifier.height(5.dp))
                Secondary(key.product_name)

                key.issued_at?.takeIf { it.isNotBlank() }?.let {
                    Secondary("Created: $it")
                }
                key.expires_at?.takeIf { it.isNotBlank() }?.let {
                    Secondary("Expires: $it")
                }

                HorizontalDivider(
                    modifier = Modifier.padding(top = 14.dp),
                    color = BorderGray
                )
            }
        }
    }
}

@Composable
fun Updates(ctx: Context) {
    var updates by remember { mutableStateOf<List<PanelUpdate>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf("") }
    var refresh by remember { mutableIntStateOf(0) }

    LaunchedEffect(refresh) {
        loading = true
        error = ""
        try {
            updates = ApiClient.updates()
        } catch (e: Exception) {
            error = e.message ?: "Could not load updates."
        } finally {
            loading = false
        }
    }

    Page {
        Heading("Updates")
        Secondary("Latest product releases and announcements.")

        if (loading) LoadingLine()
        ErrorText(error)

        if (!loading && error.isEmpty() && updates.isEmpty()) {
            Section { Secondary("No updates have been published yet.") }
        }

        updates.forEach { update ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        update.product_name,
                        color = White,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    if (update.is_latest == true) {
                        Text("LATEST", color = Blue, fontSize = 11.sp)
                    }
                }

                Text(
                    "${update.version} · ${update.title}",
                    color = Blue,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )

                update.patch_notes?.takeIf { it.isNotBlank() }?.let {
                    Secondary(it)
                }
                update.release_date?.takeIf { it.isNotBlank() }?.let {
                    Secondary(it)
                }

                update.download_url?.takeIf { it.isNotBlank() }?.let { url ->
                    QuietButton("DOWNLOAD UPDATE") {
                        ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    }
                }

                HorizontalDivider(color = BorderGray)
            }
        }
    }
}
