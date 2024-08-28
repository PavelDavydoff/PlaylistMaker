package com.example.playlistmaker.library.ui.models

import com.example.playlistmaker.search.domain.models.Track

sealed class FavoriteState {
    class Content(val tracks: List<Track>) : FavoriteState()

    data object Empty : FavoriteState()
}
