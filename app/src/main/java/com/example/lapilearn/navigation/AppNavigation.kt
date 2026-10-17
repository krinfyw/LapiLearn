package com.example.lapilearn.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lapilearn.ui.screens.HomeScreen
import com.example.lapilearn.ui.screens.LoginDocenteScreen
import com.example.lapilearn.ui.screens.LoginAsistenteScreen
import com.example.lapilearn.ui.screens.ResumenDocenteScreen
import com.example.lapilearn.ui.screens.ResumenAsistenteScreen
import com.example.lapilearn.ui.screens.CatalogoActividades
import com.example.lapilearn.viewModels.DocenteViewModel
import com.example.lapilearn.viewModels.AsistenteViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val docenteViewModel: DocenteViewModel = viewModel()
    val asistenteViewModel: AsistenteViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.DocenteLogin.route) {
            LoginDocenteScreen(navController = navController, viewModel = docenteViewModel)
        }
        composable(Screen.AsistenteLogin.route) {
            LoginAsistenteScreen(navController = navController, viewModel = asistenteViewModel)
        }
        composable(Screen.DocenteDashboard.route) {
            CatalogoActividades(navController)
        }
        composable("resumen_docente") {
            ResumenDocenteScreen(viewModel = docenteViewModel)
        }
        composable("resumen_asistente") {
            ResumenAsistenteScreen(viewModel = asistenteViewModel)
        }
    }
}