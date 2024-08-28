package com.example.playlistmaker.library.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.library.db.entity.TrackEntity
@Dao
interface TrackDao {

    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    fun insertTrack(trackEntity: TrackEntity)

    @Delete(entity = TrackEntity::class)
    fun deleteTrack(trackEntity: TrackEntity)

    @Query("SELECT * FROM track_table")
    fun getTracks(): List<TrackEntity>

    //@Query()
    //fun
}