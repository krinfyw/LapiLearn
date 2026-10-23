package com.example.lapilearn.ui.screens.inicio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lapilearn.navigation.Screen
import com.example.lapilearn.ui.theme.coralColor
import com.example.lapilearn.ui.theme.creamBg
import com.example.lapilearn.ui.theme.inkText
import com.example.lapilearn.ui.theme.tealColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(creamBg)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.wrapContentWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "📖", fontSize = 28.sp)
            Text(text = "➕", fontSize = 28.sp)
            Text(text = "🔢", fontSize = 28.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "MPAZ NEB",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = inkText
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Nivelación en Lectura y Matemática",
            style = MaterialTheme.typography.bodySmall,
            color = inkText.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Escuela Escritora Marcela Paz",
            style = MaterialTheme.typography.bodySmall,
            color = inkText.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Ingresa según tu perfil",
            style = MaterialTheme.typography.bodyMedium,
            color = inkText.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    navController.navigate(Screen.DocenteLogin.route)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(containerColor = tealColor)
            ) {
                Text(text = "Docente / UTP", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    navController.navigate(Screen.AsistenteLogin.route)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(containerColor = coralColor)
            ) {
                Text(text = "Asistente de aula", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = {
                    navController.navigate(Screen.EstudianteIngresoCodigo.route)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text(
                    text = "Estudiante (código)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = inkText
                )
            }
        }
    }
}