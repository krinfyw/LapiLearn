package com.example.lapilearn.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/** La misma creación se utiliza para migrar instalaciones v1 y para la prueba de migración. */
object EstudianteSchema {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("""CREATE TABLE IF NOT EXISTS actividades (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, titulo TEXT NOT NULL, modulo TEXT NOT NULL, curso TEXT NOT NULL, dificultad TEXT NOT NULL, objetivo TEXT NOT NULL, intentosPermitidos INTEGER NOT NULL, estado TEXT NOT NULL, codigo TEXT NOT NULL, instrucciones TEXT NOT NULL)""")
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_actividades_codigo ON actividades(codigo)")
            db.execSQL("""CREATE TABLE IF NOT EXISTS preguntas (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, actividadId INTEGER NOT NULL, orden INTEGER NOT NULL, textoApoyo TEXT NOT NULL, enunciado TEXT NOT NULL, opcionA TEXT NOT NULL, opcionB TEXT NOT NULL, opcionC TEXT NOT NULL, opcionD TEXT NOT NULL, correcta INTEGER NOT NULL, retroalimentacion TEXT NOT NULL, FOREIGN KEY(actividadId) REFERENCES actividades(id) ON UPDATE NO ACTION ON DELETE RESTRICT)""")
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_preguntas_actividadId_orden ON preguntas(actividadId, orden)")
            db.execSQL("""CREATE TABLE IF NOT EXISTS intentos (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, actividadId INTEGER NOT NULL, numero INTEGER NOT NULL, iniciadoEn INTEGER NOT NULL, finalizadoEn INTEGER, FOREIGN KEY(actividadId) REFERENCES actividades(id) ON UPDATE NO ACTION ON DELETE RESTRICT)""")
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_intentos_actividadId_numero ON intentos(actividadId, numero)")
            db.execSQL("""CREATE TABLE IF NOT EXISTS respuestas (intentoId INTEGER NOT NULL, preguntaId INTEGER NOT NULL, alternativa INTEGER NOT NULL, esCorrecta INTEGER NOT NULL, respondidaEn INTEGER NOT NULL, PRIMARY KEY(intentoId, preguntaId), FOREIGN KEY(intentoId) REFERENCES intentos(id) ON UPDATE NO ACTION ON DELETE RESTRICT, FOREIGN KEY(preguntaId) REFERENCES preguntas(id) ON UPDATE NO ACTION ON DELETE RESTRICT)""")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_respuestas_preguntaId ON respuestas(preguntaId)")
            sembrar(db, incluirCodigo = true)
        }
    }

    fun sembrar(db: SupportSQLiteDatabase, incluirCodigo: Boolean = false) {
        fun actividad(id: Int, titulo: String, modulo: String, curso: String, dificultad: String,
                      objetivo: String, intentos: Int, estado: String, codigo: String) {
            val instrucciones = "Lee con calma. Selecciona una alternativa y confirma tu respuesta. Puedes pedir ayuda a tu asistente."
            if (incluirCodigo) {
                db.execSQL("INSERT INTO actividades (id,titulo,modulo,curso,dificultad,objetivo,intentosPermitidos,estado,codigo,instrucciones) VALUES (?,?,?,?,?,?,?,?,?,?)",
                    arrayOf<Any>(id, titulo, modulo, curso, dificultad, objetivo, intentos, estado, codigo, instrucciones))
            } else {
                db.execSQL("INSERT INTO actividades (id,titulo,modulo,curso,dificultad,objetivo,intentosPermitidos,estado,instrucciones) VALUES (?,?,?,?,?,?,?,?,?)",
                    arrayOf<Any>(id, titulo, modulo, curso, dificultad, objetivo, intentos, estado, instrucciones))
            }
        }
        actividad(1, "El bosque encantado", "Lectura", "2° básico", "Media", "Comprensión implícita", 2, "Disponible", "BOSQ01")
        actividad(2, "Sumas hasta 100", "Matemática", "2° básico", "Media", "Operatoria", 2, "Borrador", "SUMA01")
        actividad(3, "Vocabulario: animales", "Lectura", "1° básico", "Fácil", "Vocabulario", 3, "Disponible", "ANIM01")

        fun pregunta(actividadId: Int, orden: Int, texto: String, enunciado: String,
                     opciones: List<String>, correcta: Int, explicacion: String) {
            db.execSQL("INSERT INTO preguntas (actividadId,orden,textoApoyo,enunciado,opcionA,opcionB,opcionC,opcionD,correcta,retroalimentacion) VALUES (?,?,?,?,?,?,?,?,?,?)",
                arrayOf<Any>(actividadId, orden, texto, enunciado, opciones[0], opciones[1], opciones[2], opciones[3], correcta, explicacion))
        }
        val bosque = "Ana caminaba por el bosque cuando vio un pájaro junto a un árbol. El pájaro tenía un ala herida. Ana preparó una caja suave y pidió ayuda a una persona adulta. Juntos llevaron al pájaro a un lugar donde podían cuidarlo."
        pregunta(1, 1, bosque, "¿Por qué Ana pidió ayuda?", listOf("Quería trepar al árbol", "El pájaro necesitaba cuidados", "Había perdido una caja", "Quería buscar flores"), 1, "El pájaro tenía un ala herida. Ana pidió ayuda para que pudieran cuidarlo.")
        pregunta(1, 2, bosque, "¿Cómo fue Ana con el pájaro?", listOf("Indiferente", "Impaciente", "Cuidadosa", "Desordenada"), 2, "Preparar una caja suave y buscar ayuda muestra que Ana fue cuidadosa.")
        pregunta(2, 1, "", "Hay 24 lápices y llegan 13 más. ¿Cuántos hay en total?", listOf("11", "27", "37", "47"), 2, "24 + 13 = 37: suma 20 + 10 y después 4 + 3.")
        pregunta(2, 2, "", "¿Cuánto es 35 + 25?", listOf("50", "60", "65", "70"), 1, "35 + 25 = 60. Cinco unidades más cinco forman otra decena.")
        pregunta(3, 1, "", "¿Qué animal tiene plumas?", listOf("Gato", "Perro", "Pájaro", "Pez"), 2, "Los pájaros tienen plumas que cubren su cuerpo.")
        pregunta(3, 2, "", "¿Cuál de estos animales tiene aletas?", listOf("Pez", "Conejo", "Caballo", "Gato"), 0, "El pez usa sus aletas para moverse en el agua.")
    }
}
