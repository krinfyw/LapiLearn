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
import com.example.lapilearn.ui.screens.asistente.ResumenAsistenteScreen
import com.example.lapilearn.ui.screens.docente.CatalogoActividades
import com.example.lapilearn.ui.screens.docente.EditarActividadScreen
import com.example.lapilearn.ui.screens.estudiante.EstudiantePregunta
import com.example.lapilearn.ui.screens.estudiante.IngresoCodigoScreen
import com.example.lapilearn.viewModels.DocenteViewModel
import com.example.lapilearn.viewModels.AsistenteViewModel
import com.example.lapilearn.viewModels.EstudianteViewModel
import com.example.lapilearn.ui.screens.docente.EditarActividadScreen
import com.example.lapilearn.viewModels.ActividadViewModel
import com.example.lapilearn.ui.screens.docente.NuevaActividadScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val docenteViewModel: DocenteViewModel = viewModel()
    val asistenteViewModel: AsistenteViewModel = viewModel()
    val estudianteViewModel: EstudianteViewModel = viewModel()
    val actividadViewModel: ActividadViewModel = viewModel()

    NavHost(
        navController = navController, startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.DocenteLogin.route) {
            LoginDocenteScreen(navController = navController, viewModel = docenteViewModel)
        }
        composable(Screen.AsistenteLogin.route) {
            LoginAsistenteScreen(navController = navController, viewModel = asistenteViewModel)
        }
        composable("resumen_docente") {
            ResumenDocenteScreen(viewModel = docenteViewModel)
        }
        composable("resumen_asistente") {
            ResumenAsistenteScreen(viewModel = asistenteViewModel)
        }
        composable (Screen.EstudianteIngresoCodigo.route){
            IngresoCodigoScreen(navController= navController, viewModel = estudianteViewModel)
        }
        composable ("estudiante_pregunta"){
            EstudiantePregunta()
        }
        composable(Screen.DocenteDashboard.route) {
            CatalogoActividades(navController, actividadViewModel)
        }
        composable(Screen.EditarActividad.route) {
            EditarActividadScreen(navController, actividadViewModel)
        }
        composable(Screen.NuevaActividad.route) {
            NuevaActividadScreen(navController = navController, viewModel = actividadViewModel)
        }
    }
}