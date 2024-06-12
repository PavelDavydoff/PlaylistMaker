package com.example.playlistmaker.player.ui.models

sealed interface PlayerState {
    data object Default : PlayerState
    data object Prepare : PlayerState
    data object Playing : PlayerState
    data object Paused : PlayerState
}