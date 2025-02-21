package com.example.playlistmaker.library.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.library.db.entity.TrackPlEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackPlDao {
    @Insert(entity = TrackPlEntity::class, onConflict = OnConflictStrategy.IGNORE)
    fun insertTrack(trackPlEntity: TrackPlEntity)

    @Query("SELECT * FROM track_in_playlists_table")
    fun getTracks(): Flow<List<TrackPlEntity>>

    @Delete(entity = TrackPlEntity::class)
    fun deleteTrack(trackPlEntity: TrackPlEntity)
}