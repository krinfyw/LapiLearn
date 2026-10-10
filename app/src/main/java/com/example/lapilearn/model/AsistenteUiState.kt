package com.example.lapilearn.model

data class AsistenteUiState(
    val correo: String = "",
    val contrasena: String = "",
    val errores: AsistenteErrores = AsistenteErrores()
)