package com.example.lapilearn.ui.screens.estudiante

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.lapilearn.model.EstudianteUiState
import com.example.lapilearn.navigation.Screen
import com.example.lapilearn.ui.component.Cabecera
import com.example.lapilearn.ui.components.CampoTexto
import com.example.lapilearn.ui.theme.inkText
import com.example.lapilearn.ui.theme.tealColor
import com.example.lapilearn.viewModels.EstudianteViewModel

@Composable
fun IngresoCodigoScreen(
    viewModel: EstudianteViewModel,
    navController: NavController
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    FormularioCodigo(
        estado = estado,
        onCodigoChange = viewModel::onCodigoChange,
        onIngresarClick = {
            viewModel.ingresar {
                navController.navigate(Screen.EstudiantePregunta.route) { launchSingleTop = true }
            }
        }
    )
}

@Composable
fun FormularioCodigo(
    estado: EstudianteUiState,
    onCodigoChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onIngresarClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(56.dp))

        Cabecera("Hola Estudiante!")

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Ingresa el código de tu actividad de práctica.",
            fontSize = 16.sp,
            color = inkText.copy(alpha = 0.7f),
        )

        Spacer(modifier = Modifier.height(16.dp))

        CampoTexto(
            valor = estado.codigo,
            etiqueta = "código",
            onChange = onCodigoChange,
            error = estado.errores.codigo
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onIngresarClick,
            enabled = !estado.cargando,
            colors = ButtonDefaults.buttonColors(
                containerColor = tealColor,
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (estado.cargando) "Buscando actividad…" else "Ingresar")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("Práctica local · Usa el mismo dispositivo", fontSize = 12.sp, color = inkText.copy(alpha = 0.5f))
    }
}