package com.example.playlistmaker.player.ui

import android.icu.text.SimpleDateFormat
import android.os.Handler
import android.os.Looper
import java.util.Date

class TrackTimer(val callback: (String) -> Boolean) {

    private var handler = Handler(Looper.getMainLooper())

    private var time = 0L

    var text = "00:00"

    fun start() {
        val startTime = System.currentTimeMillis()
        handler.post(update(startTime))
    }

    private fun update(start: Long): Runnable {
        return object : Runnable {
            override fun run() {
                val current = System.currentTimeMillis()
                time = current - start
                text = formatMilliseconds(time)
                val isPlaying = callback(text)
                if (isPlaying) {
                    handler.postDelayed(this, 1000L)
                }
            }
        }
    }

    fun formatMilliseconds(milliseconds: Long): String {
        val format = SimpleDateFormat("mm:ss")
        return format.format(Date(milliseconds))
    }
}