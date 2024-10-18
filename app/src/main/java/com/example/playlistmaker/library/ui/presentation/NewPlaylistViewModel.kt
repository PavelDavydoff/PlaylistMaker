package com.example.playlistmaker.library.ui.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.library.domain.api.ImageFileInteractor
import com.example.playlistmaker.library.domain.api.PlaylistInteractor
import com.example.playlistmaker.library.domain.models.Playlist

class NewPlaylistViewModel(private val playlistInteractor: PlaylistInteractor, private val imageFileInteractor: ImageFileInteractor): ViewModel() {
    fun addNewPlaylist(playlist: Playlist){
        playlistInteractor.addNewPlaylist(playlist)
    }
    fun saveImage(uri: Uri){
        val url = uri.toString()
        imageFileInteractor.saveImageToPrivateStorage(url)
    }
}