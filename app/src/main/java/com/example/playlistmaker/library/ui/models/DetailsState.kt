package com.example.playlistmaker.library.ui.models

import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track

data class DetailsState(val playlist: Playlist, val tracks: List<Track>)

