package com.example.lapilearn.ui.screens.asistente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.lapilearn.model.DetalleIntento
import com.example.lapilearn.model.DetalleRespuesta
import com.example.lapilearn.ui.component.Cabecera
import com.example.lapilearn.ui.theme.coralColor
import com.example.lapilearn.ui.theme.coralLight
import com.example.lapilearn.ui.theme.creamBg
import com.example.lapilearn.ui.theme.inkText
import com.example.lapilearn.ui.theme.lineColor
import com.example.lapilearn.ui.theme.okColor
import com.example.lapilearn.ui.theme.okLight
import com.example.lapilearn.viewModels.AsistentePanelViewModel

/** Muestra qué respondió el estudiante en cada pregunta, intento por intento. */
@Composable
fun AsistenteDetalleScreen(
    navController: NavController,
    viewModel: AsistentePanelViewModel
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val intentos by viewModel.detalle.collectAsStateWithLifecycle()

    val resumen = estado.asignaciones.firstOrNull { it.asignacion.id == estado.asignacionEnDetalleId }

    Scaffold(
        containerColor = creamBg,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Cabecera(
                    texto = "Respuestas detalladas",
                    subtitulo = "Perfil: Asistente de aula",
                    onVolver = { navController.popBackStack() }
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = resumen?.asignacion?.aliasEstudiante ?: "Estudiante",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = inkText
                )
                Text(
                    text = resumen?.actividad?.titulo ?: "Actividad no disponible",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            if (intentos.isEmpty()) {
                item {
                    Text(
                        text = "El estudiante aún no ha respondido preguntas.",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            } else {
                items(intentos, key = { it.numero }) { intento ->
                    IntentoCard(intento)
                }
            }

            item {
                Text(
                    text = "Herramienta de apoyo para orientar la práctica. No es una nota oficial " +
                        "ni un diagnóstico: el criterio es de docentes, UTP y PIE.",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun IntentoCard(intento: DetalleIntento) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Intento ${intento.numero}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = inkText
            )
            Text(
                text = if (intento.finalizado) "Finalizado" else "En desarrollo",
                fontSize = 13.sp,
                color = Color.Gray
            )

            if (intento.respuestas.isEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Sin respuestas todavía.", fontSize = 14.sp, color = Color.Gray)
            }

            intento.respuestas.forEach { respuesta ->
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = lineColor)
                Spacer(modifier = Modifier.height(12.dp))
                RespuestaItem(respuesta)
            }
        }
    }
}

@Composable
private fun RespuestaItem(respuesta: DetalleRespuesta) {
    val colorResultado = if (respuesta.esCorrecta) okColor else coralColor
    val fondoResultado = if (respuesta.esCorrecta) okLight else coralLight

    Column {
        Text(
            text = "Pregunta ${respuesta.orden}",
            fontSize = 12.sp,
            color = Color.Gray
        )
        Text(
            text = respuesta.enunciado,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = inkText
        )

        Spacer(modifier = Modifier.height(8.dp))

        Surface(color = fondoResultado, shape = MaterialTheme.shapes.small) {
            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                Text(
                    text = if (respuesta.esCorrecta) "Correcta" else "Incorrecta",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResultado
                )
                Text(
                    text = "Respondió: ${respuesta.elegida}",
                    fontSize = 14.sp,
                    color = inkText
                )
                if (!respuesta.esCorrecta) {
                    Text(
                        text = "Correcta: ${respuesta.correcta}",
                        fontSize = 14.sp,
                        color = inkText
                    )
                }
            }
        }
    }
}
