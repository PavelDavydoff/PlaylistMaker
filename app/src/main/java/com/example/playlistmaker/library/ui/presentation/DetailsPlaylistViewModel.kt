package com.example.playlistmaker.library.ui.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.api.FavoriteInteractor
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.library.ui.models.DetailsState
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

class DetailsPlaylistViewModel(private val favoriteInteractor: FavoriteInteractor) : ViewModel() {

    private val stateLiveData = MutableLiveData<DetailsState>()
    fun observeState(): LiveData<DetailsState> = stateLiveData

    fun jsonToPlaylist(json: String): Playlist {
        val gson = Gson()
        val playlist = object : TypeToken<Playlist>() {}.type
        return gson.fromJson(json, playlist)
    }

    private fun toTracksList(tracksString: String): List<String> {
        return tracksString.split(",").map { it }
    }

    fun getTracks(playlist: Playlist) {
        val tracksId = toTracksList(playlist.tracks)
        val resultList = mutableListOf<Track>()
        var listOfTracks: MutableList<Track>
        viewModelScope.launch {
            favoriteInteractor.getTracksFromPlaylist().collect { tracks ->
                listOfTracks = tracks.toMutableList()
                for (trackA in tracksId) {
                    for (trackB in listOfTracks) {
                        if (trackA == trackB.trackName) {
                            resultList.add(trackB)
                        }
                    }
                }
                if (resultList.isEmpty()) {
                    stateLiveData.postValue(DetailsState.Empty)
                } else {
                    stateLiveData.postValue(DetailsState.Content(resultList))
                }
            }
        }
    }
}