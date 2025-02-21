package com.example.playlistmaker.library.di

import com.example.playlistmaker.library.data.PlaylistRepositoryImpl
import com.example.playlistmaker.library.data.converters.PlaylistDbConverter
import com.example.playlistmaker.library.domain.api.PlaylistRepository
import org.koin.dsl.module

val playlistRepositoryModule = module {
    factory<PlaylistRepository> {
        PlaylistRepositoryImpl(get(),get())
    }

    factory {
        PlaylistDbConverter()
    }
}