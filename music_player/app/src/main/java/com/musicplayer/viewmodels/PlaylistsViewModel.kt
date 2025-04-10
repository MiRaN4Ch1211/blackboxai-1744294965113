package com.musicplayer.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.musicplayer.data.AppDatabase
import com.musicplayer.models.Playlist
import com.musicplayer.models.Song
import com.musicplayer.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Date

class PlaylistsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PlaylistRepository
    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists

    init {
        val playlistDao = AppDatabase.getDatabase(application).playlistDao()
        repository = PlaylistRepository(playlistDao)
        loadPlaylists()
    }

    private fun loadPlaylists() {
        viewModelScope.launch {
            _playlists.value = repository.getAllPlaylists()
        }
    }

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            val newPlaylist = Playlist(
                name = name,
                createdAt = Date(),
                updatedAt = Date()
            )
            repository.insertPlaylist(newPlaylist)
            loadPlaylists()
        }
    }

    fun updatePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            repository.updatePlaylist(playlist.copy(updatedAt = Date()))
            loadPlaylists()
        }
    }

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            repository.deletePlaylist(playlist)
            loadPlaylists()
        }
    }

    // Playlist Details Methods
    fun getPlaylistById(playlistId: Long): LiveData<Playlist?> {
        val result = MutableLiveData<Playlist?>()
        viewModelScope.launch {
            result.value = repository.getPlaylistById(playlistId)
        }
        return result
    }

    fun getPlaylistSongs(playlistId: Long): Flow<List<Song>> {
        return repository.getPlaylistSongs(playlistId)
    }

    fun loadPlaylist(playlistId: Long) {
        viewModelScope.launch {
            repository.getPlaylistById(playlistId)
        }
    }

    fun addSongToPlaylist(playlistId: Long, songId: Long) {
        viewModelScope.launch {
            repository.addSongToPlaylist(playlistId, songId)
        }
    }

    fun removeSongFromPlaylist(playlistId: Long, songId: Long) {
        viewModelScope.launch {
            repository.removeSongFromPlaylist(playlistId, songId)
        }
    }

    fun reorderPlaylistSongs(playlistId: Long, fromPosition: Int, toPosition: Int) {
        viewModelScope.launch {
            repository.reorderPlaylistSongs(playlistId, fromPosition, toPosition)
        }
    }

    fun playPlaylist(playlistId: Long) {
        viewModelScope.launch {
            val songs = repository.getPlaylistWithSongs(playlistId)?.second
            songs?.firstOrNull()?.let { firstSong ->
                // TODO: Implement play functionality through MusicService
            }
        }
    }

    fun shufflePlaylist(playlistId: Long) {
        viewModelScope.launch {
            val songs = repository.getPlaylistWithSongs(playlistId)?.second
            songs?.shuffled()?.firstOrNull()?.let { randomSong ->
                // TODO: Implement shuffle functionality through MusicService
            }
        }
    }
}
