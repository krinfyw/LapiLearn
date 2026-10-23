
package com.example.lapilearn.ui.screens.docente

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.lapilearn.navigation.Screen
import com.example.lapilearn.ui.component.Cabecera
import com.example.lapilearn.ui.theme.coralColor
import com.example.lapilearn.ui.theme.coralLight
import com.example.lapilearn.ui.theme.creamBg
import com.example.lapilearn.ui.theme.inkText
import com.example.lapilearn.ui.theme.okColor
import com.example.lapilearn.ui.theme.okLight
import com.example.lapilearn.ui.theme.tealColor
import com.example.lapilearn.ui.theme.tealLight
import com.example.lapilearn.viewModels.ActividadViewModel

@Composable
fun CatalogoActividades(
    navController: NavController,
    viewModel: ActividadViewModel
) {
    val actividades by viewModel.actividades.collectAsStateWithLifecycle()

    var moduloSeleccionado by remember {
        mutableStateOf("Lectura")
    }
    var cursoSeleccionado by remember {
        mutableStateOf("2° básico")
    }
    var cursoExpandido by remember {
        mutableStateOf(false)
    }
    val cursos = listOf(
        "1° básico",
        "2° básico",
        "3° básico",
        "4° básico"
    )

    // FILTRAR ACTIVIDADES POR MÓDULO Y CURSO
    val actividadesFiltradas = actividades.filter {
        it.modulo == moduloSeleccionado &&
                it.curso == cursoSeleccionado
    }

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
                    texto = "Catálogo de actividades",
                    subtitulo = "Perfil: Docente / UTP"
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // FILTROS DE MÓDULO
            item {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                moduloSeleccionado = "Lectura"
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor =
                                    if (moduloSeleccionado == "Lectura")
                                        tealColor
                                    else tealLight
                            )
                        ) {
                            Text(
                                text = "Lectura",
                                color =
                                    if (moduloSeleccionado == "Lectura")
                                        Color.White
                                    else tealColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Button(
                            onClick = {
                                moduloSeleccionado = "Matemática"
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor =
                                    if (moduloSeleccionado == "Matemática")
                                        coralColor
                                    else coralLight
                            )
                        ) {
                            Text(
                                text = "Matemática",
                                color =
                                    if (moduloSeleccionado == "Matemática")
                                        Color.White
                                    else coralColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    // SELECCIONAR CURSO
                    Button(
                        onClick = {
                            cursoExpandido = !cursoExpandido
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = inkText
                        )
                    ) {
                        Text(
                            text = "📋 Curso: $cursoSeleccionado"
                        )
                    }
                    if (cursoExpandido) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.spacedBy(4.dp)
                        ) {
                            for (curso in cursos) {
                                Button(
                                    onClick = {
                                        cursoSeleccionado = curso
                                        cursoExpandido = false
                                    },
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(
                                        horizontal = 2.dp,
                                        vertical = 8.dp
                                    ),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor =
                                            if (cursoSeleccionado == curso)
                                                tealColor
                                            else Color.White,
                                        contentColor =
                                            if (cursoSeleccionado == curso)
                                                Color.White
                                            else inkText
                                    )
                                ) {
                                    Text(
                                        text = curso,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ACTIVIDADES FILTRADAS
            items(actividadesFiltradas) { actividad ->
                val disponible =
                    actividad.estado == "Disponible"
                ActividadCard(
                    titulo = actividad.titulo,
                    descripcion =
                        "${actividad.modulo} · ${actividad.objetivo} · ${actividad.curso}",
                    estado = actividad.estado,
                    estadoColor =
                        if (disponible) okColor
                        else Color.Gray,
                    estadoBg =
                        if (disponible) okLight
                        else Color(0xFFF3EFE4),
                    onEditarClick = {
                        viewModel.seleccionar(actividad.id)
                        navController.navigate(
                            Screen.EditarActividad.route
                        )
                    }
                )
            }

            // MENSAJE SI NO HAY ACTIVIDADES
            if (actividadesFiltradas.isEmpty()) {
                item {
                    Text(
                        text = "No hay actividades de $moduloSeleccionado para $cursoSeleccionado",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }

            // NUEVA ACTIVIDAD
            item {
                Button(
                    onClick = {
                        navController.navigate(
                            Screen.NuevaActividad.route
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = tealColor
                    )
                ) {
                    Text(
                        text = "+ Nueva actividad",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ActividadCard(
    titulo: String,
    descripcion: String,
    estado: String,
    estadoColor: Color,
    estadoBg: Color,
    onEditarClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = titulo,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF22303A)
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = descripcion,
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = estadoBg,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = estado,
                        color = estadoColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 4.dp
                        )
                    )
                }
                Button(
                    onClick = onEditarClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF22303A)
                    )
                ) {
                    Text(
                        text = "Editar",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
