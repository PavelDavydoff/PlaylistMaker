package com.example.playlistmaker.player.data

import android.content.Intent
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.player.domain.TrackGetter

class TrackGetterImpl: TrackGetter {
    override fun getTrack(key: String, intent: Intent): Track {
        return intent.getSerializableExtra(key) as Track
        /*return Track(track.trackName, track.artistName, track.trackTimeMillis, track.artworkUrl100,
            track.collectionName,track.releaseDate,track.primaryGenreName,track.country,track.previewUrl)*/
    }
}