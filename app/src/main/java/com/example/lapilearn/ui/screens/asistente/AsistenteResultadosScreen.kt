package com.example.lapilearn.ui.screens.asistente

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
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
import com.example.lapilearn.model.EstadoAsignacion
import com.example.lapilearn.model.ResumenAsignacion
import com.example.lapilearn.ui.component.Cabecera
import com.example.lapilearn.utils.AsignacionReglas
import com.example.lapilearn.viewModels.AsistentePanelViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val CreamBg = Color(0xFFFBF8F2)
private val InkText = Color(0xFF22303A)
private val Teal = Color(0xFF2C6E7F)
private val TealLight = Color(0xFFE4F0F0)
private val Coral = Color(0xFFE8804A)
private val CoralLight = Color(0xFFFCEADF)
private val OkColor = Color(0xFF4E9F6B)
private val OkLight = Color(0xFFE5F5EB)
private val GrayLight = Color(0xFFF3EFE4)
/** Resultados básicos por asignación: estado, intentos, aciertos, errores y observación. */
@Composable
fun AsistenteResultadosScreen(
    navController: NavController,
    viewModel: AsistentePanelViewModel
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = CreamBg,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Cabecera(
                    texto = "Resultados de práctica",
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
            item { ResumenGeneral(estado.asignaciones) }

            if (estado.asignaciones.isEmpty()) {
                item {
                    Text(
                        text = "Aún no has asignado actividades.",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            } else {
                items(estado.asignaciones, key = { it.asignacion.id }) { resumen ->
                    AsignacionCard(
                        resumen = resumen,
                        onObservacion = { viewModel.abrirObservacion(resumen) }
                    )
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

    val enObservacion = estado.asignacionEnObservacion
    if (enObservacion != null) {
        DialogoObservacion(
            alias = enObservacion.asignacion.aliasEstudiante,
            texto = estado.textoObservacion,
            error = estado.errorObservacion,
            onTextoChange = viewModel::onObservacionChange,
            onGuardar = viewModel::guardarObservacion,
            onCerrar = viewModel::cerrarObservacion
        )
    }
}

@Composable
private fun ResumenGeneral(asignaciones: List<ResumenAsignacion>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("Resumen", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = InkText)

            EstadoAsignacion.entries.forEach { estado ->
                val cantidad = asignaciones.count { it.estado == estado }
                Text("${estado.etiqueta}: $cantidad", fontSize = 14.sp, color = InkText)
            }

            Spacer(modifier = Modifier.height(4.dp))

            listOf("Lectura", "Matemática").forEach { modulo ->
                val delModulo = asignaciones.filter { it.actividad?.modulo == modulo }
                Text(
                    text = "$modulo (último intento): ${delModulo.sumOf { it.aciertos }} aciertos · " +
                        "${delModulo.sumOf { it.errores }} errores",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun AsignacionCard(
    resumen: ResumenAsignacion,
    onObservacion: () -> Unit
) {
    val asignacion = resumen.asignacion
    val actividad = resumen.actividad
    val (colorEstado, fondoEstado) = when (resumen.estado) {
        EstadoAsignacion.PENDIENTE -> Color.Gray to GrayLight
        EstadoAsignacion.EN_DESARROLLO -> Coral to CoralLight
        EstadoAsignacion.FINALIZADA -> OkColor to OkLight
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
        color = Color.White
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
                    fontSize = 16.sp,
                    color = InkText
                )
                Surface(color = fondoEstado, shape = MaterialTheme.shapes.small) {
                    Text(
                        text = resumen.estado.etiqueta,
                        color = colorEstado,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = actividad?.titulo ?: "Actividad no disponible",
                fontSize = 15.sp,
                color = InkText
            )
            Text(
                text = if (actividad != null) {
                    "${actividad.modulo} · ${actividad.curso} · ${actividad.dificultad} · ${formatoFecha(asignacion.creadaEn)}"
                } else {
                    formatoFecha(asignacion.creadaEn)
                },
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                Dato("Intentos", "${resumen.intentosUsados} de ${actividad?.intentosPermitidos ?: "-"}")
                Dato("Aciertos", resumen.aciertos.toString())
                Dato("Errores", resumen.errores.toString())
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(color = TealLight, shape = MaterialTheme.shapes.small) {
                Text(
                    text = "Código: ${asignacion.codigo}",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Teal,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (asignacion.observacion.isBlank()) "Sin observación" else asignacion.observacion,
                fontSize = 14.sp,
                color = if (asignacion.observacion.isBlank()) Color.Gray else InkText
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onObservacion,
                modifier = Modifier.heightIn(min = 48.dp)
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
private fun Dato(etiqueta: String, valor: String) {
    Column {
        Text(text = valor, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = InkText)
        Text(text = etiqueta, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
private fun DialogoObservacion(
    alias: String,
    texto: String,
    error: String?,
    onTextoChange: (String) -> Unit,
    onGuardar: () -> Unit,
    onCerrar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Observación para $alias") },
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
                        Text(error ?: "${texto.length}/${AsignacionReglas.MAX_OBSERVACION}")
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
                colors = ButtonDefaults.buttonColors(containerColor = Teal)
            ) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onCerrar) { Text("Cancelar") } }
    )
}

private fun formatoFecha(millis: Long): String =
    SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date(millis))
