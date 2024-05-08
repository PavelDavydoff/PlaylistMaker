package com.example.playlistmaker.player.domain

import androidx.lifecycle.LiveData

interface MediaPlayerInteractor {
    var playerState: Int
    var url: String?
    fun prepare(callback: () -> Unit)
    fun start(callback: () -> Unit)
    fun pause(callback: () -> Unit)
    fun playbackControl(callback1: () -> Unit, callback2: () -> Unit)
    fun release()
}