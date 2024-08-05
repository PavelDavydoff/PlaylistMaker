package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface HistoryRepository {
    fun clearPrefs()
    fun getTracks(): List<Track>
    fun putTracks()
    fun addTrack(track: Track)
    fun clearHistory()
}