package com.example.lapilearn.ui.component

import android.content.ActivityNotFoundException
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.lapilearn.data.local.AvatarAlmacenamiento
import com.example.lapilearn.ui.theme.creamBg
import com.example.lapilearn.ui.theme.inkText
import com.example.lapilearn.ui.theme.tealColor
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AvatarUsuario(
    claveUsuario: String,
    onCerrarSesion: () -> Unit,
    colorContenido: Color = inkText
) {
    val context = LocalContext.current
    val alcance = rememberCoroutineScope()
    var foto by remember(claveUsuario) { mutableStateOf<Bitmap?>(null) }
    var ocupado by remember(claveUsuario) { mutableStateOf(true) }
    var menuAbierto by remember { mutableStateOf(false) }
    var elegirOrigen by remember { mutableStateOf(false) }

    LaunchedEffect(claveUsuario) {
        try {
            foto = withContext(Dispatchers.IO) {
                AvatarAlmacenamiento.cargar(context.applicationContext, claveUsuario)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Toast.makeText(context, "No se pudo cargar el avatar.", Toast.LENGTH_SHORT).show()
        } finally {
            ocupado = false
        }
    }

    val guardarSeleccion: (Uri?) -> Unit = { uri ->
        if (uri != null && !ocupado) {
            ocupado = true
            alcance.launch {
                try {
                    val guardada = withContext(Dispatchers.IO) {
                        AvatarAlmacenamiento.guardar(context.applicationContext, claveUsuario, uri)
                    }
                    foto = guardada
                    Toast.makeText(context, "Foto guardada correctamente.", Toast.LENGTH_SHORT).show()
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "No se pudo abrir o guardar la imagen. Prueba con otra foto.",
                        Toast.LENGTH_LONG
                    ).show()
                } finally {
                    ocupado = false
                }
            }
        }
    }
    val galeria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = guardarSeleccion
    )
    val archivos = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = guardarSeleccion
    )

    Box {
        IconButton(
            onClick = { menuAbierto = true },
            enabled = !ocupado,
            modifier = Modifier.size(48.dp)
        ) {
            val imagen = foto
            if (ocupado) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = tealColor)
            } else if (imagen != null) {
                Image(
                    bitmap = imagen.asImageBitmap(),
                    contentDescription = "Abrir menú de usuario",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(40.dp).clip(CircleShape)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Abrir menú de usuario",
                    tint = colorContenido,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
        DropdownMenu(
            expanded = menuAbierto,
            onDismissRequest = { menuAbierto = false },
            containerColor = creamBg
        ) {
            DropdownMenuItem(
                text = { Text("Cambiar foto", color = inkText) },
                enabled = !ocupado,
                onClick = {
                    menuAbierto = false
                    elegirOrigen = true
                }
            )
            DropdownMenuItem(
                text = { Text("Cerrar sesión", color = inkText) },
                enabled = !ocupado,
                onClick = {
                    menuAbierto = false
                    onCerrarSesion()
                }
            )
        }
    }

    if (elegirOrigen) {
        AlertDialog(
            containerColor = creamBg,
            titleContentColor = inkText,
            textContentColor = inkText,
            onDismissRequest = { elegirOrigen = false },
            title = { Text("Cambiar foto") },
            text = {
                Column {
                    TextButton(onClick = {
                        elegirOrigen = false
                        try {
                            galeria.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        } catch (e: ActivityNotFoundException) {
                            Toast.makeText(context, "No hay un selector de fotos disponible.", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text("Galería", color = tealColor)
                    }
                    TextButton(onClick = {
                        elegirOrigen = false
                        try {
                            archivos.launch(arrayOf("image/*"))
                        } catch (e: ActivityNotFoundException) {
                            Toast.makeText(context, "No hay un selector de archivos disponible.", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text("Archivos", color = tealColor)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { elegirOrigen = false }) {
                    Text("Cancelar", color = tealColor)
                }
            }
        )
    }
}
