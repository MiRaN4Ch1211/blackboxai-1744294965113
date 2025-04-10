package com.musicplayer.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.musicplayer.models.Playlist
import com.musicplayer.models.PlaylistSong
import com.musicplayer.models.Song
import com.musicplayer.utils.Converters

@Database(
    entities = [
        Song::class,
        Playlist::class,
        PlaylistSong::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun playlistDao(): PlaylistDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "music_player_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

@androidx.room.Dao
interface SongDao {
    @androidx.room.Query("SELECT * FROM songs")
    suspend fun getAllSongs(): List<Song>

    @androidx.room.Query("SELECT * FROM songs WHERE id = :songId")
    suspend fun getSongById(songId: Long): Song?

    @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: Song)

    @androidx.room.Delete
    suspend fun deleteSong(song: Song)

    @androidx.room.Query("UPDATE songs SET playCount = playCount + 1, lastPlayed = :timestamp WHERE id = :songId")
    suspend fun updatePlayStats(songId: Long, timestamp: Long = System.currentTimeMillis())

    @androidx.room.Query("SELECT * FROM songs ORDER BY lastPlayed DESC LIMIT :limit")
    suspend fun getRecentlyPlayed(limit: Int): List<Song>

    @androidx.room.Query("SELECT * FROM songs ORDER BY playCount DESC LIMIT :limit")
    suspend fun getMostPlayed(limit: Int): List<Song>
}
