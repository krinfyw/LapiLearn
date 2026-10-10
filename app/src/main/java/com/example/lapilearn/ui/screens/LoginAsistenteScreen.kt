package com.example.lapilearn.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.lapilearn.model.AsistenteUiState
import com.example.lapilearn.ui.components.CampoTexto
import com.example.lapilearn.viewModels.AsistenteViewModel

// 1. Composable Contenedor (Stateful): Maneja el ViewModel, la navegación y el estado del ciclo de vida.
@Composable
fun LoginAsistenteScreen(
    navController: NavController,
    viewModel: AsistenteViewModel
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    FormularioLoginAsistente(
        estado = estado,
        onCorreoChange = viewModel::onCorreoChange,
        onContrasenaChange = viewModel::onContrasenaChange,
        onIngresarClick = {
            if (viewModel.validarFormulario()) {
                navController.navigate("resumen_asistente")
            }
        }
    )
}

// 2. Composable Presentacional (Stateless): Solo dibuja la UI.
@Composable
fun FormularioLoginAsistente(
    estado: AsistenteUiState,
    onCorreoChange: (String) -> Unit,
    onContrasenaChange: (String) -> Unit,
    onIngresarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Campo Correo
        Spacer(Modifier.height(56.dp))
        Text(
            text = "Iniciar sesión",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
        Spacer(Modifier.height(12.dp))
        CampoTexto(
            valor = estado.correo,
            etiqueta = "Correo electrónico",
            onChange = onCorreoChange,
            error = estado.errores.correo,
            esCorreo = true,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Contraseña
        CampoTexto(
            valor = estado.contrasena,
            etiqueta = "Contraseña",
            onChange = onContrasenaChange,
            error = estado.errores.contrasena,
            esContrasena = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botón de Ingreso
        Button(
            onClick = onIngresarClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2C6E7F),
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ingresar")
        }
    }
}