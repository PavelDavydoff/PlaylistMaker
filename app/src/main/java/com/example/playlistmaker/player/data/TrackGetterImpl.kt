package com.example.playlistmaker.player.data

import android.content.Intent
import com.example.playlistmaker.player.domain.Track
import com.example.playlistmaker.player.domain.TrackGetter
import com.example.playlistmaker.search.data.dto.TrackDto

class TrackGetterImpl: TrackGetter {
    override fun getTrack(key: String, intent: Intent): Track {
        val track = intent.getSerializableExtra(key) as TrackDto
        return Track(track.trackName, track.artistName, track.trackTimeMillis, track.artworkUrl100,
            track.collectionName,track.releaseDate,track.primaryGenreName,track.country,track.previewUrl)
    }
}