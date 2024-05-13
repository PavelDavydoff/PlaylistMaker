package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TrackConsumer)
    interface TrackConsumer {
        fun consume(foundTracks: List<Track>?, errorMessage: String?)
    }
}