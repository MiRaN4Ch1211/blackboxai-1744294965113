package com.musicplayer.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.musicplayer.data.SongDao
import com.musicplayer.models.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MusicRepository(
    private val context: Context,
    private val songDao: SongDao
) {
    private val contentResolver: ContentResolver = context.contentResolver

    suspend fun loadDeviceMusic() = withContext(Dispatchers.IO) {
        val songs = mutableListOf<Song>()
        
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        
        contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                val artist = cursor.getString(artistColumn)
                val album = cursor.getString(albumColumn)
                val duration = cursor.getLong(durationColumn)
                val path = cursor.getString(pathColumn)
                val albumId = cursor.getLong(albumIdColumn)

                val albumArtUri = ContentUris.withAppendedId(
                    Uri.parse("content://media/external/audio/albumart"),
                    albumId
                )

                val song = Song.fromMediaStore(
                    id = id,
                    title = title,
                    artist = artist,
                    album = album,
                    duration = duration,
                    path = path,
                    albumArtUri = albumArtUri
                )
                
                songs.add(song)
                songDao.insertSong(song)
            }
        }
        
        songs
    }

    suspend fun getAllSongs(): List<Song> = withContext(Dispatchers.IO) {
        songDao.getAllSongs()
    }

    suspend fun getRecentlyPlayed(limit: Int = 20): List<Song> = withContext(Dispatchers.IO) {
        songDao.getRecentlyPlayed(limit)
    }

    suspend fun getMostPlayed(limit: Int = 20): List<Song> = withContext(Dispatchers.IO) {
        songDao.getMostPlayed(limit)
    }

    suspend fun updatePlayStats(songId: Long) = withContext(Dispatchers.IO) {
        songDao.updatePlayStats(songId)
    }

    suspend fun getSongById(songId: Long): Song? = withContext(Dispatchers.IO) {
        songDao.getSongById(songId)
    }

    suspend fun deleteSong(song: Song) = withContext(Dispatchers.IO) {
        songDao.deleteSong(song)
    }
}
