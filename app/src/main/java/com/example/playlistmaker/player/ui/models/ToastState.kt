package com.example.playlistmaker.player.ui.models

sealed interface ToastState {
    data object None : ToastState
    data class Show(val additionalMessage: String) : ToastState
}