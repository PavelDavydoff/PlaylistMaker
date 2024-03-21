package com.example.playlistmaker.domain

import android.content.Intent

interface TrackGetter {
    fun getTrack(ket: String, intent: Intent) : Track
}