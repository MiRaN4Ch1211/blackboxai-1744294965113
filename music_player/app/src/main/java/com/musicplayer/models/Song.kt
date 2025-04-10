package com.musicplayer.models

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class Song(
    @PrimaryKey
    val id: Long,
    
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val path: String,
    
    val albumArtUri: Uri? = null,
    val description: String? = null,
    
    // Additional metadata
    val dateAdded: Long = System.currentTimeMillis(),
    val lastPlayed: Long? = null,
    val playCount: Int = 0
) {
    companion object {
        fun fromMediaStore(
            id: Long,
            title: String,
            artist: String,
            album: String,
            duration: Long,
            path: String,
            albumArtUri: Uri?
        ): Song {
            return Song(
                id = id,
                title = title,
                artist = artist,
                album = album,
                duration = duration,
                path = path,
                albumArtUri = albumArtUri
            )
        }
    }

    fun formatDuration(): String {
        val minutes = duration / 1000 / 60
        val seconds = duration / 1000 % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "title" to title,
            "artist" to artist,
            "album" to album,
            "duration" to duration,
            "path" to path,
            "albumArtUri" to albumArtUri?.toString(),
            "description" to description,
            "dateAdded" to dateAdded,
            "lastPlayed" to lastPlayed,
            "playCount" to playCount
        )
    }
}
