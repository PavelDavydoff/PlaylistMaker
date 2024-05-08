package com.example.playlistmaker.player.domain

import android.content.Intent
import com.example.playlistmaker.player.domain.Track

interface TrackGetter {
    fun getTrack(key: String, intent: Intent) : Track
}