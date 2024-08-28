package com.example.playlistmaker.library.domain.api

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {

    fun addToFavorite(track: Track)

    fun removeFromFavorite(track: Track)

    fun getFavorites(): Flow<List<Track>>
}