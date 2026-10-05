package com.example.lapilearn.navigation

sealed class Screen(val route: String) {
    data object Home : Screen(route = "home_page")
    data object DocenteLogin : Screen(route = "docente_login")
    data object AsistenteLogin : Screen(route = "asistente_login")

    data object DocenteDashboard : Screen(route = "docente_dashboard")

    data class Detail(val itemId: String) : Screen(route = "detail_page/{itemId}") {
        fun buildRoute(): String {
            return route.replace(oldValue = "{itemId}", newValue = itemId)
        }
    }
}