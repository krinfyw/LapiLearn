package com.example.lapilearn.ui.screens.estudiante

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lapilearn.model.PasoEstudiante
import com.example.lapilearn.ui.component.estudiante.*
import com.example.lapilearn.ui.theme.creamBg
import com.example.lapilearn.ui.theme.inkText
import com.example.lapilearn.ui.theme.tealColor
import com.example.lapilearn.viewModels.EstudianteViewModel

@Composable
fun EstudiantePregunta(viewModel: EstudianteViewModel, onSalir: () -> Unit) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    var confirmarSalida by remember { mutableStateOf(false) }
    val sesion = estado.sesion
    BackHandler { if (!estado.cargando) confirmarSalida = true }

    // Usamos las variables globales de ui.theme
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = tealColor,
            onPrimary = Color.White,
            background = creamBg,
            surface = Color.White,
            onSurface = inkText,
            onBackground = inkText
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .safeDrawingPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
                .widthIn(max = 720.dp)
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Práctica de estudiante",
                style = MaterialTheme.typography.labelLarge,
                color = tealColor
            )
            Text(
                text = sesion?.actividad?.titulo ?: "Tu actividad",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            if (estado.cargando) LinearProgressIndicator(Modifier.fillMaxWidth())

            AnimatedVisibility(visible = estado.error != null) {
                Text(
                    text = estado.error.orEmpty(),
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (sesion == null) {
                Text(if (estado.cargando) "Cargando actividad…" else "Ingresa tu código para cargar una actividad.")
                Button(onClick = onSalir, enabled = !estado.cargando) {
                    Text("Volver al código")
                }
            } else {
                Text(
                    text = "${sesion.actividad.modulo} · ${sesion.actividad.curso} · Intento ${sesion.intento.numero} de ${sesion.actividad.intentosPermitidos}",
                    style = MaterialTheme.typography.bodyMedium
                )
                AnimatedContent(
                    targetState = estado.paso,
                    label = "pasoEstudiante"
                ) { paso ->
                    when (paso) {
                        PasoEstudiante.INSTRUCCIONES -> InstruccionesEstudiante(estado, viewModel::comenzar)
                        PasoEstudiante.PREGUNTA -> PreguntaEstudiante(estado, viewModel::seleccionar, viewModel::solicitarConfirmacion)
                        PasoEstudiante.RETROALIMENTACION -> RetroalimentacionEstudiante(estado, viewModel::siguiente)
                        PasoEstudiante.RESULTADO -> ResultadoEstudiante(estado, viewModel::reintentar)
                    }
                }
                OutlinedButton(
                    onClick = { confirmarSalida = true },
                    enabled = !estado.cargando,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Salir de la actividad")
                }
            }
        }

        if (estado.confirmar) {
            AlertDialog(
                onDismissRequest = viewModel::cancelarConfirmacion,
                title = { Text("¿Confirmas tu respuesta?") },
                text = {
                    Text("Elegiste: ${estado.preguntaActual?.alternativas?.getOrNull(estado.seleccion ?: -1).orEmpty()}\nUna vez enviada no podrás cambiarla en este intento.")
                },
                confirmButton = {
                    TextButton(
                        onClick = viewModel::confirmarRespuesta,
                        enabled = !estado.cargando
                    ) {
                        Text(if (estado.cargando) "Guardando…" else "Confirmar")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = viewModel::cancelarConfirmacion,
                        enabled = !estado.cargando
                    ) {
                        Text("Volver")
                    }
                }
            )
        }

        if (confirmarSalida) {
            AlertDialog(
                onDismissRequest = { confirmarSalida = false },
                title = { Text("¿Salir de la actividad?") },
                text = { Text("Las respuestas confirmadas están guardadas. Puedes continuar con el mismo código en este dispositivo.") },
                confirmButton = {
                    TextButton(onClick = {
                        confirmarSalida = false
                        onSalir()
                    }) {
                        Text("Salir")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { confirmarSalida = false }) {
                        Text("Continuar aquí")
                    }
                }
            )
        }
    }
}