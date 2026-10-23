
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
import com.example.lapilearn.model.Actividad
import com.example.lapilearn.navigation.Screen
import com.example.lapilearn.ui.component.Cabecera
import com.example.lapilearn.ui.components.CampoTexto
import com.example.lapilearn.ui.theme.inkText
import com.example.lapilearn.ui.theme.lineColor
import com.example.lapilearn.ui.theme.tealColor
import com.example.lapilearn.viewModels.ActividadViewModel

@Composable
fun NuevaActividadScreen(
    navController: NavController,
    viewModel: ActividadViewModel
) {

    var titulo by remember { mutableStateOf("") }
    var modulo by remember { mutableStateOf("Lectura") }
    var curso by remember { mutableStateOf("1° básico") }
    var dificultad by remember { mutableStateOf("Fácil") }
    var objetivo by remember { mutableStateOf("") }
    var instrucciones by remember { mutableStateOf("") }
    var intentos by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("Borrador") }
    var actividadGuardada by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }

    var mostrarModulos by remember { mutableStateOf(false) }
    var mostrarCursos by remember { mutableStateOf(false) }
    var mostrarDificultades by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        Cabecera("Nueva actividad")

        Spacer(modifier = Modifier.height(12.dp))

        // TÍTULO

        CampoTexto(
            valor = titulo,
            etiqueta = "Título",
            onChange = { titulo = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // SELECCIONAR MÓDULO

        Column(modifier = Modifier.fillMaxWidth()) {

            OutlinedButton(
                onClick = {
                    mostrarModulos = !mostrarModulos
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
                onClick = {
                    mostrarCursos = !mostrarCursos
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

        // OBJETIVO

        CampoTexto(
            valor = objetivo,
            etiqueta = "Objetivo",
            onChange = { objetivo = it }
        )

        Spacer(modifier = Modifier.height(8.dp))


        CampoTexto(
            valor = instrucciones,
            etiqueta = "Instrucciones",
            onChange = { instrucciones = it }
        )
        Spacer(modifier = Modifier.height(8.dp))

        // INTENTOS

        CampoTexto(
            valor = intentos,
            etiqueta = "Intentos",
            onChange = { intentos = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ESTADO DE LA ACTIVIDAD

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
                    onClick = {
                        estado = opcion
                    },
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

        // GUARDAR ACTIVIDAD

        Button(
            onClick = {

                if (titulo.isNotBlank() &&
                    objetivo.isNotBlank() &&
                    intentos.isNotBlank()
                ) {

                    mensaje =  "Botón Guardar presionado"

                    val nuevaActividad = Actividad(
                        id = viewModel.nuevoId(),
                        titulo = titulo,
                        modulo = modulo,
                        curso = curso,
                        dificultad = dificultad,
                        objetivo = objetivo,
                        intentos = intentos,
                        estado = estado,
                        instrucciones = instrucciones
                    )
                    viewModel.agregar(nuevaActividad) {
                        actividadGuardada = true
                        mensaje = "Actividad guardada correctamente."
                    }

                } else {
                    mensaje = ""
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = tealColor
            )
        ) {
            Text(
                text = "Guardar actividad",
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // MENSAJES

        if (mensaje.isNotBlank()) {
            Text(
                text = mensaje,
                color = if (actividadGuardada) tealColor else Color.Red
            )
        }

        if (viewModel.errorGuardado != null) {
            Text(
                text = viewModel.errorGuardado ?: "",
                color = Color.Red
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al catálogo")
        }
    }
}
