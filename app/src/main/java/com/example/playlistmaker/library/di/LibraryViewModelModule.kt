package com.example.playlistmaker.library.di

import com.example.playlistmaker.library.ui.presentation.FavoriteViewModel
import com.example.playlistmaker.library.ui.presentation.LibraryViewModel
import com.example.playlistmaker.library.ui.presentation.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val libraryViewModel = module {
    viewModel {
        LibraryViewModel()
    }

    viewModel {
        FavoriteViewModel(get())
    }

    viewModel {
        PlaylistsViewModel()
    }
}
