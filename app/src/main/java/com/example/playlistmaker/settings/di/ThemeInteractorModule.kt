package com.example.playlistmaker.settings.di

import com.example.playlistmaker.settings.domain.ThemeInteractor
import com.example.playlistmaker.settings.domain.impl.ThemeInteractorImpl
import org.koin.dsl.module

val themeInteractorModule = module {
    factory<ThemeInteractor> {
        ThemeInteractorImpl(get())
    }
}