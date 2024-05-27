package com.example.playlistmaker.player.domain

import android.content.Intent
import com.example.playlistmaker.search.domain.models.Track

interface TrackGetter {
    fun getTrack(key: String, intent: Intent) : Track
}