package com.dam.lockgo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.dam.lockgo.service.AppNavigation
import com.dam.lockgo.ui.theme.LockGoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LockGoTheme {
                AppNavigation()
            }
        }
    }
}