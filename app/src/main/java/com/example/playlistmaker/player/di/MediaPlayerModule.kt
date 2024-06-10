package com.example.playlistmaker.player.di

import android.media.MediaPlayer
import org.koin.dsl.module

val mediaPlayerModule = module {
    factory { MediaPlayer() }
}