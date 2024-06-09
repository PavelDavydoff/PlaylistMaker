package com.example.playlistmaker.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.player.ui.models.PlayerState

class PlayerViewModel : ViewModel() {

    private val stateLiveData = MutableLiveData<PlayerState>()
    fun observeState(): LiveData<PlayerState> = stateLiveData

    fun play(){
        stateLiveData.postValue(PlayerState.Playing)
    }

    fun pause(){
        stateLiveData.postValue(PlayerState.Paused)
    }
}