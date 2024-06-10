package com.example.playlistmaker.player.presentation

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.player.ui.models.PlayerState
import com.example.playlistmaker.player.ui.models.SingleLiveEvent
import com.example.playlistmaker.player.ui.models.ToastState
import org.koin.java.KoinJavaComponent.inject

class PlayerViewModel : ViewModel() {

    private val player: MediaPlayer by inject(MediaPlayer::class.java)

    private val stateLiveData = MutableLiveData<PlayerState>()
    fun observeState(): LiveData<PlayerState> = stateLiveData

    private val showToast = SingleLiveEvent<String>()
    fun observeShowToast(): LiveData<String> = showToast

    private val toastState = MutableLiveData<ToastState>(ToastState.None)
    fun observeToastState(): LiveData<ToastState> = toastState

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

    fun play() {
        stateLiveData.postValue(PlayerState.Playing)
        player.start()
    }

    fun pause() {
        stateLiveData.postValue(PlayerState.Paused)
        player.pause()
    }

    fun release() {
        player.release()
    }

    companion object {
        private const val ERROR_MESSAGE = "URL = NULL"
    }
}