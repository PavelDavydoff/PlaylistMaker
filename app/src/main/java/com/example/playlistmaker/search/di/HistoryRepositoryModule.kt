package com.example.playlistmaker.search.di

import com.example.playlistmaker.search.data.HistoryRepositoryImpl
import com.example.playlistmaker.search.domain.api.HistoryRepository
import org.koin.dsl.module

val historyRepositoryModule = module{
    factory<HistoryRepository> {
        HistoryRepositoryImpl(get())
    }
}