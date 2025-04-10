package com.musicplayer.data

import androidx.room.*
import com.musicplayer.models.Playlist
import com.musicplayer.models.PlaylistSong
import com.musicplayer.models.Song
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists ORDER BY updatedAt DESC")
    suspend fun getAllPlaylists(): List<Playlist>

    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    suspend fun getPlaylistById(playlistId: Long): Playlist?

    @Query("""
        SELECT s.* FROM songs s
        INNER JOIN playlist_songs ps ON s.id = ps.songId
        WHERE ps.playlistId = :playlistId
        ORDER BY ps.position
    """)
    fun getPlaylistSongs(playlistId: Long): Flow<List<Song>>

    @Query("""
        SELECT s.* FROM songs s
        INNER JOIN playlist_songs ps ON s.id = ps.songId
        WHERE ps.playlistId = :playlistId
        ORDER BY ps.position
    """)
    suspend fun getPlaylistSongsSync(playlistId: Long): List<Song>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: Playlist): Long

    @Update
    suspend fun updatePlaylist(playlist: Playlist)

    @Delete
    suspend fun deletePlaylist(playlist: Playlist)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistSong(playlistSong: PlaylistSong)

    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId AND songId = :songId")
    suspend fun deletePlaylistSong(playlistId: Long, songId: Long)

    @Query("SELECT COUNT(*) FROM playlist_songs WHERE playlistId = :playlistId")
    suspend fun getPlaylistSongCount(playlistId: Long): Int

    @Query("SELECT * FROM songs WHERE id = :songId")
    suspend fun getSongById(songId: Long): Song?

    @Transaction
    suspend fun reorderPlaylistSongs(playlistId: Long, fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            // Moving down
            @Query("""
                UPDATE playlist_songs 
                SET position = position - 1 
                WHERE playlistId = :playlistId 
                AND position > :fromPosition 
                AND position <= :toPosition
            """)
            suspend fun updatePositionsDown(playlistId: Long, fromPosition: Int, toPosition: Int)
            
            updatePositionsDown(playlistId, fromPosition, toPosition)
        } else {
            // Moving up
            @Query("""
                UPDATE playlist_songs 
                SET position = position + 1 
                WHERE playlistId = :playlistId 
                AND position >= :toPosition 
                AND position < :fromPosition
            """)
            suspend fun updatePositionsUp(playlistId: Long, fromPosition: Int, toPosition: Int)
            
            updatePositionsUp(playlistId, fromPosition, toPosition)
        }

        @Query("""
            UPDATE playlist_songs 
            SET position = :toPosition 
            WHERE playlistId = :playlistId 
            AND position = :fromPosition
        """)
        suspend fun updateMovedItem(playlistId: Long, fromPosition: Int, toPosition: Int)
        
        updateMovedItem(playlistId, fromPosition, toPosition)
    }

    @Query("""
        SELECT * FROM playlists 
        WHERE name LIKE '%' || :query || '%' 
        ORDER BY updatedAt DESC
    """)
    suspend fun searchPlaylists(query: String): List<Playlist>
}
