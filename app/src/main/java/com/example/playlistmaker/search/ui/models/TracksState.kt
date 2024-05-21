package com.example.playlistmaker.search.ui.models

import com.example.playlistmaker.search.domain.models.Track

sealed interface TracksState {
    data object Loading : TracksState
    data class Content(val tracks: List<Track>) : TracksState
    data object Error : TracksState
    data object Empty : TracksState
    data class History(val tracks: List<Track>): TracksState
}