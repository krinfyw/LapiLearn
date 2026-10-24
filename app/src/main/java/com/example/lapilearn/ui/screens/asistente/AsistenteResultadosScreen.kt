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
import com.example.lapilearn.ui.theme.coralColor
import com.example.lapilearn.ui.theme.coralLight
import com.example.lapilearn.ui.theme.creamBg
import com.example.lapilearn.ui.theme.grayLight
import com.example.lapilearn.ui.theme.inkText
import com.example.lapilearn.ui.theme.okColor
import com.example.lapilearn.ui.theme.okLight
import com.example.lapilearn.ui.theme.tealColor
import com.example.lapilearn.ui.theme.tealLight
import com.example.lapilearn.utils.AsignacionReglas
import com.example.lapilearn.viewModels.AsistentePanelViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


/** Resultados básicos por asignación: estado, intentos, aciertos, errores y observación. */
@Composable
fun AsistenteResultadosScreen(
    navController: NavController,
    viewModel: AsistentePanelViewModel
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

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
            Text("Resumen", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = inkText)

            EstadoAsignacion.entries.forEach { estado ->
                val cantidad = asignaciones.count { it.estado == estado }
                Text("${estado.etiqueta}: $cantidad", fontSize = 14.sp, color = inkText)
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
        EstadoAsignacion.PENDIENTE -> Color.Gray to grayLight
        EstadoAsignacion.EN_DESARROLLO -> coralColor to coralLight
        EstadoAsignacion.FINALIZADA -> okColor to okLight
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
                    color = inkText
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
                color = inkText
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

            Surface(color = tealLight, shape = MaterialTheme.shapes.small) {
                Text(
                    text = "Código: ${asignacion.codigo}",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = tealColor,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (asignacion.observacion.isBlank()) "Sin observación" else asignacion.observacion,
                fontSize = 14.sp,
                color = if (asignacion.observacion.isBlank()) Color.Gray else inkText
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onObservacion,
                modifier = Modifier.heightIn(min = 48.dp)
            ) {
                Text(
                    text = if (asignacion.observacion.isBlank()) "Agregar observación" else "Editar observación",
                    color = inkText,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun Dato(etiqueta: String, valor: String) {
    Column {
        Text(text = valor, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = inkText)
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
    val colorError = Color(0xFFB3261E)

    AlertDialog(
        containerColor = creamBg,
        titleContentColor = inkText,
        textContentColor = inkText,
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
                        Text(
                            text = error ?: "${texto.length}/${AsignacionReglas.MAX_OBSERVACION}",
                            color = if (error != null) colorError else inkText
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = inkText,
                        unfocusedTextColor = inkText,
                        errorTextColor = inkText
                    ),
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onGuardar,
                colors = ButtonDefaults.buttonColors(
                    containerColor = tealColor,
                    contentColor = Color.White
                )
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCerrar) {
                Text("Cancelar", color = tealColor)
            }
        }
    )
}



private fun formatoFecha(millis: Long): String =
    SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date(millis))
