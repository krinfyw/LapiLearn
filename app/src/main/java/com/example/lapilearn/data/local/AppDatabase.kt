package com.example.lapilearn.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.lapilearn.data.local.dao.AsistenteDao
import com.example.lapilearn.data.local.dao.DocenteDao
import com.example.lapilearn.data.local.entity.AsistenteEntity
import com.example.lapilearn.data.local.entity.DocenteEntity

@Database(
    entities = [DocenteEntity::class, AsistenteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun docenteDao(): DocenteDao
    abstract fun asistenteDao(): AsistenteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Una sola instancia para toda la app (evita abrir el mismo archivo dos veces)
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lapilearn_database"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        // Datos de prueba: solo se ejecuta al crear la BD por primera vez
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            db.execSQL("INSERT INTO docentes (correo, contrasena, nombre) VALUES ('docente@duoc.cl', '123456', 'Docente Prueba')")
                            db.execSQL("INSERT INTO asistentes (correo, contrasena, nombre) VALUES ('asistente@duoc.cl', '123456', 'Asistente Prueba')")
                        }
                    })
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}