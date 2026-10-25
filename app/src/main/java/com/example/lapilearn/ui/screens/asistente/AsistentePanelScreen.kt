package com.example.lapilearn.ui.screens.asistente

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.lapilearn.data.local.entity.ActividadEntity
import com.example.lapilearn.model.AsistentePanelUiState
import com.example.lapilearn.navigation.Screen
import com.example.lapilearn.ui.component.Cabecera
import com.example.lapilearn.ui.components.CampoTexto
import com.example.lapilearn.ui.theme.coralColor
import com.example.lapilearn.ui.theme.coralLight
import com.example.lapilearn.ui.theme.creamBg
import com.example.lapilearn.ui.theme.inkText
import com.example.lapilearn.ui.theme.lineColor
import com.example.lapilearn.ui.theme.okColor
import com.example.lapilearn.ui.theme.okLight
import com.example.lapilearn.ui.theme.tealColor
import com.example.lapilearn.ui.theme.tealLight
import com.example.lapilearn.viewModels.AsistentePanelViewModel
import com.example.lapilearn.viewModels.AsistenteViewModel

/** Panel de la asistente: elige módulo, curso y dificultad, y asigna actividades autorizadas. */
@Composable
fun AsistentePanelScreen(
    navController: NavController,
    viewModel: AsistentePanelViewModel,
    asistenteViewModel: AsistenteViewModel,
    onCerrarSesion: () -> Unit,
    usuarioClave: String? = null
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val sesion by asistenteViewModel.uiState.collectAsStateWithLifecycle()
    val responsable = sesion.nombre.ifBlank { "Asistente de aula" }

    Scaffold(
        containerColor = creamBg,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Cabecera(
                    texto = "Asignar actividades",
                    subtitulo = "Perfil: Asistente de aula",
                    usuarioClave = usuarioClave,
                    onCerrarSesion = onCerrarSesion
                )
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = { navController.navigate(Screen.AsistenteResultados.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = tealColor)
                ) {
                    Text(
                        text = "Ver resultados (${estado.asignaciones.size})",
                        fontWeight = FontWeight.Bold
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
            item {
                Filtros(
                    estado = estado,
                    onModulo = viewModel::onModuloChange,
                    onCurso = viewModel::onCursoChange,
                    onDificultad = viewModel::onDificultadChange,
                    onLimpiar = viewModel::limpiarFiltros
                )
            }

            if (estado.actividades.isEmpty()) {
                item {
                    Text(
                        text = "No hay actividades disponibles con esos filtros.",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
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

    // Diálogo para asignar: alias ficticio -> código temporal
    val seleccionada = estado.actividadSeleccionada
    if (seleccionada != null) {
        DialogoAsignar(
            actividad = seleccionada,
            alias = estado.alias,
            errorAlias = estado.errorAlias,
            guardando = estado.guardando,
            codigoGenerado = estado.codigoGenerado,
            onAliasChange = viewModel::onAliasChange,
            onConfirmar = { viewModel.confirmarAsignacion(responsable) },
            onCerrar = viewModel::cerrarAsignacion
        )
    }
}

@Composable
private fun Filtros(
    estado: AsistentePanelUiState,
    onModulo: (String) -> Unit,
    onCurso: (String) -> Unit,
    onDificultad: (String) -> Unit,
    onLimpiar: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Tú eliges el módulo, el curso y la dificultad: la app no lo decide por el estudiante.",
            fontSize = 14.sp,
            color = Color.Gray
        )

        // Módulo
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AsistentePanelViewModel.MODULOS.forEach { modulo ->
                val esLectura = modulo == "Lectura"
                FiltroBoton(
                    texto = modulo,
                    seleccionado = estado.modulo == modulo,
                    colorActivo = if (esLectura) tealColor else coralColor,
                    colorSuave = if (esLectura) tealLight else coralLight,
                    modifier = Modifier.weight(1f),
                    onClick = { onModulo(modulo) }
                )
            }
        }

        // Curso
        Text("Curso", fontSize = 12.sp, color = Color.Gray)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AsistentePanelViewModel.CURSOS.forEach { curso ->
                FiltroBoton(
                    texto = curso.substringBefore(" "),
                    seleccionado = estado.curso == curso,
                    colorActivo = tealColor,
                    colorSuave = Color.White,
                    modifier = Modifier.weight(1f),
                    onClick = { onCurso(curso) }
                )
            }
        }

        // Dificultad
        Text("Dificultad", fontSize = 12.sp, color = Color.Gray)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AsistentePanelViewModel.DIFICULTADES.forEach { dificultad ->
                FiltroBoton(
                    texto = dificultad,
                    seleccionado = estado.dificultad == dificultad,
                    colorActivo = tealColor,
                    colorSuave = Color.White,
                    modifier = Modifier.weight(1f),
                    onClick = { onDificultad(dificultad) }
                )
            }
        }

        if (estado.hayFiltros) {
            TextButton(onClick = onLimpiar) {
                Text("Quitar filtros", color = tealColor)
            }
        }
    }
}

@Composable
private fun FiltroBoton(
    texto: String,
    seleccionado: Boolean,
    colorActivo: Color,
    colorSuave: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 48.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
        border = if (seleccionado) null else BorderStroke(1.dp, lineColor),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (seleccionado) colorActivo else colorSuave,
            contentColor = if (seleccionado) Color.White else colorActivo
        )
    ) {
        Text(text = texto, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
private fun ActividadAsignableCard(
    actividad: ActividadEntity,
    onAsignar: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = actividad.titulo,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = inkText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${actividad.modulo} · ${actividad.objetivo} · ${actividad.curso}",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(color = okLight, shape = MaterialTheme.shapes.small) {
                    Text(
                        text = actividad.estado,
                        color = okColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Text(
                    text = "${actividad.dificultad} · ${actividad.intentosPermitidos} intentos",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onAsignar,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = tealColor)
            ) {
                Text("Asignar a un estudiante", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun DialogoAsignar(
    actividad: ActividadEntity,
    alias: String,
    errorAlias: String?,
    guardando: Boolean,
    codigoGenerado: String?,
    onAliasChange: (String) -> Unit,
    onConfirmar: () -> Unit,
    onCerrar: () -> Unit
) {
    if (codigoGenerado == null) {
        AlertDialog(
            containerColor = creamBg,
            titleContentColor = inkText,
            textContentColor = inkText,
            onDismissRequest = { if (!guardando) onCerrar() },
            title = { Text("Asignar \"${actividad.titulo}\"") },
            text = {
                Column {
                    Text(
                        text = "Escribe un alias ficticio (no uses nombres reales). " +
                            "Luego se genera un código temporal para el estudiante.",
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
                    enabled = !guardando,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = tealColor,
                        contentColor = Color.White
                    )                ) {
                    Text(if (guardando) "Guardando…" else "Generar código")
                }
            },
            dismissButton = {
                TextButton(onClick = onCerrar, enabled = !guardando) {
                    Text("Cancelar", color = tealColor)
                }            }
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
                    Surface(color = tealLight, shape = MaterialTheme.shapes.medium) {
                        Text(
                            text = codigoGenerado,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = tealColor,
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
                    colors = ButtonDefaults.buttonColors(containerColor = tealColor)
                ) { Text("Listo") }
            }
        )
    }
}
