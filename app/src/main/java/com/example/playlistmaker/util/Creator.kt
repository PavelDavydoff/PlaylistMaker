package com.example.playlistmaker.util

import com.example.playlistmaker.player.domain.MediaPlayerInteractor
import com.example.playlistmaker.player.ui.MediaPlayerInteractorImpl

object Creator {
    fun mediaPlayerProvider(): MediaPlayerInteractor {
        return MediaPlayerInteractorImpl()
    }
}