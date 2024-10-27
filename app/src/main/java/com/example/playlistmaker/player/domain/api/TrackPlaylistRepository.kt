package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track

interface TrackPlaylistRepository {
    fun addTrack(track: Track, playlist: Playlist): Boolean
    fun removeTrack(track: Track, playlist: Playlist)
    fun removeFromPlaylists(track: Track)
}