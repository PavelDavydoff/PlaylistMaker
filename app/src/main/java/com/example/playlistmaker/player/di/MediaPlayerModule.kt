package com.example.playlistmaker.player.di

import com.example.playlistmaker.player.domain.MediaPlayerInteractor
import com.example.playlistmaker.player.ui.MediaPlayerInteractorImpl
import org.koin.dsl.module

val mediaPlayerModule = module {
    factory<MediaPlayerInteractor> { MediaPlayerInteractorImpl() }
}