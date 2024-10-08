package com.example.playlistmaker.library.domain.impl

import com.example.playlistmaker.library.domain.api.PlaylistInteractor
import com.example.playlistmaker.library.domain.api.PlaylistRepository
import com.example.playlistmaker.library.domain.models.Playlist

class PlaylistInteractorImpl(private val repository: PlaylistRepository) : PlaylistInteractor {
    override fun addNewPlaylist(playlist: Playlist) {
        repository.addNewPlaylist(playlist)
    }
}