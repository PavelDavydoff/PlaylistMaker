package com.example.playlistmaker.library.domain.impl

import com.example.playlistmaker.library.domain.api.FavoriteInteractor
import com.example.playlistmaker.library.domain.api.FavoriteRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoriteInteractorImpl(private val favoriteRepository: FavoriteRepository): FavoriteInteractor {
    override fun addToFavorite(track: Track) {
        favoriteRepository.addToFavorite(track)
    }

    override fun removeFromFavorite(track: Track) {
        favoriteRepository.removeFromFavorite(track)
    }

    override fun getFavorites(): Flow<List<Track>> {
        return favoriteRepository.getFavorites()
    }

    override fun addToPlaylists(track: Track){
        favoriteRepository.addToPlaylists(track)
    }

    override fun deleteFromPlaylists(track: Track) {
        favoriteRepository.deleteFromPlaylists(track)
    }

    override fun getTracksFromPlaylist(): Flow<List<Track>> {
        return favoriteRepository.getTracksFromPlaylist()
    }
}