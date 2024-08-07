package com.example.playlistmaker.search.di

import com.example.playlistmaker.search.domain.api.HistoryInteractor
import com.example.playlistmaker.search.domain.impl.HistoryInteractorImpl
import org.koin.dsl.module

val historyInteractorModule = module {
    factory<HistoryInteractor> {
        HistoryInteractorImpl(get())
    }
}