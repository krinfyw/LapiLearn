package com.example.lapilearn.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
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
}