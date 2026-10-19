
package com.example.lapilearn.ui.screens.docente

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lapilearn.viewModels.ActividadViewModel

@Composable
fun ProgresionScreen(
    navController: NavController,
    viewModel: ActividadViewModel
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Perfil: Docente / UTP",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Progresión · 2° básico",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF263641)
            )
        }

        HorizontalDivider(
            color = Color(0xFFE5D8C7)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(20.dp)
        ) {

            Text(
                text = "Lectura",
                color = Color(0xFF28758A),
                modifier = Modifier
                    .background(
                        Color(0xFFE5F1F2),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(
                        horizontal = 14.dp,
                        vertical = 6.dp
                    )
            )

            Spacer(modifier = Modifier.height(12.dp))

            TarjetaProgresion(
                titulo = "1. Vocabulario: animales",
                dificultad = "Fácil",
                onClick = {
                    viewModel.dificultadSeleccionada = "Fácil"
                    navController.popBackStack()
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            TarjetaProgresion(
                titulo = "2. El bosque encantado",
                dificultad = "Media",
                onClick = {
                    viewModel.dificultadSeleccionada = "Media"
                    navController.popBackStack()
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            TarjetaProgresion(
                titulo = "3. Comprensión: la huerta",
                dificultad = "Avanzada",
                onClick = {
                    viewModel.dificultadSeleccionada = "Avanzada"
                    navController.popBackStack()
                }
            )
        }

        HorizontalDivider(
            color = Color(0xFFE5D8C7)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            OutlinedButton(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Reordenar secuencia",
                    color = Color(0xFF263641),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun TarjetaProgresion(
    titulo: String,
    dificultad: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(
            1.dp,
            Color(0xFFE5D8C7)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = titulo,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF263641)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Dificultad: $dificultad",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}
