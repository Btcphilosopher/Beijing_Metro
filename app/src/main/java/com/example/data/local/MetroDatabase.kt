package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        SavedStationEntity::class,
        SavedRouteEntity::class,
        TripHistoryEntity::class,
        UserSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MetroDatabase : RoomDatabase() {
    abstract fun metroDao(): MetroDao

    companion object {
        @Volatile
        private var INSTANCE: MetroDatabase? = null

        fun getDatabase(context: Context): MetroDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MetroDatabase::class.java,
                    "beijing_metro_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
