package com.example.lapilearn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.lapilearn.navigation.AppNavigation
import com.example.lapilearn.ui.theme.LapiLearnTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LapiLearnTheme {
                AppNavigation()
            }
        }
    }
}