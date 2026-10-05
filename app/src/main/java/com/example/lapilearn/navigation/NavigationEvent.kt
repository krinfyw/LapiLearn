package com.example.lapilearn.navigation

sealed class NavigationEvent {
    data class NavigateTo(
        val route: Screen,
        val popupToRoute: Screen? = null,
        val inclusive: Boolean = false,
        val singleTop: Boolean = false
    ) : NavigationEvent()

    object PopBackStack : NavigationEvent()
    object NavigateUp : NavigationEvent()
}