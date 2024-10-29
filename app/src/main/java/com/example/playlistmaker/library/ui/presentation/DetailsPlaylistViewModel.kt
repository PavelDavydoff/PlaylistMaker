package com.example.playlistmaker.library.ui.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.api.FavoriteInteractor
import com.example.playlistmaker.library.domain.api.PlaylistInteractor
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.library.ui.models.DetailsState
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class DetailsPlaylistViewModel(private val favoriteInteractor: FavoriteInteractor, private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val stateLiveData = MutableLiveData<DetailsState>()
    fun observeState(): LiveData<DetailsState> = stateLiveData

    private fun toTracksList(tracksString: String): List<String> {
        return tracksString.split(",").map { it }
    }

    private fun removeTrack(track: Track, playlist: Playlist) {

        val tracksList = mutableListOf<String>()

        tracksList.addAll(toTracksList(playlist.tracks))

        tracksList.remove(track.trackName)

        val tracks = tracksList.joinToString(",")
        playlist.tracksCount--
        val playlist2 = Playlist(
            playlist.playlistId,
            playlist.name,
            playlist.description,
            playlist.filePath,
            tracks,
            playlist.tracksCount
        )
        playlistInteractor.addNewPlaylist(playlist2)
        removeFromPlaylists(track)
    }

    private fun removeFromPlaylists(track: Track) {
        var isMatch = false
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            playlistInteractor.getPlaylists().collect { playlists ->
                val listOfPlaylists = playlists.toMutableList()
                for (playlist in listOfPlaylists){
                    if (isMatch) {
                        break
                    }
                    val tracks = toTracksList(playlist.tracks)
                    for (trackName in tracks){
                        if (track.trackName == trackName){
                            isMatch = true
                            break
                        }
                    }
                }
                if (!isMatch){
                    favoriteInteractor.deleteFromPlaylists(track)
                }
            }
        }
    }

    fun getTracks(id: Int) {

        viewModelScope.launch {

            favoriteInteractor.getTracksFromPlaylist().collect { tracks ->

                val playlist = playlistInteractor.getPlaylist(id)

                val currentTracksList = toTracksList(playlist.tracks)
                val resultList = mutableListOf<Track>()
                val globalListOfTracks = tracks.toMutableList()

                for (trackA in currentTracksList) {
                    for (trackB in globalListOfTracks) {
                        if (trackA == trackB.trackName) {
                            resultList.add(trackB)
                        }
                    }
                }
                if (resultList.isEmpty()){
                    stateLiveData.postValue(DetailsState.Empty(playlist))
                } else {
                    stateLiveData.postValue(DetailsState.Content(playlist, resultList))
                }
            }
        }
    }

    fun deleteTrack(track: Track, playlist: Playlist){
        removeTrack(track, playlist)
        getTracks(playlist.playlistId)
    }

    fun deletePlaylist(playlist: Playlist, tracks: List<Track>){

        playlistInteractor.deletePlaylist(playlist)

        for (track in  tracks){
            removeFromPlaylists(track)
        }
    }

    fun trackToJson(track: Track): String{
        val gson = Gson()
        return gson.toJson(track)
    }
}