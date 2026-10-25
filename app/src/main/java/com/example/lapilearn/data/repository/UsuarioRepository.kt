package com.example.lapilearn.data.repository

import android.content.Context
import com.example.lapilearn.data.local.AppDatabase

class UsuarioRepository(private val db: AppDatabase) {
    constructor(context: Context) : this(AppDatabase.getInstance(context.applicationContext))

    suspend fun loginDocente(correo: String, contrasena: String) =
        db.docenteDao().login(correo, contrasena)

    suspend fun loginAsistente(correo: String, contrasena: String) =
        db.asistenteDao().login(correo, contrasena)
}
