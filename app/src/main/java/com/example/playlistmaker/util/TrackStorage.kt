package com.example.playlistmaker.util

interface TrackStorage {
    fun setTrack(track: String)
    fun getTrack(): String
}