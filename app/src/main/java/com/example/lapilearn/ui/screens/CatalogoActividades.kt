package com.example.lapilearn.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

    Scaffold(
        containerColor = creamBg,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .border(width = 1.dp, color = lineColor)
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Perfil: Docente / UTP",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Catálogo de actividades",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = inkText
                )
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .border(width = 1.dp, color = lineColor)
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = tealColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("+ Nueva actividad", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Fila de Filtros
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SuggestionChip(
                        onClick = { },
                        label = { Text("Lectura", color = tealColor, fontWeight = FontWeight.Bold) },
                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = tealLight),
                        border = null
                    )
                    SuggestionChip(
                        onClick = { },
                        label = { Text("Matemática", color = coralColor, fontWeight = FontWeight.Bold) },
                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = coralLight),
                        border = null
                    )
                }

                OutlinedButton(
                    onClick = {},
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("📋 Curso", color = Color.Gray, fontSize = 12.sp)
                }
            }

            // Lista de Tarjetas (Cards)
            ActividadCard(
                titulo = "El bosque encantado",
                descripcion = "Lectura · comprensión implícita · 2° básico",
                estado = "Disponible",
                estadoColor = okColor,
                estadoBg = okLight,
                lineColor = lineColor
            )

            ActividadCard(
                titulo = "Sumas hasta 100",
                descripcion = "Matemática · operatoria · 2° básico",
                estado = "Borrador",
                estadoColor = Color.Gray,
                estadoBg = Color(0xFFF3EFE4),
                lineColor = lineColor
            )

            ActividadCard(
                titulo = "Vocabulario: animales",
                descripcion = "Lectura · vocabulario · 1° básico",
                estado = "Disponible",
                estadoColor = okColor,
                estadoBg = okLight,
                lineColor = lineColor
            )
        }
    }
}

// Componente reutilizable para las tarjetas del catálogo
@Composable
fun ActividadCard(
    titulo: String,
    descripcion: String,
    estado: String,
    estadoColor: Color,
    estadoBg: Color,
    lineColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder(true).copy(brush = androidx.compose.ui.graphics.SolidColor(lineColor)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = titulo, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF22303A))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = descripcion, fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = estadoBg,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = estado,
                        color = estadoColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                OutlinedButton(
                    onClick = {},
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Text("Editar", color = Color(0xFF22303A), fontSize = 12.sp)
                }
            }
        }
    }
}