package com.example.lapilearn.model

data class EstudianteUiState (
    val codigo: String = "",
    val errores: EstudianteErrores = EstudianteErrores()
)