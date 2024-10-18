package com.example.playlistmaker.library.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.library.db.dao.PlaylistDao
import com.example.playlistmaker.library.db.entity.PlaylistEntity

@Database(version = 1, entities = [PlaylistEntity::class])
abstract class PlaylistsDatabase : RoomDatabase(){
    abstract fun playlistDao(): PlaylistDao
}