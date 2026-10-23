package com.example.lapilearn.model

data class AsistenteUiState(
    val correo: String = "",
    val contrasena: String = "",
    val nombre: String = "",
    val errores: AsistenteErrores = AsistenteErrores(),
    val usuarioId: Long? = null
)
