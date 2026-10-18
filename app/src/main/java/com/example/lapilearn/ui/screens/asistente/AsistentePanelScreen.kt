package com.example.lapilearn.ui.screens.asistente

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.lapilearn.model.Actividad
import com.example.lapilearn.model.Dificultad
import com.example.lapilearn.model.Modulo
import com.example.lapilearn.navigation.Screen
import com.example.lapilearn.ui.components.CampoTexto
import com.example.lapilearn.viewModels.AsistentePanelViewModel

// Paleta compartida con el resto de la app
private val CreamBg = Color(0xFFFBF8F2)
private val InkText = Color(0xFF22303A)
private val TealColor = Color(0xFF2C6E7F)
private val TealLight = Color(0xFFE4F0F0)
private val CoralColor = Color(0xFFE8804A)
private val CoralLight = Color(0xFFFCEADF)
private val LineColor = Color(0xFFE1D9C8)

fun etiquetaCurso(curso: Int): String = "${curso}° básico"

/** Panel de la asistente: elige módulo, curso y dificultad y asigna actividades autorizadas. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsistentePanelScreen(
    navController: NavController,
    viewModel: AsistentePanelViewModel,
    onCerrarSesion: () -> Unit
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
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Perfil: Asistente de aula", fontSize = 12.sp, color = Color.Gray)
                    Text(
                        text = "Asignar actividades",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = InkText
                    )
                }
                TextButton(onClick = onCerrarSesion) {
                    Text("Cerrar sesión", color = TealColor)
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .border(width = 1.dp, color = LineColor)
                    .padding(16.dp)
            ) {
                Button(
                    onClick = { navController.navigate(Screen.AsistenteResultados.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Ver resultados (${estado.asignaciones.size})", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Filtros(
                    modulo = estado.modulo,
                    curso = estado.curso,
                    dificultad = estado.dificultad,
                    onModulo = viewModel::onModuloChange,
                    onCurso = viewModel::onCursoChange,
                    onDificultad = viewModel::onDificultadChange
                )
            }

            if (estado.actividades.isEmpty()) {
                item {
                    EstadoVacio(onLimpiar = viewModel::limpiarFiltros)
                }
            } else {
                items(estado.actividades, key = { it.id }) { actividad ->
                    ActividadAsignableCard(
                        actividad = actividad,
                        onAsignar = { viewModel.abrirAsignacion(actividad) }
                    )
                }
            }
        }
    }

    // Diálogo para asignar (alias ficticio -> código temporal)
    val seleccionada = estado.actividadSeleccionada
    if (seleccionada != null) {
        DialogoAsignar(
            actividad = seleccionada,
            alias = estado.alias,
            errorAlias = estado.errorAlias,
            codigoGenerado = estado.codigoGenerado,
            onAliasChange = viewModel::onAliasChange,
            onConfirmar = viewModel::confirmarAsignacion,
            onCerrar = viewModel::cerrarAsignacion
        )
    }
}

@Composable
private fun Filtros(
    modulo: Modulo?,
    curso: Int?,
    dificultad: Dificultad?,
    onModulo: (Modulo) -> Unit,
    onCurso: (Int) -> Unit,
    onDificultad: (Dificultad) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Tú eliges el nivel: la app no lo decide por el estudiante.",
            fontSize = 14.sp,
            color = Color.Gray
        )

        FiltroFila(titulo = "Módulo") {
            Modulo.entries.forEach {
                FilterChip(
                    selected = modulo == it,
                    onClick = { onModulo(it) },
                    label = { Text(it.etiqueta, fontSize = 14.sp) }
                )
            }
        }
        FiltroFila(titulo = "Curso") {
            (1..4).forEach {
                FilterChip(
                    selected = curso == it,
                    onClick = { onCurso(it) },
                    label = { Text(etiquetaCurso(it), fontSize = 14.sp) }
                )
            }
        }
        FiltroFila(titulo = "Dificultad") {
            Dificultad.entries.forEach {
                FilterChip(
                    selected = dificultad == it,
                    onClick = { onDificultad(it) },
                    label = { Text(it.etiqueta, fontSize = 14.sp) }
                )
            }
        }
    }
}

@Composable
private fun FiltroFila(titulo: String, contenido: @Composable () -> Unit) {
    Column {
        Text(text = titulo, fontSize = 12.sp, color = Color.Gray)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            contenido()
        }
    }
}

@Composable
private fun EstadoVacio(onLimpiar: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No hay actividades disponibles con esos filtros.",
            fontSize = 16.sp,
            color = InkText,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(onClick = onLimpiar) { Text("Quitar filtros") }
    }
}

@Composable
private fun ActividadAsignableCard(actividad: Actividad, onAsignar: () -> Unit) {
    val esLectura = actividad.modulo == Modulo.LECTURA
    val colorModulo = if (esLectura) TealColor else CoralColor
    val fondoModulo = if (esLectura) TealLight else CoralLight

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineColor),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(color = fondoModulo, shape = RoundedCornerShape(12.dp)) {
                    Text(
                        text = actividad.modulo.etiqueta,
                        color = colorModulo,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Text(
                    text = "${etiquetaCurso(actividad.curso)} · ${actividad.dificultad.etiqueta}",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = actividad.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = InkText)
            Text(text = actividad.contenido, fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = actividad.objetivo, fontSize = 15.sp, color = InkText)

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${actividad.cantidadPreguntas} preguntas · ${actividad.intentosPermitidos} intentos permitidos",
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onAsignar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Asignar a un estudiante", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun DialogoAsignar(
    actividad: Actividad,
    alias: String,
    errorAlias: String?,
    codigoGenerado: String?,
    onAliasChange: (String) -> Unit,
    onConfirmar: () -> Unit,
    onCerrar: () -> Unit
) {
    if (codigoGenerado == null) {
        AlertDialog(
            onDismissRequest = onCerrar,
            title = { Text("Asignar \"${actividad.nombre}\"") },
            text = {
                Column {
                    Text(
                        text = "Escribe un alias ficticio (no uses nombres reales). " +
                            "Luego se generará un código temporal para el estudiante.",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CampoTexto(
                        valor = alias,
                        etiqueta = "Alias del estudiante",
                        onChange = onAliasChange,
                        error = errorAlias
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = onConfirmar,
                    colors = ButtonDefaults.buttonColors(containerColor = TealColor)
                ) { Text("Generar código") }
            },
            dismissButton = { TextButton(onClick = onCerrar) { Text("Cancelar") } }
        )
    } else {
        AlertDialog(
            onDismissRequest = onCerrar,
            title = { Text("¡Actividad asignada!") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Entrega este código a ${alias.trim()}:",
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(color = TealLight, shape = RoundedCornerShape(12.dp)) {
                        Text(
                            text = codigoGenerado,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = TealColor,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "El estudiante lo escribe en \"Estudiante (código)\" en la pantalla de inicio.",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = onCerrar,
                    colors = ButtonDefaults.buttonColors(containerColor = TealColor)
                ) { Text("Listo") }
            }
        )
    }
}
