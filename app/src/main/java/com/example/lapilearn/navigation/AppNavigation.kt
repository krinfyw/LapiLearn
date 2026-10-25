package com.example.lapilearn.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lapilearn.ui.screens.inicio.HomeScreen
import com.example.lapilearn.ui.screens.inicio.LoginDocenteScreen
import com.example.lapilearn.ui.screens.inicio.LoginAsistenteScreen
import com.example.lapilearn.ui.screens.asistente.AsistentePanelScreen
import com.example.lapilearn.ui.screens.asistente.AsistenteResultadosScreen
import com.example.lapilearn.ui.screens.asistente.AsistenteDetalleScreen
import com.example.lapilearn.viewModels.AsistentePanelViewModel
import com.example.lapilearn.ui.screens.docente.CatalogoActividades
import com.example.lapilearn.ui.screens.docente.EditarActividadScreen
import com.example.lapilearn.ui.screens.estudiante.EstudiantePregunta
import com.example.lapilearn.ui.screens.estudiante.IngresoCodigoScreen
import com.example.lapilearn.viewModels.DocenteViewModel
import com.example.lapilearn.viewModels.AsistenteViewModel
import com.example.lapilearn.viewModels.EstudianteViewModel
import com.example.lapilearn.viewModels.ActividadViewModel
import com.example.lapilearn.ui.screens.docente.NuevaActividadScreen
import com.example.lapilearn.ui.screens.docente.AgregarPreguntaScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val docenteViewModel: DocenteViewModel = viewModel()
    val asistenteViewModel: AsistenteViewModel = viewModel()
    val estudianteViewModel: EstudianteViewModel = viewModel()
    val actividadViewModel: ActividadViewModel = viewModel()
    val asistentePanelViewModel: AsistentePanelViewModel = viewModel()
    val docente by docenteViewModel.uiState.collectAsStateWithLifecycle()
    val asistente by asistenteViewModel.uiState.collectAsStateWithLifecycle()
    val claveDocente = docente.usuarioId?.let { "docente_$it" }
    val claveAsistente = asistente.usuarioId?.let { "asistente_$it" }

    fun cerrarSesionDocente() {
        docenteViewModel.cerrarSesion()
        navController.navigate(Screen.Home.route) {
            popUpTo(Screen.Home.route) { inclusive = true }
        }
    }

    fun cerrarSesionAsistente() {
        asistenteViewModel.cerrarSesion()
        navController.navigate(Screen.Home.route) {
            popUpTo(Screen.Home.route) { inclusive = true }
        }
    }

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
        composable(Screen.AgregarPregunta.route) {
            AgregarPreguntaScreen(navController = navController, viewModel = actividadViewModel)
        }
        composable(Screen.AsistentePanel.route) {
            AsistentePanelScreen(
                navController = navController,
                viewModel = asistentePanelViewModel,
                asistenteViewModel = asistenteViewModel,
                onCerrarSesion = ::cerrarSesionAsistente,
                usuarioClave = claveAsistente
            )
        }
        composable(Screen.AsistenteResultados.route) {
            AsistenteResultadosScreen(
                navController = navController,
                viewModel = asistentePanelViewModel
            )
        }
        composable(Screen.AsistenteDetalle.route) {
            AsistenteDetalleScreen(
                navController = navController,
                viewModel = asistentePanelViewModel
            )
        }
        composable (Screen.EstudianteIngresoCodigo.route){
            IngresoCodigoScreen(navController= navController, viewModel = estudianteViewModel)
        }
        composable (Screen.EstudiantePregunta.route){
            EstudiantePregunta(viewModel = estudianteViewModel, onSalir = {
                estudianteViewModel.salir()
                navController.popBackStack(Screen.EstudianteIngresoCodigo.route, false)
            })
        }
        composable(Screen.DocenteDashboard.route) {
            CatalogoActividades(
                navController = navController,
                viewModel = actividadViewModel,
                usuarioClave = claveDocente,
                onCerrarSesion = ::cerrarSesionDocente
            )
        }
        composable(Screen.EditarActividad.route) {
            EditarActividadScreen(navController, actividadViewModel)
        }
        composable(Screen.NuevaActividad.route) {
            NuevaActividadScreen(navController = navController, viewModel = actividadViewModel)
        }
    }
}
