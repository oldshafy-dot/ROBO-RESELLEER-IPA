package com.robo.reseller
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.robo.reseller.data.ApiClient
import com.robo.reseller.ui.RoboApp
class MainActivity:ComponentActivity(){override fun onCreate(b:Bundle?){super.onCreate(b);ApiClient.init(this);setContent{RoboApp(this)}}}
