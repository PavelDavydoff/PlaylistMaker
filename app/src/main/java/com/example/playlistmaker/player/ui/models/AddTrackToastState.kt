package com.example.playlistmaker.player.ui.models

sealed interface AddTrackToastState{
    class IsAdded(val message: String): AddTrackToastState
    class IsNotAdded(val message: String): AddTrackToastState
}
