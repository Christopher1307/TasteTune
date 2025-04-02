package com.example.tastetune.data


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "analysis")
data class Analysis(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val foodLabel: String,
    val timestamp: Long,
    val imagePath: String
)
