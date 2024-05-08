package com.example.playlistmaker.player.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.domain.PlayerState

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val playerState = MutableLiveData<PlayerState>()

    private val player = Creator.mediaPlayerProvider()

    private val stateLiveData = MutableLiveData<PlayerState>()
    fun observeState(): LiveData<PlayerState> = stateLiveData

    companion object {
        fun getViewModelFactory(url: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }
}