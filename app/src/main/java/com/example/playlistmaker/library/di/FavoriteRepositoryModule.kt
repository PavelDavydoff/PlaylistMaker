package com.example.playlistmaker.library.di

import com.example.playlistmaker.library.data.FavoriteRepositoryImpl
import com.example.playlistmaker.library.data.converters.TrackDbConverter
import com.example.playlistmaker.library.domain.api.FavoriteRepository
import org.koin.dsl.module

val favoriteRepositoryModule = module {
    factory<FavoriteRepository> {
        FavoriteRepositoryImpl(get(), get())
    }

    factory {
        TrackDbConverter()
    }
}