package com.example.tastetune.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Analysis::class, Playlist::class], version = 1, exportSchema = false)
abstract class TasteTuneDatabase : RoomDatabase() {

    abstract fun analysisDao(): AnalysisDao
    abstract fun playlistDao(): PlaylistDao

    companion object {
        @Volatile
        private var INSTANCE: TasteTuneDatabase? = null

        fun getDatabase(context: Context): TasteTuneDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TasteTuneDatabase::class.java,
                    "tastetune_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
