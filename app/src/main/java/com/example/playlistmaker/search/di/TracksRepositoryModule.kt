package com.example.playlistmaker.search.di

import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.api.TracksRepository
import org.koin.dsl.module

val tracksRepositoryModule = module {
    factory<TracksRepository> {
        TracksRepositoryImpl(get())
    }
}