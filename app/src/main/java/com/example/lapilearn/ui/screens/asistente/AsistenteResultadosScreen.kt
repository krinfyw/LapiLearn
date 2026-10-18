package com.example.lapilearn.ui.screens.asistente

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.lapilearn.model.Asignacion
import com.example.lapilearn.model.EstadoAsignacion
import com.example.lapilearn.model.Modulo
import com.example.lapilearn.viewModels.AsistentePanelViewModel

private val CreamBg = Color(0xFFFBF8F2)
private val InkText = Color(0xFF22303A)
private val TealColor = Color(0xFF2C6E7F)
private val TealLight = Color(0xFFE4F0F0)
private val CoralColor = Color(0xFFE8804A)
private val CoralLight = Color(0xFFFCEADF)
private val OkColor = Color(0xFF4E9F6B)
private val OkLight = Color(0xFFE5F5EB)
private val GrayLight = Color(0xFFF3EFE4)
private val LineColor = Color(0xFFE1D9C8)

/** Resultados básicos de las asignaciones: estado, aciertos, errores, intentos y observaciones. */
@Composable
fun AsistenteResultadosScreen(
    navController: NavController,
    viewModel: AsistentePanelViewModel
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = CreamBg,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .border(width = 1.dp, color = LineColor)
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
                Column {
                    Text(text = "Perfil: Asistente de aula", fontSize = 12.sp, color = Color.Gray)
                    Text(
                        text = "Resultados de práctica",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = InkText
                    )
                }
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
            item { ResumenGeneral(estado.asignaciones) }

            if (estado.asignaciones.isEmpty()) {
                item {
                    Text(
                        text = "Aún no has asignado actividades.",
                        fontSize = 16.sp,
                        color = InkText
                    )
                }
            } else {
                items(estado.asignaciones, key = { it.id }) { asignacion ->
                    AsignacionCard(
                        asignacion = asignacion,
                        onObservacion = { viewModel.abrirObservacion(asignacion) }
                    )
                }
            }

            item {
                Text(
                    text = "Herramienta de apoyo para orientar la práctica. No es una nota oficial " +
                        "ni un diagnóstico; el criterio es de docentes, UTP y PIE.",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }

    val enObservacion = estado.asignacionEnObservacion
    if (enObservacion != null) {
        DialogoObservacion(
            asignacion = enObservacion,
            texto = estado.textoObservacion,
            error = estado.errorObservacion,
            onTextoChange = viewModel::onObservacionChange,
            onGuardar = viewModel::guardarObservacion,
            onCerrar = viewModel::cerrarObservacion
        )
    }
}

@Composable
private fun ResumenGeneral(asignaciones: List<Asignacion>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, LineColor),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Resumen", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = InkText)

            EstadoAsignacion.entries.forEach { estado ->
                val cantidad = asignaciones.count { it.estado == estado }
                Text("${estado.etiqueta}: $cantidad", fontSize = 14.sp, color = InkText)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Modulo.entries.forEach { modulo ->
                val delModulo = asignaciones.filter { it.modulo == modulo }
                Text(
                    text = "${modulo.etiqueta}: ${delModulo.sumOf { it.aciertos }} aciertos · " +
                        "${delModulo.sumOf { it.errores }} errores",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun AsignacionCard(asignacion: Asignacion, onObservacion: () -> Unit) {
    val (colorEstado, fondoEstado) = when (asignacion.estado) {
        EstadoAsignacion.PENDIENTE -> Color.Gray to GrayLight
        EstadoAsignacion.EN_DESARROLLO -> CoralColor to CoralLight
        EstadoAsignacion.FINALIZADA -> OkColor to OkLight
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, LineColor),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = asignacion.aliasEstudiante,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = InkText
                )
                Surface(color = fondoEstado, shape = RoundedCornerShape(12.dp)) {
                    Text(
                        text = asignacion.estado.etiqueta,
                        color = colorEstado,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Text(text = asignacion.nombreActividad, fontSize = 15.sp, color = InkText)
            Text(
                text = "${asignacion.modulo.etiqueta} · ${etiquetaCurso(asignacion.curso)} · " +
                    "${asignacion.dificultad.etiqueta} · ${asignacion.fecha}",
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Dato("Aciertos", asignacion.aciertos)
                Dato("Errores", asignacion.errores)
                Dato("Intentos", asignacion.intentos)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(color = TealLight, shape = RoundedCornerShape(8.dp)) {
                Text(
                    text = "Código: ${asignacion.codigo}",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TealColor,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (asignacion.observacion.isBlank()) "Sin observación" else asignacion.observacion,
                fontSize = 14.sp,
                color = if (asignacion.observacion.isBlank()) Color.Gray else InkText
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onObservacion,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(44.dp)
            ) {
                Text(
                    text = if (asignacion.observacion.isBlank()) "Agregar observación" else "Editar observación",
                    color = InkText,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun Dato(etiqueta: String, valor: Int) {
    Column {
        Text(text = valor.toString(), fontWeight = FontWeight.Bold, fontSize = 20.sp, color = InkText)
        Text(text = etiqueta, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
private fun DialogoObservacion(
    asignacion: Asignacion,
    texto: String,
    error: String?,
    onTextoChange: (String) -> Unit,
    onGuardar: () -> Unit,
    onCerrar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Observación para ${asignacion.aliasEstudiante}") },
        text = {
            Column {
                Text(
                    text = "Escribe una observación pedagógica general. No incluyas nombres reales, " +
                        "diagnósticos ni notas.",
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = texto,
                    onValueChange = onTextoChange,
                    label = { Text("Observación") },
                    isError = error != null,
                    supportingText = {
                        Text(error ?: "${texto.length}/${AsistentePanelViewModel.MAX_OBSERVACION}")
                    },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onGuardar,
                colors = ButtonDefaults.buttonColors(containerColor = TealColor)
            ) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onCerrar) { Text("Cancelar") } }
    )
}
