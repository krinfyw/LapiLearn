package com.example.lapilearn.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lapilearn.ui.screens.inicio.HomeScreen
import com.example.lapilearn.ui.screens.inicio.LoginDocenteScreen
import com.example.lapilearn.ui.screens.inicio.LoginAsistenteScreen
import com.example.lapilearn.ui.screens.docente.ResumenDocenteScreen
import com.example.lapilearn.ui.screens.asistente.AsistentePanelScreen
import com.example.lapilearn.ui.screens.asistente.AsistenteResultadosScreen
import com.example.lapilearn.ui.screens.docente.CatalogoActividades
import com.example.lapilearn.ui.screens.estudiante.EstudiantePregunta
import com.example.lapilearn.ui.screens.estudiante.IngresoCodigoScreen
import com.example.lapilearn.viewModels.DocenteViewModel
import com.example.lapilearn.viewModels.AsistentePanelViewModel
import com.example.lapilearn.viewModels.AsistenteViewModel
import com.example.lapilearn.viewModels.EstudianteViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val docenteViewModel: DocenteViewModel = viewModel()
    val asistenteViewModel: AsistenteViewModel = viewModel()
    val estudianteViewModel: EstudianteViewModel = viewModel()
    val asistentePanelViewModel: AsistentePanelViewModel = viewModel()

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
        composable(Screen.AsistentePanel.route) {
            AsistentePanelScreen(
                navController = navController,
                viewModel = asistentePanelViewModel,
                onCerrarSesion = {
                    asistenteViewModel.cerrarSesion()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.AsistenteResultados.route) {
            AsistenteResultadosScreen(
                navController = navController,
                viewModel = asistentePanelViewModel
            )
        }
        composable (Screen.EstudianteIngresoCodigo.route){
            IngresoCodigoScreen(navController= navController, viewModel = estudianteViewModel)
        }
        composable ("estudiante_pregunta"){
            EstudiantePregunta()
        }
    }
}