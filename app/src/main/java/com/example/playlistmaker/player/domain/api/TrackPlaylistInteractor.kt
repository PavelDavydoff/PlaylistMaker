package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track

interface TrackPlaylistInteractor {
    fun addTrack(track: Track, playlist: Playlist): Boolean
}