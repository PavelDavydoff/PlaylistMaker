package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.HistoryInteractor
import com.example.playlistmaker.search.domain.api.HistoryRepository
import com.example.playlistmaker.search.domain.models.Track

class HistoryInteractorImpl(private val repository: HistoryRepository): HistoryInteractor {
    override fun clearPrefs() {
        repository.clearPrefs()
    }

    override fun putTracks() {
        repository.putTracks()
    }

    override fun getTracks(): List<Track> {
        return repository.getTracks()
    }

    override fun addTrack(track: Track) {
        repository.addTrack(track)
    }
}