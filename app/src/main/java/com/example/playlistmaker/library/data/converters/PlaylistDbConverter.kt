package com.example.playlistmaker.library.data.converters

import com.example.playlistmaker.library.db.entity.PlaylistEntity
import com.example.playlistmaker.library.domain.models.Playlist

class PlaylistDbConverter {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.playlistId,
            playlist.name,
            playlist.description,
            playlist.filePath,
            playlist.tracks,
            playlist.tracksCount
        )
    }

    fun map(playlistEntity: PlaylistEntity): Playlist {
        return Playlist(
            playlistEntity.playlistId,
            playlistEntity.name,
            playlistEntity.description,
            playlistEntity.filePath,
            playlistEntity.tracks,
            playlistEntity.tracksCount
        )
    }
}