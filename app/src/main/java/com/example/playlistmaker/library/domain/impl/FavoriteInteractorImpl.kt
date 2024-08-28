package com.example.playlistmaker.library.domain.impl

import com.example.playlistmaker.library.domain.api.FavoriteInteractor
import com.example.playlistmaker.library.domain.api.FavoriteRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteInteractorImpl(private val favoriteRepository: FavoriteRepository): FavoriteInteractor {
    override fun addToFavorite(track: Track) {
        favoriteRepository.addToFavorite(track)
    }

    override fun removeFromFavorite(track: Track) {
        favoriteRepository.removeFromFavorite(track)
    }

    override fun getFavorites(): Flow<List<Track>> = flow {
        favoriteRepository.getFavorites()
    }
}