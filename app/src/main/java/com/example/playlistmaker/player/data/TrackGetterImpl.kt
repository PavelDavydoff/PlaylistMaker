package com.example.playlistmaker.player.data

import android.content.Intent
import com.example.playlistmaker.player.domain.Track
import com.example.playlistmaker.player.domain.TrackGetter

class TrackGetterImpl: TrackGetter {
    override fun getTrack(key: String, intent: Intent): Track {
        return intent.getSerializableExtra(key) as Track
    }
}