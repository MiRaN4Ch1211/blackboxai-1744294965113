package com.musicplayer.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

class SettingsManager private constructor(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    var isDarkTheme: Boolean
        get() = prefs.getBoolean(KEY_DARK_THEME, false)
        set(value) {
            prefs.edit {
                putBoolean(KEY_DARK_THEME, value)
            }
            AppCompatDelegate.setDefaultNightMode(
                if (value) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

    var audioQuality: AudioQuality
        get() = AudioQuality.valueOf(
            prefs.getString(KEY_AUDIO_QUALITY, AudioQuality.BALANCED.name)
                ?: AudioQuality.BALANCED.name
        )
        set(value) {
            prefs.edit {
                putString(KEY_AUDIO_QUALITY, value.name)
            }
        }

    var lastPlayedSongId: Long
        get() = prefs.getLong(KEY_LAST_PLAYED_SONG, -1L)
        set(value) {
            prefs.edit {
                putLong(KEY_LAST_PLAYED_SONG, value)
            }
        }

    var lastPlaybackPosition: Long
        get() = prefs.getLong(KEY_LAST_PLAYBACK_POSITION, 0L)
        set(value) {
            prefs.edit {
                putLong(KEY_LAST_PLAYBACK_POSITION, value)
            }
        }

    var isShuffleEnabled: Boolean
        get() = prefs.getBoolean(KEY_SHUFFLE_ENABLED, false)
        set(value) {
            prefs.edit {
                putBoolean(KEY_SHUFFLE_ENABLED, value)
            }
        }

    var repeatMode: RepeatMode
        get() = RepeatMode.valueOf(
            prefs.getString(KEY_REPEAT_MODE, RepeatMode.NONE.name)
                ?: RepeatMode.NONE.name
        )
        set(value) {
            prefs.edit {
                putString(KEY_REPEAT_MODE, value.name)
            }
        }

    enum class AudioQuality {
        HIGH,
        BALANCED,
        DATA_SAVER
    }

    enum class RepeatMode {
        NONE,
        ALL,
        ONE
    }

    companion object {
        private const val PREFS_NAME = "music_player_settings"
        private const val KEY_DARK_THEME = "dark_theme"
        private const val KEY_AUDIO_QUALITY = "audio_quality"
        private const val KEY_LAST_PLAYED_SONG = "last_played_song"
        private const val KEY_LAST_PLAYBACK_POSITION = "last_playback_position"
        private const val KEY_SHUFFLE_ENABLED = "shuffle_enabled"
        private const val KEY_REPEAT_MODE = "repeat_mode"

        @Volatile
        private var instance: SettingsManager? = null

        fun getInstance(context: Context): SettingsManager {
            return instance ?: synchronized(this) {
                instance ?: SettingsManager(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }

    init {
        // Apply saved theme setting on initialization
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
