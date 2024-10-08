package com.example.playlistmaker.library.domain.api

import com.example.playlistmaker.library.domain.models.Playlist

interface PlaylistInteractor {
    fun addNewPlaylist(playlist: Playlist)
}