package com.example.playlistmaker.player.presentation

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.api.FavoriteInteractor
import com.example.playlistmaker.library.domain.api.PlaylistInteractor
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.player.domain.api.TrackPlaylistInteractor
import com.example.playlistmaker.player.ui.models.AddTrackToastState
import com.example.playlistmaker.player.ui.models.PlayerState
import com.example.playlistmaker.player.ui.models.SingleLiveEvent
import com.example.playlistmaker.player.ui.models.ToastState
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val player: MediaPlayer,
    private val favoriteInteractor: FavoriteInteractor,
    private val playlistInteractor: PlaylistInteractor,
    private val trackPlaylistInteractor: TrackPlaylistInteractor
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

    private val playlistLiveData = MutableLiveData<MutableList<Playlist>>()
    fun observePlaylist(): LiveData<MutableList<Playlist>> = playlistLiveData

    private val trackToastState = SingleLiveEvent<AddTrackToastState>()
    fun observeTrackToast(): LiveData<AddTrackToastState> = trackToastState

    fun prepare(url: String?) {
        if (url == null) {
            showToast.postValue(ERROR_MESSAGE)
        } else {
            player.setDataSource(url)
            player.prepareAsync()
            player.setOnPreparedListener {
                stateLiveData.postValue(PlayerState.Prepare())
            }
            player.setOnCompletionListener {
                timerJob?.cancel()
                stateLiveData.postValue(PlayerState.Prepare())
            }
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

    fun stop() {
        player.stop()
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

    fun jsonToTrack(json: String): Track {
        val gson = Gson()
        val track = object : TypeToken<Track>() {}.type
        return gson.fromJson(json, track)
    }

    fun getPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect { playlists ->
                val listOfPlaylists = playlists.toMutableList()
                playlistLiveData.postValue(listOfPlaylists)
            }
        }
    }

    fun addTrackToPlaylist(playlist: Playlist, track: Track) {
        val isTrackAdded = trackPlaylistInteractor.addTrack(track, playlist)
        if (isTrackAdded) {
            trackToastState.postValue(AddTrackToastState.IsAdded(playlist.name))
        } else {
            trackToastState.postValue(AddTrackToastState.IsNotAdded(playlist.name))
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
