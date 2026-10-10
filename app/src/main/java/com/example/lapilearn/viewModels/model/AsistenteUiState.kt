package com.example.lapilearn.viewModels.model

data class AsistenteUiState(
    val correo: String = "",
    val contrasena: String = "",
    val errores: AsistenteErrores = AsistenteErrores()
)