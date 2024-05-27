package com.example.playlistmaker.search.data

import android.icu.text.SimpleDateFormat
import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.domain.models.Track
import java.util.Locale

object TrackTime {
    fun get(track: Track): String{
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
    }
}