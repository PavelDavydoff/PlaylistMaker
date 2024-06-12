package com.example.playlistmaker.search.di

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import org.koin.dsl.module

val networkClientModule = module {
    factory<NetworkClient> {
        RetrofitNetworkClient(get())
    }
}