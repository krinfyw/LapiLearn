package com.example.lapilearn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lapilearn.navigation.Screen
import com.example.lapilearn.ui.screens.AsistenteLoginScreen
import com.example.lapilearn.ui.screens.CatalogoActividades
import com.example.lapilearn.ui.screens.DocenteLoginScreen
import com.example.lapilearn.ui.screens.HomeScreen
import com.example.lapilearn.ui.theme.LapiLearnTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LapiLearnTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route
                ) {
                    composable(Screen.Home.route) {
                        HomeScreen(navController = navController)
                    }
                    composable(Screen.DocenteLogin.route) {
                        DocenteLoginScreen(navController = navController)
                    }
                    composable(Screen.AsistenteLogin.route) {
                        AsistenteLoginScreen(navController = navController)
                    }
                    composable(Screen.DocenteDashboard.route){
                        CatalogoActividades(navController)
                    }
                }
            }
        }
    }
}