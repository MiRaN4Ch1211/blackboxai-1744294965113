package com.musicplayer.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "playlists")
data class Playlist(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val name: String,
    val description: String? = null,
    val coverUri: String? = null,
    
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    
    val songCount: Int = 0,
    val totalDuration: Long = 0
) {
    fun formatDuration(): String {
        val hours = totalDuration / (1000 * 60 * 60)
        val minutes = (totalDuration % (1000 * 60 * 60)) / (1000 * 60)
        
        return when {
            hours > 0 -> String.format("%d hour%s %d minute%s", 
                hours, if (hours != 1L) "s" else "", 
                minutes, if (minutes != 1L) "s" else "")
            minutes > 0 -> String.format("%d minute%s", 
                minutes, if (minutes != 1L) "s" else "")
            else -> "Empty playlist"
        }
    }

    fun getSongCountText(): String {
        return when (songCount) {
            0 -> "No songs"
            1 -> "1 song"
            else -> "$songCount songs"
        }
    }
}

@Entity(tableName = "playlist_songs",
    primaryKeys = ["playlistId", "songId"])
data class PlaylistSong(
    val playlistId: Long,
    val songId: Long,
    val position: Int,
    val addedAt: Date = Date()
)
