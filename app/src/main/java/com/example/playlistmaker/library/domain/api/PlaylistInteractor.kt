package com.example.playlistmaker.library.domain.api

import com.example.playlistmaker.library.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    fun addNewPlaylist(playlist: Playlist)

    fun getPlaylists(): Flow<List<Playlist>>
}