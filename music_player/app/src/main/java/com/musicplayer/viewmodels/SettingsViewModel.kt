package com.musicplayer.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.musicplayer.utils.SettingsManager

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsManager = SettingsManager.getInstance(application)

    private val _isDarkTheme = MutableLiveData(settingsManager.isDarkTheme)
    val isDarkTheme: LiveData<Boolean> = _isDarkTheme

    private val _audioQuality = MutableLiveData(settingsManager.audioQuality)
    val audioQuality: LiveData<SettingsManager.AudioQuality> = _audioQuality

    fun setDarkTheme(enabled: Boolean) {
        settingsManager.isDarkTheme = enabled
        _isDarkTheme.value = enabled
    }

    fun setAudioQuality(quality: SettingsManager.AudioQuality) {
        settingsManager.audioQuality = quality
        _audioQuality.value = quality
    }

    fun getLastPlayedSongId(): Long {
        return settingsManager.lastPlayedSongId
    }

    fun setLastPlayedSongId(songId: Long) {
        settingsManager.lastPlayedSongId = songId
    }

    fun getLastPlaybackPosition(): Long {
        return settingsManager.lastPlaybackPosition
    }

    fun setLastPlaybackPosition(position: Long) {
        settingsManager.lastPlaybackPosition = position
    }

    fun isShuffleEnabled(): Boolean {
        return settingsManager.isShuffleEnabled
    }

    fun setShuffleEnabled(enabled: Boolean) {
        settingsManager.isShuffleEnabled = enabled
    }

    fun getRepeatMode(): SettingsManager.RepeatMode {
        return settingsManager.repeatMode
    }

    fun setRepeatMode(mode: SettingsManager.RepeatMode) {
        settingsManager.repeatMode = mode
    }

    fun getAppVersion(): String {
        return getApplication<Application>().let {
            val packageInfo = it.packageManager.getPackageInfo(it.packageName, 0)
            "${packageInfo.versionName} (${packageInfo.versionCode})"
        }
    }
}
