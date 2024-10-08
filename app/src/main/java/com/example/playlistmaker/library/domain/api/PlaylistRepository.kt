package com.example.playlistmaker.library.domain.api

import com.example.playlistmaker.library.domain.models.Playlist

interface PlaylistRepository {
    fun addNewPlaylist(playlist: Playlist)
}