package com.example.lapilearn.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object ActividadSchema {
    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Mantener la secuencia incluso si se habían eliminado los últimos registros.
            val secuencia = db.query("SELECT seq FROM sqlite_sequence WHERE name = 'actividades'").use {
                if (it.moveToFirst()) it.getLong(0) else 0L
            }

            // Las tablas hijas siguen apuntando a los mismos ID durante el reemplazo.
            db.execSQL("PRAGMA defer_foreign_keys = ON")
            db.execSQL("""CREATE TABLE actividades_nuevas (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, titulo TEXT NOT NULL, modulo TEXT NOT NULL, curso TEXT NOT NULL, dificultad TEXT NOT NULL, objetivo TEXT NOT NULL, intentosPermitidos INTEGER NOT NULL, estado TEXT NOT NULL, instrucciones TEXT NOT NULL)""")
            db.execSQL("""INSERT INTO actividades_nuevas (id,titulo,modulo,curso,dificultad,objetivo,intentosPermitidos,estado,instrucciones) SELECT id,titulo,modulo,curso,dificultad,objetivo,intentosPermitidos,estado,instrucciones FROM actividades""")
            db.execSQL("DROP TABLE actividades")
            db.execSQL("ALTER TABLE actividades_nuevas RENAME TO actividades")
            db.execSQL("UPDATE sqlite_sequence SET seq = MAX(seq, ?) WHERE name = 'actividades'", arrayOf<Any>(secuencia))

            // Verificar las relaciones antes de restablecer la comprobación inmediata.
            db.query("PRAGMA foreign_key_check").use {
                check(!it.moveToFirst()) { "No se pudieron conservar las relaciones de las actividades." }
            }
            db.execSQL("PRAGMA defer_foreign_keys = OFF")
        }
    }
}
