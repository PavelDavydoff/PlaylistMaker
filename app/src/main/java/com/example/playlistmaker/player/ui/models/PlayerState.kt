package com.example.playlistmaker.player.ui.models

sealed interface PlayerState{
    data object Playing: PlayerState
    data object Paused: PlayerState
}