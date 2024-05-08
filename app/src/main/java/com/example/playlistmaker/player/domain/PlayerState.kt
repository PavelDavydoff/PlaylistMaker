package com.example.playlistmaker.player.domain

sealed interface PlayerState{
    data object Playing: PlayerState
    data object Paused: PlayerState
}