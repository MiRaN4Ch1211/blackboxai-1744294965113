package com.musicplayer.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.musicplayer.data.AppDatabase
import com.musicplayer.models.Song
import com.musicplayer.repository.MusicRepository
import kotlinx.coroutines.launch

class MusicViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MusicRepository
    private val _songs = MutableLiveData<List<Song>>()
    val songs: LiveData<List<Song>> = _songs

    private val _currentSong = MutableLiveData<Song?>()
    val currentSong: LiveData<Song?> = _currentSong

    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _recentlyPlayed = MutableLiveData<List<Song>>()
    val recentlyPlayed: LiveData<List<Song>> = _recentlyPlayed

    private val _mostPlayed = MutableLiveData<List<Song>>()
    val mostPlayed: LiveData<List<Song>> = _mostPlayed

    init {
        val songDao = AppDatabase.getDatabase(application).songDao()
        repository = MusicRepository(application, songDao)
        loadSongs()
        loadRecentlyPlayed()
        loadMostPlayed()
    }

    private fun loadSongs() {
        viewModelScope.launch {
            try {
                val deviceSongs = repository.loadDeviceMusic()
                _songs.value = deviceSongs
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }
    }

    private fun loadRecentlyPlayed() {
        viewModelScope.launch {
            try {
                _recentlyPlayed.value = repository.getRecentlyPlayed()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadMostPlayed() {
        viewModelScope.launch {
            try {
                _mostPlayed.value = repository.getMostPlayed()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun playSong(song: Song) {
        viewModelScope.launch {
            try {
                repository.updatePlayStats(song.id)
                _currentSong.value = song
                _isPlaying.value = true
                // Refresh lists after updating play stats
                loadRecentlyPlayed()
                loadMostPlayed()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun pausePlayback() {
        _isPlaying.value = false
    }

    fun resumePlayback() {
        _isPlaying.value = true
    }

    fun stopPlayback() {
        _currentSong.value = null
        _isPlaying.value = false
    }

    fun refreshLibrary() {
        loadSongs()
    }

    fun searchSongs(query: String): List<Song> {
        return _songs.value?.filter { song ->
            song.title.contains(query, ignoreCase = true) ||
            song.artist.contains(query, ignoreCase = true) ||
            song.album.contains(query, ignoreCase = true)
        } ?: emptyList()
    }
}
