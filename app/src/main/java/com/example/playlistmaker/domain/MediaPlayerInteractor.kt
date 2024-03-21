package com.example.playlistmaker.domain

interface MediaPlayerInteractor {
    var url: String?
    var playerState: Int
    fun prepare(callback: () -> Unit)
    fun start(callback: () -> Unit)
    fun pause(callback: () -> Unit)
    fun playbackControl(callback1: () -> Unit, callback2: () -> Unit)
    fun release()
}