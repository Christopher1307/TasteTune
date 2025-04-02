package com.example.tastetune.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class Playlist(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val analysisId: Int,
    val playlistId: String,
    val name: String,
    val description: String
)
