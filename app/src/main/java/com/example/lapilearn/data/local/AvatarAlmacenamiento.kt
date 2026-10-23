package com.example.lapilearn.data.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.AtomicFile
import java.io.File
import java.io.FileNotFoundException
import kotlin.math.roundToInt

object AvatarAlmacenamiento {
    private fun archivo(context: Context, clave: String): AtomicFile {
        require(clave.matches(Regex("(docente|asistente)_[0-9]+"))) {
            "La cuenta no es válida."
        }
        val carpeta = File(context.filesDir, "avatares")
        check(carpeta.isDirectory || carpeta.mkdirs()) {
            "No se pudo abrir el almacenamiento interno."
        }
        return AtomicFile(File(carpeta, "$clave.png"))
    }

    fun cargar(context: Context, clave: String): Bitmap? {
        return try {
            archivo(context, clave).openRead().use { entrada ->
                BitmapFactory.decodeStream(entrada)
            }
        } catch (e: FileNotFoundException) {
            null // Todavía no se ha elegido una foto para esta cuenta.
        }
    }

    fun guardar(context: Context, clave: String, uri: Uri): Bitmap {
        val origen = ImageDecoder.createSource(context.contentResolver, uri)
        val imagen = ImageDecoder.decodeBitmap(origen) { decoder, info, _ ->
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            val ladoMayor = maxOf(info.size.width, info.size.height)
            if (ladoMayor > 512) {
                val escala = 512f / ladoMayor
                decoder.setTargetSize(
                    (info.size.width * escala).roundToInt().coerceAtLeast(1),
                    (info.size.height * escala).roundToInt().coerceAtLeast(1)
                )
            }
        }
        val destino = archivo(context, clave)
        val salida = destino.startWrite()
        try {
            check(imagen.compress(Bitmap.CompressFormat.PNG, 100, salida)) {
                "No se pudo guardar la foto."
            }
            destino.finishWrite(salida)
        } catch (e: Exception) {
            destino.failWrite(salida)
            throw e
        }
        return imagen
    }
}
