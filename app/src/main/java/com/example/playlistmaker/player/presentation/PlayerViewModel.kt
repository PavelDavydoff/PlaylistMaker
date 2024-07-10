package com.example.playlistmaker.player.presentation

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.player.ui.models.PlayerState
import com.example.playlistmaker.player.ui.models.SingleLiveEvent
import com.example.playlistmaker.player.ui.models.ToastState

class PlayerViewModel(private val player: MediaPlayer) : ViewModel() {

    private val stateLiveData = MutableLiveData<PlayerState>()
    fun observeState(): LiveData<PlayerState> = stateLiveData

    private val showToast = SingleLiveEvent<String>()
    fun observeShowToast(): LiveData<String> = showToast

    private val toastState = MutableLiveData<ToastState>(ToastState.None)
    fun observeToastState(): LiveData<ToastState> = toastState

    private val currentPositionState = MutableLiveData<String>()
    fun observePosition(): LiveData<String> = currentPositionState

    fun prepare(url: String?) {
        if (url == null) {
            showToast.postValue(ERROR_MESSAGE)
        }

        player.setDataSource(url)
        player.prepareAsync()
        player.setOnPreparedListener {
            stateLiveData.postValue(PlayerState.Prepare)
        }
        player.setOnCompletionListener {
            stateLiveData.postValue(PlayerState.Prepare)
        }
    }

    fun updatePosition(){
        if (player.isPlaying) {
            currentPositionState.postValue(formatMilliseconds(player.currentPosition))
        } else (currentPositionState.postValue(ZERO_TIME))
    }

    fun play() {
        stateLiveData.postValue(PlayerState.Playing)
        player.start()
    }

    fun pause() {
        player.pause()
        stateLiveData.postValue(PlayerState.Paused)
    }

    fun release() {
        player.release()
    }

    private fun formatMilliseconds(milliseconds: Int): String {
        val seconds = milliseconds/1000
        return String.format("00:%02d", seconds)
    }

    companion object {
        private const val ERROR_MESSAGE = "URL = NULL"
        const val ZERO_TIME = "00:00"
    }
}