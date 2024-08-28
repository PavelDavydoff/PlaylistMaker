package com.example.playlistmaker.library.data

import com.example.playlistmaker.library.data.converters.TrackDbConverter
import com.example.playlistmaker.library.db.AppDatabase
import com.example.playlistmaker.library.domain.api.FavoriteRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteRepositoryImpl(
    private val database: AppDatabase,
    private val trackDbConverter: TrackDbConverter
) : FavoriteRepository {

    override fun addToFavorite(track: Track) {
        val trackEntity = trackDbConverter.map(track)
        database.trackDao().insertTrack(trackEntity)
    }

    override fun removeFromFavorite(track: Track) {
        val trackEntity = trackDbConverter.map(track)
        database.trackDao().deleteTrack(trackEntity)
    }

    override fun getFavorites(): Flow<List<Track>> = flow {
        val tracks = database.trackDao().getTracks()
        emit(tracks.map { track -> trackDbConverter.map(track) })
    }

}