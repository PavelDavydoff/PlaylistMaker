package com.example.playlistmaker.library.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.playlistmaker.library.db.entity.TrackPlEntity

@Dao
interface TrackPlDao {
    @Insert(entity = TrackPlEntity::class, onConflict = OnConflictStrategy.IGNORE)
    fun insertTrack(trackPlEntity: TrackPlEntity)
}