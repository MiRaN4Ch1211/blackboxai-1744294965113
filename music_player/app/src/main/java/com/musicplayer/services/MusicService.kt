package com.musicplayer.services

import android.app.*
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import com.musicplayer.MainActivity
import com.musicplayer.R
import com.musicplayer.models.Song

class MusicService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var mediaSession: MediaSessionCompat? = null
    private val binder = MusicBinder()
    private var currentSong: Song? = null

    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        setupMediaSession()
    }

    private fun setupMediaSession() {
        mediaSession = MediaSessionCompat(this, "MusicService").apply {
            setPlaybackState(
                PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_NONE, 0, 1f)
                    .build()
            )
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    play()
                }

                override fun onPause() {
                    pause()
                }

                override fun onSkipToNext() {
                    skipToNext()
                }

                override fun onSkipToPrevious() {
                    skipToPrevious()
                }
            })
        }
    }

    fun play() {
        mediaPlayer?.start()
        updatePlaybackState(PlaybackStateCompat.STATE_PLAYING)
        startForeground(NOTIFICATION_ID, createNotification())
    }

    fun pause() {
        mediaPlayer?.pause()
        updatePlaybackState(PlaybackStateCompat.STATE_PAUSED)
    }

    fun skipToNext() {
        // Implement next song logic
    }

    fun skipToPrevious() {
        // Implement previous song logic
    }

    fun playSong(song: Song) {
        currentSong = song
        mediaPlayer?.apply {
            reset()
            setDataSource(song.path)
            prepare()
            start()
        }
        updatePlaybackState(PlaybackStateCompat.STATE_PLAYING)
        startForeground(NOTIFICATION_ID, createNotification())
    }

    private fun updatePlaybackState(state: Int) {
        mediaSession?.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setState(state, mediaPlayer?.currentPosition?.toLong() ?: 0, 1f)
                .build()
        )
    }

    private fun createNotification(): Notification {
        val song = currentSong ?: return createEmptyNotification()

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(song.title)
            .setContentText(song.artist)
            .setSmallIcon(R.drawable.ic_music_note)
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(
                R.drawable.ic_previous,
                "Previous",
                createActionIntent(ACTION_PREVIOUS)
            )
            .addAction(
                if (mediaPlayer?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play,
                if (mediaPlayer?.isPlaying == true) "Pause" else "Play",
                createActionIntent(if (mediaPlayer?.isPlaying == true) ACTION_PAUSE else ACTION_PLAY)
            )
            .addAction(
                R.drawable.ic_next,
                "Next",
                createActionIntent(ACTION_NEXT)
            )
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession?.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    private fun createEmptyNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Music Player")
            .setContentText("No song playing")
            .setSmallIcon(R.drawable.ic_music_note)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createActionIntent(action: String): PendingIntent {
        val intent = Intent(action)
        return PendingIntent.getBroadcast(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        mediaSession?.release()
        super.onDestroy()
    }

    companion object {
        const val CHANNEL_ID = "MusicServiceChannel"
        const val NOTIFICATION_ID = 1
        const val ACTION_PLAY = "com.musicplayer.ACTION_PLAY"
        const val ACTION_PAUSE = "com.musicplayer.ACTION_PAUSE"
        const val ACTION_PREVIOUS = "com.musicplayer.ACTION_PREVIOUS"
        const val ACTION_NEXT = "com.musicplayer.ACTION_NEXT"
    }
}
