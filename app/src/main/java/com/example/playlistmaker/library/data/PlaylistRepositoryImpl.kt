package com.example.playlistmaker.library.data

import com.example.playlistmaker.library.data.converters.PlaylistDbConverter
import com.example.playlistmaker.library.db.PlaylistsDatabase
import com.example.playlistmaker.library.domain.api.PlaylistRepository
import com.example.playlistmaker.library.domain.models.Playlist

class PlaylistRepositoryImpl(
    private val database: PlaylistsDatabase,
    private val playlistDbConverter: PlaylistDbConverter
) : PlaylistRepository {
    override fun addNewPlaylist(playlist: Playlist) {
        database.playlistDao().insertPlaylist(playlistDbConverter.map(playlist))
    }
}