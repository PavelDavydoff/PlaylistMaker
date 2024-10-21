package com.example.playlistmaker.library.ui.models

import com.example.playlistmaker.search.domain.models.Track

sealed class DetailsState{
    class Content(val tracks: List<Track>) : DetailsState()

    data object Empty : DetailsState()
}
