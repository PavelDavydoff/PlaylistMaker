package com.example.playlistmaker.library.domain.impl

import com.example.playlistmaker.library.domain.api.PlaylistInteractor
import com.example.playlistmaker.library.domain.api.PlaylistRepository
import com.example.playlistmaker.library.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val repository: PlaylistRepository) : PlaylistInteractor {
    override fun addNewPlaylist(playlist: Playlist) {
        repository.addNewPlaylist(playlist)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }

    override fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }

    override fun getPlaylist(id: Int): Playlist {
        return repository.getPlaylist(id)
    }

    override fun deletePlaylist(playlist: Playlist){
        repository.deletePlaylist(playlist)
    }
}