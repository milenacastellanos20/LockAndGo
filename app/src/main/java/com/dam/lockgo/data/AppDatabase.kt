package com.dam.lockgo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [User::class, DailyProgress::class, BlockedApp::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun lockAndGoDao(): LockAndGoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lockgo_database"
                )
                    .fallbackToDestructiveMigration() // Esto evita que la app pete al cambiar la versión
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
