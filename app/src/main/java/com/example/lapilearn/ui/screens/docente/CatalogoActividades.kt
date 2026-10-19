package com.example.lapilearn.ui.screens.docente

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoActividades(navController: NavController) {
    val creamBg = Color(0xFFFBF8F2)
    val inkText = Color(0xFF22303A)
    val tealColor = Color(0xFF2C6E7F)
    val tealLight = Color(0xFFE4F0F0)
    val coralColor = Color(0xFFE8804A)
    val coralLight = Color(0xFFFCEADF)
    val okColor = Color(0xFF4E9F6B)
    val okLight = Color(0xFFE5F5EB)
    val lineColor = Color(0xFFE1D9C8)

    var cursoExpandido by remember { mutableStateOf(false) }
    var cursoSeleccionado by remember { mutableStateOf<String?>(null) }
    val cursos = listOf("1° básico", "2° básico", "3° básico", "4° básico")

    Scaffold(
        containerColor = creamBg,
        topBar = {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Perfil: Docente / UTP", fontSize = 12.sp, color = Color.Gray)
                Text(
                    text = "Catálogo de actividades",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = inkText
                )
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = lineColor)
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
            // Fila de filtros
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(containerColor = tealLight)
                        ) {
                            Text("Lectura", color = tealColor, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(containerColor = coralLight)
                        ) {
                            Text("Matemática", color = coralColor, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Selector de curso: el botón y la lista quedan alineados a la derecha
                    Column(horizontalAlignment = Alignment.End) {
                        Button(
                            onClick = { cursoExpandido = !cursoExpandido },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color.Gray
                            )
                        ) {
                            Text(text = "📋 ${cursoSeleccionado ?: "Curso"}", fontSize = 12.sp)
                        }

                        if (cursoExpandido) {
                            for (curso in cursos) {
                                Button(
                                    onClick = {
                                        cursoSeleccionado = curso
                                        cursoExpandido = false
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = if (curso == cursoSeleccionado) tealColor else inkText
                                    )
                                ) {
                                    Text(
                                        text = curso,
                                        fontWeight = if (curso == cursoSeleccionado) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Tarjetas
            item {
                ActividadCard(
                    titulo = "El bosque encantado",
                    descripcion = "Lectura · comprensión implícita · 2° básico",
                    estado = "Disponible",
                    estadoColor = okColor,
                    estadoBg = okLight
                )
            }

            item {
                ActividadCard(
                    titulo = "Sumas hasta 100",
                    descripcion = "Matemática · operatoria · 2° básico",
                    estado = "Borrador",
                    estadoColor = Color.Gray,
                    estadoBg = Color(0xFFF3EFE4)
                )
            }

            item {
                ActividadCard(
                    titulo = "Vocabulario: animales",
                    descripcion = "Lectura · vocabulario · 1° básico",
                    estado = "Disponible",
                    estadoColor = okColor,
                    estadoBg = okLight
                )
            }

            // Botón de nueva actividad (antes en bottomBar)
            item {
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = tealColor)
                ) {
                    Text("+ Nueva actividad", fontWeight = FontWeight.Bold)
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
    estadoBg: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = titulo,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF22303A)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = descripcion, fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(color = estadoBg, shape = MaterialTheme.shapes.small) {
                    Text(
                        text = estado,
                        color = estadoColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF22303A)
                    )
                ) {
                    Text("Editar", fontSize = 12.sp)
                }
            }
        }
    }
}