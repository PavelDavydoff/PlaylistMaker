package com.example.playlistmaker.player.data

import android.util.Log
import com.example.playlistmaker.library.domain.api.FavoriteInteractor
import com.example.playlistmaker.library.domain.api.PlaylistInteractor
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.player.domain.api.TrackPlaylistRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class TrackPlaylistRepositoryImpl(
    private val favoriteInteractor: FavoriteInteractor,
    private val playlistInteractor: PlaylistInteractor
) : TrackPlaylistRepository {
    override fun addTrack(track: Track, playlist: Playlist): Boolean {
        val tracksList = mutableListOf<String>()
        tracksList.addAll(toTracksList(playlist.tracks))
        for (trackName in tracksList) {
            if (trackName == track.trackName) {
                Log.d("addTrack", "if")
                return false
            }
        }
        Log.d("addTrack", tracksList.toString())
        tracksList.add(track.trackName)
        val tracks = fromTracksList(tracksList)
        playlist.tracksCount++
        val playlist2 = Playlist(
            playlist.playlistId,
            playlist.name,
            playlist.description,
            playlist.filePath,
            tracks,
            playlist.tracksCount
        )
        playlistInteractor.addNewPlaylist(playlist2)
        favoriteInteractor.addToPlaylists(track)
        return true
    }

    override fun removeTrack(track: Track, playlist: Playlist) {
        Log.d("removeTrack0", playlist.toString())
        val tracksList = mutableListOf<String>()
        Log.d("removeTrack1", tracksList.toString())
        tracksList.addAll(toTracksList(playlist.tracks))
        Log.d("removeTrack2", tracksList.toString())
        tracksList.remove(track.trackName)
        Log.d("removeTrack3", tracksList.toString())
        val tracks = fromTracksList(tracksList)
        Log.d("removeTrack4", tracks)
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
        Log.d("removeTrack5", playlist2.toString())
        removeFromPlaylists(track)
    }

    override fun removeFromPlaylists(track: Track) {
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

    private fun fromTracksList(tracks: List<String>): String {
        return tracks.joinToString(separator = ",")
    }

    private fun toTracksList(tracksString: String): List<String> {
        return tracksString.split(",").map { it }
    }
}