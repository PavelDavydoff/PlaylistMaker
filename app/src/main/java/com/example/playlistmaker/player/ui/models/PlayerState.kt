package com.example.playlistmaker.player.ui.models



sealed class PlayerState(val isPlaying: Boolean, val progress: String) {
    class Default : PlayerState(false, "00:00")
    class Prepare : PlayerState(false, "00:00")
    class Playing(progress: String) : PlayerState(true, progress)
    class Paused(progress: String) : PlayerState(false, progress)
}