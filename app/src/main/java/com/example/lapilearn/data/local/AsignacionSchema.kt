package com.example.lapilearn.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/** Migración v2 -> v3: asignaciones por estudiante ficticio con código temporal. */
object AsignacionSchema {

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("""CREATE TABLE IF NOT EXISTS asignaciones (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, aliasEstudiante TEXT NOT NULL, actividadId INTEGER NOT NULL, codigo TEXT NOT NULL, asistente TEXT NOT NULL, creadaEn INTEGER NOT NULL, observacion TEXT NOT NULL, FOREIGN KEY(actividadId) REFERENCES actividades(id) ON UPDATE NO ACTION ON DELETE RESTRICT)""")
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_asignaciones_codigo ON asignaciones(codigo)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_asignaciones_actividadId ON asignaciones(actividadId)")

            // Los intentos pasan a pertenecer a una asignación (columna nueva, puede ser NULL).
            db.execSQL("ALTER TABLE intentos ADD COLUMN asignacionId INTEGER REFERENCES asignaciones(id) ON UPDATE NO ACTION ON DELETE RESTRICT")
            db.execSQL("DROP INDEX IF EXISTS index_intentos_actividadId_numero")
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_intentos_asignacionId_numero ON intentos(asignacionId, numero)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_intentos_actividadId ON intentos(actividadId)")

            sembrar(db)

            // Intentos que ya existían en v2: se ligan a la asignación de ejemplo de su actividad.
            db.execSQL("UPDATE intentos SET asignacionId = (SELECT a.id FROM asignaciones a WHERE a.actividadId = intentos.actividadId ORDER BY a.id LIMIT 1) WHERE asignacionId IS NULL")
        }
    }

    /**
     * Asignaciones de ejemplo (alias ficticios). Usan los mismos códigos que antes
     * (BOSQ01, SUMA01, ANIM01) para que las pruebas del estudiante sigan funcionando.
     */
    fun sembrar(db: SupportSQLiteDatabase) {
        fun asignacion(alias: String, actividadId: Int, codigo: String) {
            db.execSQL(
                "INSERT INTO asignaciones (aliasEstudiante, actividadId, codigo, asistente, creadaEn, observacion) " +
                    "SELECT ?, id, ?, ?, ?, '' FROM actividades WHERE id = ?",
                arrayOf<Any>(alias, codigo, "Asistente Prueba", System.currentTimeMillis(), actividadId)
            )
        }
        asignacion("Estudiante A", 1, "BOSQ01")
        asignacion("Estudiante B", 2, "SUMA01")
        asignacion("Estudiante C", 3, "ANIM01")
    }
}
