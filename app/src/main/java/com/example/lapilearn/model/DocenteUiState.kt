package com.example.lapilearn.model

data class DocenteUiState(
    val correo: String = "",
    val contrasena: String = "",
    val errores: DocenteErrores = DocenteErrores()
)
