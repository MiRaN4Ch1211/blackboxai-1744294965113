package com.musicplayer.repository

import com.musicplayer.data.PlaylistDao
import com.musicplayer.models.Playlist
import com.musicplayer.models.PlaylistSong
import com.musicplayer.models.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PlaylistRepository(private val playlistDao: PlaylistDao) {

    suspend fun getAllPlaylists(): List<Playlist> = withContext(Dispatchers.IO) {
        playlistDao.getAllPlaylists()
    }

    suspend fun getPlaylistById(playlistId: Long): Playlist? = withContext(Dispatchers.IO) {
        playlistDao.getPlaylistById(playlistId)
    }

    fun getPlaylistSongs(playlistId: Long): Flow<List<Song>> {
        return playlistDao.getPlaylistSongs(playlistId)
    }

    suspend fun insertPlaylist(playlist: Playlist): Long = withContext(Dispatchers.IO) {
        playlistDao.insertPlaylist(playlist)
    }

    suspend fun updatePlaylist(playlist: Playlist) = withContext(Dispatchers.IO) {
        playlistDao.updatePlaylist(playlist)
    }

    suspend fun deletePlaylist(playlist: Playlist) = withContext(Dispatchers.IO) {
        playlistDao.deletePlaylist(playlist)
    }

    suspend fun addSongToPlaylist(playlistId: Long, songId: Long) = withContext(Dispatchers.IO) {
        val position = playlistDao.getPlaylistSongCount(playlistId)
        val playlistSong = PlaylistSong(playlistId, songId, position)
        playlistDao.insertPlaylistSong(playlistSong)
        
        // Update playlist metadata
        val song = playlistDao.getSongById(songId)
        song?.let {
            val playlist = playlistDao.getPlaylistById(playlistId)
            playlist?.let {
                val updatedPlaylist = it.copy(
                    songCount = it.songCount + 1,
                    totalDuration = it.totalDuration + song.duration,
                    updatedAt = java.util.Date()
                )
                playlistDao.updatePlaylist(updatedPlaylist)
            }
        }
    }

    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long) = withContext(Dispatchers.IO) {
        playlistDao.deletePlaylistSong(playlistId, songId)
        
        // Update playlist metadata
        val song = playlistDao.getSongById(songId)
        song?.let {
            val playlist = playlistDao.getPlaylistById(playlistId)
            playlist?.let {
                val updatedPlaylist = it.copy(
                    songCount = it.songCount - 1,
                    totalDuration = it.totalDuration - song.duration,
                    updatedAt = java.util.Date()
                )
                playlistDao.updatePlaylist(updatedPlaylist)
            }
        }
    }

    suspend fun reorderPlaylistSongs(playlistId: Long, fromPosition: Int, toPosition: Int) = withContext(Dispatchers.IO) {
        playlistDao.reorderPlaylistSongs(playlistId, fromPosition, toPosition)
    }

    suspend fun getPlaylistWithSongs(playlistId: Long): Pair<Playlist, List<Song>>? = withContext(Dispatchers.IO) {
        val playlist = playlistDao.getPlaylistById(playlistId) ?: return@withContext null
        val songs = playlistDao.getPlaylistSongsSync(playlistId)
        Pair(playlist, songs)
    }
}
