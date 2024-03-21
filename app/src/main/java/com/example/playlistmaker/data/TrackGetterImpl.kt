package com.example.playlistmaker.data

import android.content.Intent
import com.example.playlistmaker.domain.Track
import com.example.playlistmaker.domain.TrackGetter

class TrackGetterImpl: TrackGetter {
    override fun getTrack(key: String, intent: Intent): Track {
        return intent.getSerializableExtra(key) as Track
    }
}