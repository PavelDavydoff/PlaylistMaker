package com.example.playlistmaker.search.ui.models

sealed interface ToastState {
    data object None : ToastState
    data class Show(val additionalMessage: String) : ToastState
}