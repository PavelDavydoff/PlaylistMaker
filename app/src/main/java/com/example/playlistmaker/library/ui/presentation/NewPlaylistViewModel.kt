package com.example.playlistmaker.library.ui.presentation

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.library.domain.api.ImageFileInteractor
import com.example.playlistmaker.library.domain.api.PlaylistInteractor
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.library.ui.models.NewPlaylistState

class NewPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val imageFileInteractor: ImageFileInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<NewPlaylistState>()
    fun observeState(): LiveData<NewPlaylistState> = stateLiveData

    fun addNewPlaylist(playlist: Playlist) {
        playlistInteractor.addNewPlaylist(playlist)
    }

    fun saveImage(uri: Uri, fileName: String): String {
        val url = uri.toString()
        return imageFileInteractor.saveImageToPrivateStorage(url, fileName)
    }

    fun setState(id: Int) {
        if (id == 0) {
            stateLiveData.postValue(NewPlaylistState.New)
        } else {
            stateLiveData.postValue(NewPlaylistState.Edit(playlistInteractor.getPlaylist(id)))
        }
    }
}