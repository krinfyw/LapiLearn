package com.example.lapilearn.ui.screens.estudiante

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lapilearn.ui.component.Cabecera
import com.example.lapilearn.ui.components.CampoTexto

@Composable
fun IngresoCodigoScreen(
    modifier: Modifier = Modifier,
    navController: NavController
    ){
    var codigo by remember { mutableStateOf("") }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier.height(56.dp))

        Cabecera("Hola Estudiante!")

        Spacer(modifier.height(16.dp))

        Text(
            text = "Ingesa el código que te dio tu profesora o asistente",
            modifier.padding(16.dp),
            color = Color.Gray
        )

        CampoTexto(
            valor ="",
            etiqueta = "codigo",
            onChange = { codigo = it }
        )

    }
}