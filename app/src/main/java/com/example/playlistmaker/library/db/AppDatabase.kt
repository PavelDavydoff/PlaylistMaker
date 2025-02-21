package com.example.playlistmaker.library.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.library.db.dao.TrackDao
import com.example.playlistmaker.library.db.dao.TrackPlDao
import com.example.playlistmaker.library.db.entity.TrackEntity
import com.example.playlistmaker.library.db.entity.TrackPlEntity

@Database(version = 2, entities = [TrackEntity::class, TrackPlEntity::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun trackPlDao(): TrackPlDao
}