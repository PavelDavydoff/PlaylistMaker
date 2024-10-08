package com.example.playlistmaker.library.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey
    val name: String,
    val description: String,
    val filePath: String,
    val tracks: String,
    val tracksCount: Int
)