package com.example.lapilearn.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lapilearn.viewModels.DocenteViewModel

@Composable
fun ResumenDocenteScreen(viewModel: DocenteViewModel) {
    val estado by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Correo: ${estado.correo}")
        Spacer(modifier = Modifier.height(8.dp))
        Text("Contraseña: ${"*".repeat(estado.contrasena.length)}")
    }
}