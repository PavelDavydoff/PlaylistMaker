package com.example.playlistmaker.player.data

import com.example.playlistmaker.library.domain.api.FavoriteInteractor
import com.example.playlistmaker.library.domain.api.PlaylistInteractor
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.player.domain.api.TrackPlaylistRepository
import com.example.playlistmaker.search.domain.models.Track

class TrackPlaylistRepositoryImpl(private val favoriteInteractor: FavoriteInteractor, private val playlistInteractor: PlaylistInteractor): TrackPlaylistRepository {
    override fun addTrack(track: Track, playlist: Playlist): Boolean {
        val tracksList = mutableListOf<String>()
        tracksList.addAll(toTracksList(playlist.tracks))
        for (trackName in tracksList) {
            if (trackName == track.trackName) {
                return false
            }
        }
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

    private fun fromTracksList(tracks: List<String>): String {
        return tracks.joinToString(separator = ",")
    }

    private fun toTracksList(tracksString: String): List<String> {
        return tracksString.split(",").map { it }
    }
}