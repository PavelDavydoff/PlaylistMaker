package com.example.playlistmaker.search.domain.models

import java.io.Serializable

data class Track(val trackName: String,
                 val artistName: String,
                 val trackTimeMillis: Long,
                 val artworkUrl100: String,
                 val collectionName: String,
                 val releaseDate: String,
                 val primaryGenreName: String,
                 val country: String,
                 val previewUrl: String,
                 var isFavorite: Boolean = false): Serializable{
    val artworkUrl512: String
        get() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
                 }
