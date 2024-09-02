package com.example.playlistmaker.library.di

import com.example.playlistmaker.library.domain.api.FavoriteInteractor
import com.example.playlistmaker.library.domain.impl.FavoriteInteractorImpl
import org.koin.dsl.module

val favoriteInteractorModule = module {
    factory<FavoriteInteractor> {
        FavoriteInteractorImpl(get())
    }
}