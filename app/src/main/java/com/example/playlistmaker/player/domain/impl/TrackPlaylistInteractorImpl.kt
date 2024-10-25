package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.player.domain.api.TrackPlaylistInteractor
import com.example.playlistmaker.player.domain.api.TrackPlaylistRepository
import com.example.playlistmaker.search.domain.models.Track

class TrackPlaylistInteractorImpl(private val trackPlaylistRepository: TrackPlaylistRepository): TrackPlaylistInteractor {
    override fun addTrack(track: Track, playlist: Playlist): Boolean {
        return trackPlaylistRepository.addTrack(track, playlist)
    }

    override fun removeTrack(track: Track, playlist: Playlist){
        trackPlaylistRepository.removeTrack(track, playlist)
    }
}