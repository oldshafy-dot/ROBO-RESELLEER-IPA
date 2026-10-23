package com.robo.reseller.ui

import android.content.Context
import android.content.Intent
import android.net.Uri

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clip

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Logout

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.robo.reseller.R
import com.robo.reseller.data.*
import com.robo.reseller.ui.theme.*

import kotlinx.coroutines.launch

import java.util.Locale


@Composable
fun RoboApp(ctx: Context) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Background,
            surface = Background,
            primary = BrightBlue
        )
    ) {
        var logged by remember {
            mutableStateOf(ApiClient.loggedIn())
        }

        if (logged) {
            MainTabs(
                ctx = ctx,
                onLogout = {
                    ApiClient.logout(ctx)
                    logged = false
                }
            )
        } else {
            Login(
                onLogin = {
                    logged = true
                }
            )
        }
    }
}


@Composable
fun Bg(
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Image(
            painter = painterResource(R.drawable.robo_logo),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 170.dp)
                .alpha(0.055f),
            contentScale = ContentScale.Fit
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                ),
            content = content
        )
    }
}


@Composable
fun Header(
    compact: Boolean = true
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        val logoWidth = if (compact) 150.dp else 245.dp
        val logoHeight = if (compact) 60.dp else 90.dp

        Image(
            painter = painterResource(R.drawable.robo_logo),
            contentDescription = null,
            modifier = Modifier
                .width(logoWidth)
                .height(logoHeight),
            contentScale = ContentScale.Fit
        )

        if (!compact) {
            Text(
                text = "RESELLER PORTAL",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = BrightBlue,
                letterSpacing = 3.sp
            )
        }
    }
}


@Composable
fun CardBox(
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Card)
            .border(
                width = 1.dp,
                color = Blue,
                shape = RoundedCornerShape(18.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content
    )
}


@Composable
fun Login(
    onLogin: () -> Unit
) {
    var username by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var error by remember {
        mutableStateOf("")
    }

    var loading by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Bg {
        Spacer(
            modifier = Modifier.height(25.dp)
        )

        Header(compact = false)

        CardBox {

            Text(
                text = "WELCOME BACK",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = BrightBlue,
                letterSpacing = 2.sp
            )

            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                },
                label = {
                    Text("Username")
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors()
            )

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                },
                label = {
                    Text("Password")
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                colors = fieldColors()
            )

            if (error.isNotEmpty()) {
                Text(
                    text = error,
                    color = Color.White,
                    fontSize = 13.sp
                )
            }

            Button(
                onClick = {
                    scope.launch {
                        loading = true
                        error = ""

                        try {
                            val result = ApiClient.login(
                                context,
                                username,
                                password
                            )

                            if (
                                result.role.lowercase() !=
                                "reseller"
                            ) {
                                ApiClient.logout(context)

                                throw Exception(
                                    "Please sign in with a reseller account."
                                )
                            }

                            onLogin()

                        } catch (e: Exception) {
                            error =
                                e.message ?: "Login failed"
                        }

                        loading = false
                    }
                },
                enabled =
                    username.isNotBlank() &&
                    password.isNotBlank() &&
                    !loading,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Blue
                ),
                shape = RoundedCornerShape(14.dp)
            ) {

                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White
                    )

                    Spacer(
                        modifier = Modifier.width(10.dp)
                    )
                }

                Text(
                    text = "SIGN IN",
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
            }
        }

        Spacer(
            modifier = Modifier.height(25.dp)
        )

        Text(
            text = "SECURE ACCESS • ROBO ANDROID",
            fontSize = 10.sp,
            color = MutedWhite,
            letterSpacing = 1.5.sp
        )
    }
}


@Composable
private fun fieldColors() =
    OutlinedTextFieldDefaults.colors(
        focusedBorderColor = BrightBlue,
        unfocusedBorderColor = Blue,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedLabelColor = BrightBlue,
        unfocusedLabelColor = MutedWhite
    )


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTabs(
    ctx: Context,
    onLogout: () -> Unit
) {
    var tab by remember {
        mutableIntStateOf(0)
    }

    val titles = listOf(
        "ROBO ANDROID",
        "PRODUCTS",
        "MY KEYS",
        "UPDATES"
    )

    Scaffold(
        containerColor = Background,

        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = titles[tab],
                        fontWeight = FontWeight.Bold
                    )
                },
                colors =
                    TopAppBarDefaults
                        .centerAlignedTopAppBarColors(
                            containerColor = Background,
                            titleContentColor = Color.White
                        )
            )
        },

        bottomBar = {

            NavigationBar(
                containerColor = Color.Black
            ) {

                val icons = listOf(
                    Icons.Default.Home,
                    Icons.Default.GridView,
                    Icons.Default.Key,
                    Icons.Default.Download
                )

                val labels = listOf(
                    "Home",
                    "Products",
                    "My Keys",
                    "Updates"
                )

                icons.forEachIndexed { index, icon ->

                    NavigationBarItem(
                        selected = tab == index,

                        onClick = {
                            tab = index
                        },

                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription =
                                    labels[index]
                            )
                        },

                        label = {
                            Text(labels[index])
                        },

                        colors =
                            NavigationBarItemDefaults.colors(
                                selectedIconColor =
                                    BrightBlue,
                                selectedTextColor =
                                    BrightBlue,
                                unselectedIconColor =
                                    MutedWhite,
                                unselectedTextColor =
                                    MutedWhite,
                                indicatorColor =
                                    Card
                            )
                    )
                }
            }
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier.padding(paddingValues)
        ) {

            when (tab) {

                0 -> Home(
                    ctx = ctx,
                    onLogout = onLogout
                )

                1 -> Products(ctx)

                2 -> Keys()

                3 -> Updates(ctx)
            }
        }
    }
}


@Composable
fun Home(
    ctx: Context,
    onLogout: () -> Unit
) {
    var user by remember {
        mutableStateOf<ApiUser?>(null)
    }

    var error by remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {
        try {
            user = ApiClient.me()
        } catch (e: Exception) {
            error = e.message ?: "Request failed"
        }
    }

    Bg {

        Header()

        if (user != null) {

            val currentUser = user!!

            CardBox {

                Text(
                    text = "ACCOUNT OVERVIEW",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrightBlue,
                    letterSpacing = 1.6.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = "Username",
                        color = MutedWhite
                    )

                    Spacer(
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = currentUser.username
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = "Role",
                        color = MutedWhite
                    )

                    Spacer(
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text =
                            currentUser.role
                                .replaceFirstChar {
                                    it.uppercase()
                                }
                    )
                }

                HorizontalDivider(
                    color = Blue
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Column {

                        Text(
                            text = "AVAILABLE BALANCE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MutedWhite
                        )

                        Text(
                            text = String.format(
                                Locale.US,
                                "$%.2f",
                                currentUser.balance
                            ),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector =
                            Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = BrightBlue,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

        } else if (error.isNotEmpty()) {

            CardBox {
                Text(error)
            }

        } else {

            CircularProgressIndicator(
                color = BrightBlue
            )
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        CardBox {

            Text(
                text = "QUICK ACCESS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = BrightBlue,
                letterSpacing = 1.6.sp
            )

            Text(
                text =
                    "Manage your products, keys and latest updates from one place."
            )

            Row {

                Icon(
                    imageVector =
                        Icons.Default.GridView,
                    contentDescription = null,
                    tint = BrightBlue
                )

                Spacer(
                    modifier = Modifier.width(6.dp)
                )

                Text(
                    text = "Products",
                    color = BrightBlue
                )

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector =
                        Icons.Default.Key,
                    contentDescription = null,
                    tint = BrightBlue
                )

                Spacer(
                    modifier = Modifier.width(6.dp)
                )

                Text(
                    text = "Keys",
                    color = BrightBlue
                )
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(
                width = 1.dp,
                color = Blue
            )
        ) {

            Icon(
                imageVector =
                    Icons.Default.Logout,
                contentDescription = null
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Text("Log Out")
        }
    }
}


@Composable
fun Products(
    ctx: Context
) {
    var products by remember {
        mutableStateOf<List<Product>>(emptyList())
    }

    var error by remember {
        mutableStateOf("")
    }

    var busy by remember {
        mutableStateOf(false)
    }

    var dialog by remember {
        mutableStateOf<String?>(null)
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {

        busy = true

        try {
            products = ApiClient.products()
            error = ""
        } catch (e: Exception) {
            error = e.message ?: "Request failed"
        }

        busy = false
    }

    Bg {

        Header()

        if (error.isNotEmpty()) {

            CardBox {
                Text(error)
            }
        }

        if (
            products.isEmpty() &&
            error.isEmpty() &&
            !busy
        ) {

            CardBox {

                Icon(
                    imageVector =
                        Icons.Default.GridView,
                    contentDescription = null,
                    tint = BrightBlue,
                    modifier = Modifier.size(38.dp)
                )

                Text(
                    text = "NO PRODUCTS",
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text =
                        "Products will appear here when available.",
                    color = MutedWhite
                )
            }
        }

        products.forEach { product ->

            CardBox {

                Text(
                    text = product.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                product.description
                    ?.takeIf { it.isNotBlank() }
                    ?.let { description ->

                        Text(
                            text = description,
                            fontSize = 14.sp,
                            color = MutedWhite
                        )
                    }

                product.version
                    ?.takeIf { it.isNotBlank() }
                    ?.let { version ->

                        Text(
                            text = "VERSION  $version",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrightBlue
                        )
                    }

                product.categories.forEach { category ->

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(
                                RoundedCornerShape(14.dp)
                            )
                            .background(Color.Black)
                            .border(
                                width = 1.dp,
                                color = Blue,
                                shape =
                                    RoundedCornerShape(14.dp)
                            )
                            .padding(12.dp),

                        verticalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {

                        Row(
                            modifier =
                                Modifier.fillMaxWidth()
                        ) {

                            Column {

                                Text(
                                    text = category.name,
                                    fontWeight =
                                        FontWeight.Bold
                                )

                                Text(
                                    text =
                                        "${category.available_count} available",
                                    fontSize = 12.sp,
                                    color = MutedWhite
                                )
                            }

                            Spacer(
                                modifier =
                                    Modifier.weight(1f)
                            )

                            Text(
                                text = String.format(
                                    Locale.US,
                                    "$%.2f",
                                    category.price_cents /
                                        100.0
                                ),
                                fontWeight =
                                    FontWeight.Bold,
                                color = BrightBlue
                            )
                        }

                        Button(
                            onClick = {

                                scope.launch {

                                    busy = true

                                    try {

                                        val result =
                                            ApiClient.getKey(
                                                category.id
                                            )

                                        dialog =
                                            "KEY\n${result.key}\nNew balance: ${
                                                String.format(
                                                    Locale.US,
                                                    "$%.2f",
                                                    result.balance
                                                )
                                            }"

                                        products =
                                            ApiClient.products()

                                    } catch (e: Exception) {

                                        dialog =
                                            e.message
                                                ?: "Purchase failed"
                                    }

                                    busy = false
                                }
                            },

                            enabled =
                                category.available_count >
                                    0 && !busy,

                            modifier =
                                Modifier.fillMaxWidth(),

                            colors =
                                ButtonDefaults
                                    .buttonColors(
                                        containerColor =
                                            Blue
                                    ),

                            shape =
                                RoundedCornerShape(12.dp)

                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Key,
                                contentDescription = null
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(8.dp)
                            )

                            Text(
                                text = "GET KEY",
                                fontWeight =
                                    FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                product.download_url
                    ?.takeIf { it.isNotBlank() }
                    ?.let { url ->

                        TextButton(
                            onClick = {

                                ctx.startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(url)
                                    )
                                )
                            }
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Download,
                                contentDescription = null
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(5.dp)
                            )

                            Text(
                                text =
                                    "Product Download",
                                color = BrightBlue
                            )
                        }
                    }
            }
        }

        if (busy) {

            CircularProgressIndicator(
                color = BrightBlue
            )
        }

        dialog?.let { message ->

            AlertDialog(

                onDismissRequest = {
                    dialog = null
                },

                confirmButton = {

                    TextButton(
                        onClick = {
                            dialog = null
                        }
                    ) {
                        Text("OK")
                    }
                },

                text = {
                    Text(message)
                },

                containerColor = Card
            )
        }
    }
}


@Composable
fun Keys() {

    var keys by remember {
        mutableStateOf<List<KeyRecord>>(emptyList())
    }

    var error by remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {

        try {
            keys = ApiClient.keys()
        } catch (e: Exception) {
            error = e.message ?: "Request failed"
        }
    }

    Bg {

        Header()

        if (error.isNotEmpty()) {

            CardBox {
                Text(error)
            }
        }

        if (
            keys.isEmpty() &&
            error.isEmpty()
        ) {

            CardBox {

                Icon(
                    imageVector =
                        Icons.Default.Key,
                    contentDescription = null,
                    tint = BrightBlue,
                    modifier = Modifier.size(38.dp)
                )

                Text(
                    text = "NO KEYS YET",
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text =
                        "Purchased keys will appear here.",
                    color = MutedWhite
                )
            }
        }

        keys.forEach { key ->

            CardBox {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Key,
                        contentDescription = null,
                        tint = BrightBlue
                    )

                    Spacer(
                        modifier =
                            Modifier.width(6.dp)
                    )

                    Text(
                        text =
                            key.category_name.uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrightBlue
                    )

                    Spacer(
                        modifier =
                            Modifier.weight(1f)
                    )

                    Text(
                        text =
                            key.status.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = key.key_value,
                    fontFamily = FontFamily.Monospace,
                    fontWeight =
                        FontWeight.SemiBold
                )

                Text(
                    text = key.product_name,
                    fontSize = 14.sp,
                    color = MutedWhite
                )

                key.issued_at
                    ?.takeIf { it.isNotBlank() }
                    ?.let { issued ->

                        Text(
                            text = "Issued: $issued",
                            fontSize = 11.sp,
                            color = MutedWhite
                        )
                    }
            }
        }
    }
}


@Composable
fun Updates(
    ctx: Context
) {

    var updates by remember {
        mutableStateOf<List<PanelUpdate>>(emptyList())
    }

    var error by remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {

        try {
            updates = ApiClient.updates()
        } catch (e: Exception) {
            error = e.message ?: "Request failed"
        }
    }

    Bg {

        Header()

        if (error.isNotEmpty()) {

            CardBox {
                Text(error)
            }
        }

        if (
            updates.isEmpty() &&
            error.isEmpty()
        ) {

            CardBox {

                Icon(
                    imageVector =
                        Icons.Default.Download,
                    contentDescription = null,
                    tint = BrightBlue,
                    modifier = Modifier.size(38.dp)
                )

                Text(
                    text = "NO UPDATES",
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text =
                        "Published updates will appear here.",
                    color = MutedWhite
                )
            }
        }

        updates.forEach { update ->

            CardBox {

                Row {

                    Text(
                        text = update.product_name,
                        fontWeight =
                            FontWeight.Bold
                    )

                    Spacer(
                        modifier =
                            Modifier.weight(1f)
                    )

                    if (update.is_latest == true) {

                        Text(
                            text = "LATEST",
                            fontSize = 11.sp,
                            fontWeight =
                                FontWeight.Bold,
                            color = BrightBlue,
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = BrightBlue,
                                    shape =
                                        RoundedCornerShape(
                                            20.dp
                                        )
                                )
                                .padding(
                                    horizontal = 9.dp,
                                    vertical = 5.dp
                                )
                        )
                    }
                }

                Text(
                    text =
                        "${update.version} — ${update.title}",
                    fontWeight =
                        FontWeight.Bold,
                    color = BrightBlue
                )

                update.patch_notes
                    ?.takeIf { it.isNotBlank() }
                    ?.let { notes ->

                        Text(notes)
                    }

                update.release_date
                    ?.takeIf { it.isNotBlank() }
                    ?.let { date ->

                        Text(
                            text = date,
                            fontSize = 11.sp,
                            color = MutedWhite
                        )
                    }

                update.download_url
                    ?.takeIf { it.isNotBlank() }
                    ?.let { url ->

                        Button(
                            onClick = {

                                ctx.startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(url)
                                    )
                                )
                            },

                            modifier =
                                Modifier.fillMaxWidth(),

                            colors =
                                ButtonDefaults
                                    .buttonColors(
                                        containerColor =
                                            Blue
                                    ),

                            shape =
                                RoundedCornerShape(12.dp)

                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Download,
                                contentDescription = null
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(6.dp)
                            )

                            Text(
                                text =
                                    "DOWNLOAD UPDATE",
                                fontWeight =
                                    FontWeight.Bold
                            )
                        }
                    }
            }
        }
    }
}
