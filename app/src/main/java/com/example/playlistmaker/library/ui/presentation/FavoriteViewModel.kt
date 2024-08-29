package com.example.playlistmaker.library.ui.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.api.FavoriteInteractor
import com.example.playlistmaker.library.ui.models.FavoriteState
import kotlinx.coroutines.launch

class FavoriteViewModel(private val favoriteInteractor: FavoriteInteractor) : ViewModel() {

    private val stateLiveData = MutableLiveData<FavoriteState>()
    fun observeState(): LiveData<FavoriteState> = stateLiveData

    fun getFavorites(){
        viewModelScope.launch {
            favoriteInteractor.getFavorites().collect { tracks ->
                val listOfTracks = tracks.toMutableList()
                listOfTracks.addAll(tracks)

                if (listOfTracks.isEmpty()) {
                    stateLiveData.postValue(FavoriteState.Empty)
                } else {
                    stateLiveData.postValue(FavoriteState.Content(listOfTracks))
                }
            }
        }
    }
}