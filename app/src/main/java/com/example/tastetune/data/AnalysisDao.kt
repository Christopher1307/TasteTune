package com.example.tastetune.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AnalysisDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalysis(analysis: Analysis): Long

    @Query("SELECT * FROM analysis ORDER BY timestamp DESC")
    suspend fun getAllAnalysis(): List<Analysis>
}
