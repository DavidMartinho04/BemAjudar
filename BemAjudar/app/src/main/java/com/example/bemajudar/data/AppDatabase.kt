package com.example.bemajudar.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bemajudar.data.local.UserDao
import com.example.bemajudar.data.local.UserEntity

@Database(entities = [UserEntity::class], version = 3, exportSchema = false) // Versão incrementada para 3
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bem_ajudar_db"
                )
                    .fallbackToDestructiveMigration() // Apaga os dados antigos em caso de mudança de schema
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
