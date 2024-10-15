package com.example.playlistmaker.library.data.converters

import com.example.playlistmaker.library.db.entity.TrackEntity
import com.example.playlistmaker.library.db.entity.TrackPlEntity
import com.example.playlistmaker.search.domain.models.Track

class TrackDbConverter {

    fun map(track: Track): TrackEntity {
        return TrackEntity(
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.previewUrl,
            track.country,
        )
    }

    fun map(track: TrackEntity): Track {
        return Track(
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.previewUrl,
            track.country,
            true
        )
    }

    fun convertToPl(track: Track): TrackPlEntity{
        return TrackPlEntity(track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.previewUrl,
            track.country,)
    }
}