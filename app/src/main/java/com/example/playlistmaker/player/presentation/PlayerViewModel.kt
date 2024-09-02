package com.example.playlistmaker.player.presentation

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.api.FavoriteInteractor
import com.example.playlistmaker.player.ui.models.PlayerState
import com.example.playlistmaker.player.ui.models.SingleLiveEvent
import com.example.playlistmaker.player.ui.models.ToastState
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val player: MediaPlayer,
    private val favoriteInteractor: FavoriteInteractor
) : ViewModel() {

    private var timerJob: Job? = null

    private val stateLiveData = MutableLiveData<PlayerState>()
    fun observeState(): LiveData<PlayerState> = stateLiveData

    private val showToast = SingleLiveEvent<String>()
    fun observeShowToast(): LiveData<String> = showToast

    private val toastState = MutableLiveData<ToastState>(ToastState.None)
    fun observeToastState(): LiveData<ToastState> = toastState

    private val favoriteState = MutableLiveData<Boolean>()
    fun observeFavorite(): LiveData<Boolean> = favoriteState

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

    fun onFavoriteClicked(track: Track) {
        viewModelScope.launch {
            if (!track.isFavorite) {
                track.isFavorite = true
                favoriteInteractor.addToFavorite(track)
                favoriteState.postValue(true)
            } else {
                track.isFavorite = false
                favoriteInteractor.removeFromFavorite(track)
                favoriteState.postValue(false)
            }
        }
    }

    fun checkFavorite(track: Track) {
        viewModelScope.launch {
            val listOfTracks = favoriteInteractor.getFavorites()
            listOfTracks.collect { tracks ->
                tracks.forEach {
                    if (it.trackName == track.trackName) {
                        track.isFavorite = true
                        favoriteState.postValue(true)
                    }
                }
            }
        }
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
