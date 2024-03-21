package com.example.playlistmaker.presentation

import android.media.MediaPlayer
import com.example.playlistmaker.domain.MediaPlayerInteractor

class MediaPlayerInteractorImpl : MediaPlayerInteractor {
    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
    }

    override var url: String? = null

    private val player = MediaPlayer()
    override var playerState = STATE_DEFAULT

    override fun prepare(callback: () -> Unit) {
        player.setDataSource(url)
        player.prepareAsync()
        player.setOnPreparedListener {
            playerState = STATE_PREPARED
        }
        player.setOnCompletionListener {
            playerState = STATE_PREPARED
            callback()
        }
    }

    override fun start(callback: () -> Unit) {
        callback()
        player.start()
        playerState = STATE_PLAYING
    }

    override fun pause(callback: () -> Unit) {
        callback()
        player.pause()
        playerState = STATE_PAUSED
    }

    override fun release() {
        player.release()
    }

    override fun playbackControl(callback1: () -> Unit, callback2: () -> Unit) {
        when (playerState) {
            STATE_PLAYING -> {
                pause {callback1()}
            }

            STATE_PREPARED, STATE_PAUSED -> {
                start{callback2()}
            }
        }
    }
}