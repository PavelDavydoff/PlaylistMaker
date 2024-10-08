package com.example.playlistmaker.library.ui.models

import com.example.playlistmaker.library.domain.models.Playlist

sealed class PlaylistsState {
    class Content(val playlists: List<Playlist>) : PlaylistsState()
    data object Empty : PlaylistsState()
}