package com.example.playlistmaker.search.di

import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import org.koin.dsl.module

val tracksInteractorModule = module {
    factory<TracksInteractor> {
        TracksInteractorImpl(get())
    }
}