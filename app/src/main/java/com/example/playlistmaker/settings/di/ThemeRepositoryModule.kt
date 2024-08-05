package com.example.playlistmaker.settings.di

import com.example.playlistmaker.settings.data.ThemeRepositoryImpl
import com.example.playlistmaker.settings.domain.ThemeRepository
import org.koin.dsl.module

val themeRepositoryModule = module {
    factory<ThemeRepository> {
        ThemeRepositoryImpl(get())
    }
}