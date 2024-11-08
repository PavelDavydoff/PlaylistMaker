package com.example.playlistmaker.library.ui.models

import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track

sealed class DetailsState(val playlist: Playlist){
    class Content(playlist: Playlist, val tracks: List<Track>): DetailsState(playlist)
    class Empty(playlist: Playlist) : DetailsState(playlist)
}


