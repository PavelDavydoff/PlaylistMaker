package com.example.playlistmaker.library.ui.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.api.FavoriteInteractor
import com.example.playlistmaker.library.domain.api.PlaylistInteractor
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.library.ui.models.DetailsState
import com.example.playlistmaker.player.domain.api.TrackPlaylistRepository
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import kotlinx.coroutines.launch

class DetailsPlaylistViewModel(private val favoriteInteractor: FavoriteInteractor, private val trackPlaylistRepository: TrackPlaylistRepository, private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val stateLiveData = MutableLiveData<DetailsState>()
    fun observeState(): LiveData<DetailsState> = stateLiveData

    private fun toTracksList(tracksString: String): List<String> {
        return tracksString.split(",").map { it }
    }

    /*private fun playlistToMessage(playlist: Playlist, tracks: List<Track>): String {
        var result = "${playlist.name}\n${playlist.description}"
        result+=
        return result
    }*/

    fun getTracks(id: Int) {

        viewModelScope.launch {

            val playlist = playlistInteractor.getPlaylist(id)
            val currentTracksList = toTracksList(playlist.tracks)
            val resultList = mutableListOf<Track>()
            var globalListOfTracks: MutableList<Track>
            Log.d("Треки", currentTracksList.toString())

            favoriteInteractor.getTracksFromPlaylist().collect { tracks ->
                globalListOfTracks = tracks.toMutableList()
                for (trackA in currentTracksList) {
                    for (trackB in globalListOfTracks) {
                        if (trackA == trackB.trackName) {
                            //Наверняка здесь добавляется лишний раз
                            Log.d("ViewModelCycle", "add ${trackB.trackName}")
                            resultList.add(trackB)
                        }
                    }
                }
                Log.d("DetailsViewModel", resultList.map { track -> track.trackName  }.toString())
                stateLiveData.postValue(DetailsState(playlist, resultList))
            }
        }
    }



    fun removeTrack(track: Track, playlist: Playlist){
        trackPlaylistRepository.removeTrack(track, playlist)
    }

    fun trackToJson(track: Track): String{
        val gson = Gson()
        return gson.toJson(track)
    }
}