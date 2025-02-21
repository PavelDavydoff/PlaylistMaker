package com.example.playlistmaker.player.ui.models

import com.example.playlistmaker.library.domain.models.Playlist

sealed class PlaylistState {
    class Content(val playlists: List<Playlist>) : PlaylistState()
    data object Empty : PlaylistState()
}