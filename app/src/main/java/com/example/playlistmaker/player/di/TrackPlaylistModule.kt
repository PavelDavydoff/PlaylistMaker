package com.example.playlistmaker.player.di

import com.example.playlistmaker.player.data.TrackPlaylistRepositoryImpl
import com.example.playlistmaker.player.domain.api.TrackPlaylistInteractor
import com.example.playlistmaker.player.domain.api.TrackPlaylistRepository
import com.example.playlistmaker.player.domain.impl.TrackPlaylistInteractorImpl
import org.koin.dsl.module

val trackPlaylistModule = module {
    factory<TrackPlaylistRepository> {
        TrackPlaylistRepositoryImpl(get(), get())
    }
    factory<TrackPlaylistInteractor> {
        TrackPlaylistInteractorImpl(get())
    }
}
