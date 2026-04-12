package com.dam.lockgo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [CompletedActivity::class, RewardProfileEntity::class, OwnedBadgeEntity::class],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun lockAndGoDao(): LockAndGoDao
    abstract fun rewardDao(): RewardDao

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
                    .fallbackToDestructiveMigration() // Destruye las tablas viejas
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}