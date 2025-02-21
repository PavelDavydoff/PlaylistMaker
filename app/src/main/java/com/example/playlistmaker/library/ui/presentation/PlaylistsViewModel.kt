package com.example.playlistmaker.library.ui.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.api.PlaylistInteractor
import com.example.playlistmaker.library.ui.models.PlaylistsState
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistInteractor: PlaylistInteractor): ViewModel(){

    private val stateLiveData = MutableLiveData<PlaylistsState>()
    fun observeState(): LiveData<PlaylistsState> = stateLiveData

    fun getPlaylists(){
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect {playlists ->
                val listOfPlaylists = playlists.toMutableList()
                if(listOfPlaylists.isEmpty()){
                    stateLiveData.postValue(PlaylistsState.Empty)
                } else {
                    stateLiveData.postValue(PlaylistsState.Content(listOfPlaylists))
                }
            }
        }
    }
}