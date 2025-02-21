package com.example.playlistmaker.library.di

import com.example.playlistmaker.library.domain.api.PlaylistInteractor
import com.example.playlistmaker.library.domain.impl.PlaylistInteractorImpl
import org.koin.dsl.module

val playlistInteractorModule = module {
    factory<PlaylistInteractor> {
        PlaylistInteractorImpl(get())
    }
}