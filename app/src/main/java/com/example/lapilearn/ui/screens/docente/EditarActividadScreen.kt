
package com.example.lapilearn.ui.screens.docente

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lapilearn.ui.component.Cabecera
import com.example.lapilearn.ui.components.CampoTexto
import com.example.lapilearn.viewModels.ActividadViewModel

@Composable
fun EditarActividadScreen(
    navController: NavController,
    viewModel: ActividadViewModel
) {
    val actividad = viewModel.obtenerSeleccionada() ?: return

    var modulo by remember { mutableStateOf(actividad.modulo) }
    var curso by remember { mutableStateOf(actividad.curso) }
    var dificultad by remember { mutableStateOf(actividad.dificultad) }
    var objetivo by remember { mutableStateOf(actividad.objetivo) }
    var intentos by remember { mutableStateOf(actividad.intentos) }
    var estado by remember { mutableStateOf(actividad.estado) }

    var mostrarModulos by remember { mutableStateOf(false) }
    var mostrarCursos by remember { mutableStateOf(false) }
    var mostrarDificultades by remember { mutableStateOf(false) }

    val tealColor = Color(0xFF2C6E7F)
    val inkText = Color(0xFF22303A)
    val lineColor = Color(0xFFE5D8C7)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Cabecera("Editar actividad")

        Spacer(modifier = Modifier.height(12.dp))

        // SELECCIONAR MÓDULO

        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { mostrarModulos = !mostrarModulos },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = Color.Gray
                ),
                border = BorderStroke(1.dp, lineColor)
            ) {
                Text(
                    text = "Módulo: $modulo",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (mostrarModulos) {
                for (opcion in listOf("Matemática", "Lectura")) {
                    OutlinedButton(
                        onClick = {
                            modulo = opcion
                            mostrarModulos = false
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = Color.Gray
                        ),
                        border = BorderStroke(1.dp, lineColor)
                    ) {
                        Text(opcion)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // SELECCIONAR CURSO

        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { mostrarCursos = !mostrarCursos },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = Color.Gray
                ),
                border = BorderStroke(1.dp, lineColor)
            ) {
                Text(
                    text = "Curso recomendado: $curso",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (mostrarCursos) {
                for (opcion in listOf(
                    "1° básico",
                    "2° básico",
                    "3° básico",
                    "4° básico"
                )) {
                    OutlinedButton(
                        onClick = {
                            curso = opcion
                            mostrarCursos = false
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = Color.Gray
                        ),
                        border = BorderStroke(1.dp, lineColor)
                    ) {
                        Text(opcion)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // SELECCIONAR DIFICULTAD

        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = {
                    mostrarDificultades = !mostrarDificultades
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = Color.Gray
                ),
                border = BorderStroke(1.dp, lineColor)
            ) {
                Text(
                    text = "Dificultad: $dificultad",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (mostrarDificultades) {
                for (opcion in listOf(
                    "Fácil",
                    "Media",
                    "Avanzada"
                )) {
                    OutlinedButton(
                        onClick = {
                            dificultad = opcion
                            mostrarDificultades = false
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = Color.Gray
                        ),
                        border = BorderStroke(1.dp, lineColor)
                    ) {
                        Text(opcion)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        CampoTexto(
            valor = objetivo,
            etiqueta = "Objetivo",
            onChange = { objetivo = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        CampoTexto(
            valor = intentos,
            etiqueta = "Intentos",
            onChange = { intentos = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Estado de la Actividad",
            fontSize = 12.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (opcion in listOf(
                "Borrador",
                "Disponible",
                "Desactivar"
            )) {
                val seleccionado = estado == opcion

                Button(
                    onClick = { estado = opcion },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            if (seleccionado) tealColor else Color.White,
                        contentColor =
                            if (seleccionado) Color.White else inkText
                    )
                ) {
                    Text(
                        text = opcion,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // GUARDAR CAMBIOS

        Button(
            onClick = {
                viewModel.actualizar(
                    actividad.copy(
                        modulo = modulo,
                        curso = curso,
                        dificultad = dificultad,
                        objetivo = objetivo,
                        intentos = intentos,
                        estado = estado
                    )
                )

                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = tealColor
            )
        ) {
            Text(
                text = "Guardar cambios",
                fontWeight = FontWeight.Bold
            )
        }
    }
}
