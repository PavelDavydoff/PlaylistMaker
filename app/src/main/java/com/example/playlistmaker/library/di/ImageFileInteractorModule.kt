package com.example.playlistmaker.library.di

import com.example.playlistmaker.library.domain.api.ImageFileInteractor
import com.example.playlistmaker.library.domain.impl.ImageFileInteractorImpl
import org.koin.dsl.module

val imageFileInteractorModule = module {
    factory<ImageFileInteractor> {
        ImageFileInteractorImpl(get())
    }
}