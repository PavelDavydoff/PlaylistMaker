package com.example.playlistmaker.util

import android.content.Context
import com.example.playlistmaker.player.domain.MediaPlayerInteractor
import com.example.playlistmaker.player.ui.MediaPlayerInteractorImpl
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl

object Creator {
    fun getTracksRepository(context: Context): TracksRepository{
        return TracksRepositoryImpl(RetrofitNetworkClient(context))
    }
    fun provideTracksInteractor(context: Context): TracksInteractor{
        return TracksInteractorImpl(getTracksRepository(context))
    }
    fun mediaPlayerProvider(): MediaPlayerInteractor {
        return MediaPlayerInteractorImpl()
    }
}