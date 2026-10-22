package com.example.lapilearn.navigation

sealed class Screen(val route: String) {
    data object Home : Screen(route = "home_page")
    data object DocenteLogin : Screen(route = "docente_login")
    data object AgregarPregunta: Screen(route = "agregar_pregunta")
    data object AsistenteLogin : Screen(route = "asistente_login")
    data object AsistentePanel : Screen(route = "asistente_panel")
    data object AsistenteResultados : Screen(route = "asistente_resultados")

    data object DocenteDashboard : Screen(route = "docente_dashboard")

    data object EditarActividad : Screen(route = "edictar_actividad")

    data object NuevaActividad : Screen(route = "nueva_actividad")
    data object EstudianteIngresoCodigo: Screen(route = "ingreso_codigo")
    data object EstudiantePregunta: Screen(route="estudiante_pregunta")
}