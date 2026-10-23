package com.example.lapilearn.ui.component.estudiante

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.lapilearn.model.EstudianteUiState
import com.example.lapilearn.ui.theme.tealColor
import com.example.lapilearn.ui.theme.tealLight

// Como Formulario en el ejemplo: recibe datos y avisa las acciones mediante lambdas.
@Composable
fun InstruccionesEstudiante(estado: EstudianteUiState, onComenzar: () -> Unit) {
    val sesion = estado.sesion ?: return
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Antes de comenzar", style = MaterialTheme.typography.titleLarge)
        Text(sesion.actividad.instrucciones)
        Text("${sesion.preguntas.size} preguntas · ${sesion.respuestas.size} respuestas guardadas")
        Text("Tu progreso se guarda en este dispositivo. Cada intento recorre toda la actividad.")
        Button(onClick = onComenzar, enabled = !estado.cargando, modifier = Modifier.fillMaxWidth()) {
            Text(if (sesion.respuestas.isEmpty()) "Comenzar" else "Continuar actividad")
        }
    }
}

@Composable
fun PreguntaEstudiante(
    estado: EstudianteUiState,
    onSeleccionar: (Int) -> Unit,
    onResponder: () -> Unit
) {
    val sesion = estado.sesion ?: return
    val pregunta = estado.preguntaActual ?: return
    val indice = sesion.preguntas.indexOfFirst { it.id == pregunta.id }
    val progreso by animateFloatAsState(indice.toFloat() / sesion.preguntas.size, label = "progreso")

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Pregunta ${indice + 1} de ${sesion.preguntas.size}", style = MaterialTheme.typography.titleMedium)
        LinearProgressIndicator(progress = { progreso }, modifier = Modifier.fillMaxWidth())
        if (pregunta.textoApoyo.isNotBlank()) {
            Card(colors = CardDefaults.cardColors(containerColor = tealLight)) {
                Text(pregunta.textoApoyo, Modifier.padding(16.dp), style = MaterialTheme.typography.bodyLarge)
            }
        }
        Text(pregunta.enunciado, style = MaterialTheme.typography.titleLarge)
        Column(Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            pregunta.alternativas.forEachIndexed { indiceOpcion, texto ->
                AlternativaEstudiante(
                    texto = texto,
                    seleccionada = estado.seleccion == indiceOpcion,
                    habilitada = !estado.cargando && !estado.confirmar,
                    onSeleccionar = { onSeleccionar(indiceOpcion) }
                )
            }
        }
        Button(onClick = onResponder, enabled = estado.seleccion != null && !estado.cargando,
            modifier = Modifier.fillMaxWidth()) { Text("Responder") }
    }
}

@Composable
private fun AlternativaEstudiante(
    texto: String,
    seleccionada: Boolean,
    habilitada: Boolean,
    onSeleccionar: () -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = if (seleccionada) tealLight else Color.White,
        border = BorderStroke(1.dp, tealColor),
        modifier = Modifier.fillMaxWidth().selectable(
            selected = seleccionada, enabled = habilitada, role = Role.RadioButton,
            onClick = onSeleccionar
        )
    ) {
        Row(Modifier.padding(12.dp).heightIn(min = 48.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = seleccionada, onClick = null)
            Spacer(Modifier.width(12.dp))
            Text(texto, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun RetroalimentacionEstudiante(estado: EstudianteUiState, onSiguiente: () -> Unit) {
    val sesion = estado.sesion ?: return
    val pregunta = estado.preguntaRespondida ?: return
    val respuesta = sesion.respuestas.firstOrNull { it.preguntaId == pregunta.id } ?: return

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Icon(
            if (respuesta.esCorrecta) Icons.Default.CheckCircle else Icons.Default.Info,
            contentDescription = null, tint = tealColor,
            modifier = Modifier.size(40.dp)
        )
        Text(if (respuesta.esCorrecta) "¡Muy bien!" else "Sigamos aprendiendo",
            style = MaterialTheme.typography.titleLarge)
        Text("Elegiste: ${pregunta.alternativas[respuesta.alternativa]}")
        Text("Respuesta correcta: ${pregunta.alternativas[pregunta.correcta]}")
        Text(pregunta.retroalimentacion, style = MaterialTheme.typography.bodyLarge)
        Button(onClick = onSiguiente, enabled = !estado.cargando, modifier = Modifier.fillMaxWidth()) {
            Text(if (sesion.finalizada) "Ver resultado" else "Siguiente pregunta")
        }
    }
}

@Composable
fun ResultadoEstudiante(estado: EstudianteUiState, onReintentar: () -> Unit) {
    val sesion = estado.sesion ?: return
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("¡Terminaste la actividad!", style = MaterialTheme.typography.titleLarge)
        Text("Aciertos: ${sesion.aciertos} de ${sesion.preguntas.size}", style = MaterialTheme.typography.headlineSmall)
        Text("Preguntas para seguir practicando: ${sesion.respuestas.size - sesion.aciertos}")
        Text("Tus respuestas quedaron guardadas en este dispositivo.")
        if (sesion.intento.numero < sesion.actividad.intentosPermitidos) {
            Button(onClick = onReintentar, enabled = !estado.cargando, modifier = Modifier.fillMaxWidth()) {
                Text("Practicar otra vez")
            }
        } else {
            Text("Completaste los intentos disponibles. Revisa tu avance con tu asistente.")
        }
    }
}