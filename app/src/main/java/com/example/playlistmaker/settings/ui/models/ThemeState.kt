package com.example.playlistmaker.settings.ui.models

sealed interface ThemeState {
    data object Light : ThemeState
    data object Dark : ThemeState
}