package com.example.lapilearn.ui.screens.inicio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.lapilearn.model.AsistenteUiState
import com.example.lapilearn.navigation.Screen
import com.example.lapilearn.ui.component.Cabecera
import com.example.lapilearn.ui.components.CampoTexto
import com.example.lapilearn.ui.theme.coralColor
import com.example.lapilearn.ui.theme.creamBg
import com.example.lapilearn.ui.theme.inkText
import com.example.lapilearn.ui.theme.tealColor
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
        onVolverClick = { navController.popBackStack() },
        onIngresarClick = {
            viewModel.loginAsistente {
                navController.navigate(Screen.AsistentePanel.route) {
                    // Al volver atrás no se regresa al login
                    popUpTo(Screen.AsistenteLogin.route) { inclusive = true }
                }
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
    onVolverClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(creamBg)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Botón para volver al inicio
        Spacer(Modifier.height(32.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onVolverClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver al inicio",
                    tint = inkText
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Cabecera("Iniciar sesión")

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
                containerColor = tealColor,
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ingresar")
        }
    }
}