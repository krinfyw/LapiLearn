package com.example.lapilearn.data

/**
 * Cuentas protegidas de PRUEBA (ficticias) para el MVP académico.
 * No son credenciales reales de la escuela.
 */
object CuentasDePrueba {
    const val ASISTENTE_CORREO = "asistente@lapilearn.cl"
    const val ASISTENTE_CONTRASENA = "asistente123"
    const val ASISTENTE_ALIAS = "Asistente de prueba"

    fun credencialesAsistenteValidas(correo: String, contrasena: String): Boolean =
        correo.trim().equals(ASISTENTE_CORREO, ignoreCase = true) &&
            contrasena == ASISTENTE_CONTRASENA
}
