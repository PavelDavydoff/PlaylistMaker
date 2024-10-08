package com.example.playlistmaker.library.ui.presentation

import androidx.lifecycle.ViewModel
import com.example.playlistmaker.library.domain.api.PlaylistInteractor
import com.example.playlistmaker.library.domain.models.Playlist

class NewPlaylistViewModel(private val playlistInteractor: PlaylistInteractor): ViewModel() {
    fun addNewPlaylist(playlist: Playlist){
        playlistInteractor.addNewPlaylist(playlist)
    }
}