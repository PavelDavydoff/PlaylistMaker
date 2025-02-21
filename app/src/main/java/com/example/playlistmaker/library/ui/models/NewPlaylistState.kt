package com.example.playlistmaker.library.ui.models

import com.example.playlistmaker.library.domain.models.Playlist

sealed interface NewPlaylistState{
    data object New: NewPlaylistState
    data class Edit(val playlist: Playlist): NewPlaylistState
}