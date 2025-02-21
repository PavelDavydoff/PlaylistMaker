package com.example.playlistmaker.library.di

import com.example.playlistmaker.library.data.ImageFileRepositoryImpl
import com.example.playlistmaker.library.domain.api.ImageFileRepository
import org.koin.dsl.module

val imageFileRepositoryModule = module {
    factory<ImageFileRepository> {
        ImageFileRepositoryImpl(get())
    }
}