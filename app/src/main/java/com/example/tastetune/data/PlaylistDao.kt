package com.example.tastetune.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: Playlist): Long

    @Query("SELECT * FROM playlists WHERE analysisId = :analysisId")
    suspend fun getPlaylistsForAnalysis(analysisId: Int): List<Playlist>
}
