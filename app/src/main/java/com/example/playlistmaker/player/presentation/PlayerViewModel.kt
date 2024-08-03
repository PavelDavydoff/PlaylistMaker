package com.example.playlistmaker.player.presentation

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.ui.models.PlayerState
import com.example.playlistmaker.player.ui.models.SingleLiveEvent
import com.example.playlistmaker.player.ui.models.ToastState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val player: MediaPlayer) : ViewModel() {

    private var timerJob: Job? = null

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
            stateLiveData.postValue(PlayerState.Prepare())
        }
        player.setOnCompletionListener {
            timerJob?.cancel()
            stateLiveData.postValue(PlayerState.Prepare())
            Log.d("PlayerState", stateLiveData.value.toString())
        }
    }

    fun play() {
        player.start()
        stateLiveData.postValue(PlayerState.Playing(getCurrentPosition()))
        startTimer()
    }

    fun pause() {
        player.pause()
        timerJob?.cancel()
        stateLiveData.postValue(PlayerState.Paused(getCurrentPosition()))
    }

     private fun releasePlayer() {
        player.stop()
        player.release()
        stateLiveData.value = PlayerState.Default()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (player.isPlaying) {
                delay(TIMER_DELAY)
                stateLiveData.postValue(PlayerState.Playing(getCurrentPosition()))
            }
        }
    }

    private fun getCurrentPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(player.currentPosition)
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    companion object {
        private const val ERROR_MESSAGE = "URL = NULL"
        private const val TIMER_DELAY = 300L
    }
}
