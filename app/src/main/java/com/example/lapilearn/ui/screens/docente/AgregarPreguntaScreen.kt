
package com.example.lapilearn.ui.screens.docente

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lapilearn.ui.component.Cabecera
import com.example.lapilearn.ui.components.CampoTexto
import com.example.lapilearn.ui.theme.tealColor
import com.example.lapilearn.viewModels.ActividadViewModel

@Composable
fun AgregarPreguntaScreen(
    navController: NavController,
    viewModel: ActividadViewModel
) {
    val actividad = viewModel.obtenerSeleccionada() ?: return
    var enunciado by remember { mutableStateOf("") }
    var opcionA by remember { mutableStateOf("") }
    var opcionB by remember { mutableStateOf("") }
    var opcionC by remember { mutableStateOf("") }
    var opcionD by remember { mutableStateOf("") }
    var correcta by remember { mutableStateOf(0) }
    var textoApoyo by remember { mutableStateOf("") }
    var retroalimentacion by remember { mutableStateOf("") }
    var mostrarMensaje by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Cabecera("Agregar pregunta")

        Spacer(modifier = Modifier.height(12.dp))

        Text("Actividad: ${actividad.titulo}")

        Spacer(modifier = Modifier.height(16.dp))

        CampoTexto(
            valor = textoApoyo,
            etiqueta = "Texto de apoyo",
            onChange = { textoApoyo = it }
        )
        Spacer(modifier = Modifier.height(8.dp))

        CampoTexto(
            valor = enunciado,
            etiqueta = "Pregunta",
            onChange = { enunciado = it }
        )
        Spacer(modifier = Modifier.height(8.dp))

        CampoTexto(
            valor = opcionA,
            etiqueta = "Alternativa A",
            onChange = { opcionA = it }
        )
        Spacer(modifier = Modifier.height(8.dp))

        CampoTexto(
            valor = opcionB,
            etiqueta = "Alternativa B",
            onChange = { opcionB = it }
        )
        Spacer(modifier = Modifier.height(8.dp))

        CampoTexto(
            valor = opcionC,
            etiqueta = "Alternativa C",
            onChange = { opcionC = it }
        )
        Spacer(modifier = Modifier.height(8.dp))

        CampoTexto(
            valor = opcionD,
            etiqueta = "Alternativa D",
            onChange = { opcionD = it }
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text("Selecciona la respuesta correcta")

        Spacer(modifier = Modifier.height(8.dp))

        for (opcion in 0..3) {
            val letra = listOf("A", "B", "C", "D")[opcion]
            Button(
                onClick = {
                    correcta = opcion
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor =
                        if (correcta == opcion)
                            tealColor
                        else
                            Color.LightGray,
                    contentColor =
                        if (correcta == opcion)
                            Color.White
                        else
                            Color.Black
                )
            ) {
                Text("Alternativa $letra")
            }

            Spacer(modifier = Modifier.height(4.dp))
        }
        Spacer(modifier = Modifier.height(12.dp))

        CampoTexto(
            valor = retroalimentacion,
            etiqueta = "Retroalimentación",
            onChange = { retroalimentacion = it }
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                mostrarMensaje = false
                viewModel.agregarPregunta(
                    enunciado = enunciado,
                    opcionA = opcionA,
                    opcionB = opcionB,
                    opcionC = opcionC,
                    opcionD = opcionD,
                    correcta = correcta,
                    textoApoyo = textoApoyo,
                    retroalimentacion = retroalimentacion
                ) {
                    enunciado = ""
                    opcionA = ""
                    opcionB = ""
                    opcionC = ""
                    opcionD = ""
                    correcta = 0
                    textoApoyo = ""
                    retroalimentacion = ""
                    mostrarMensaje = true
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = tealColor
            ),
            enabled = !viewModel.guardandoPregunta
        ) {
            Text(if (viewModel.guardandoPregunta) "Guardando..." else "Guardar pregunta")
        }
        if (mostrarMensaje) {
            Text(
                text = "Pregunta guardada correctamente.",
                color = tealColor
            )
        }
        viewModel.errorGuardado?.let { error ->
            Text(
                text = error,
                color = Color.Red
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            enabled = !viewModel.guardandoPregunta,
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}
