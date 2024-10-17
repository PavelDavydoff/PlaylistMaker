package com.example.playlistmaker.player.ui.models

sealed interface AddTrackToastState{
    class IsAdded(val playlistName: String): AddTrackToastState
    class IsNotAdded(val playlistName: String): AddTrackToastState
}
