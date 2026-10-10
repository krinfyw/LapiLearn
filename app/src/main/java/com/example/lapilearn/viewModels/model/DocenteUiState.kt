package com.example.lapilearn.viewModels.model

data class DocenteUiState(
    val correo: String = "",
    val contrasena: String = "",
    val errores: DocenteErrores = DocenteErrores()
)
