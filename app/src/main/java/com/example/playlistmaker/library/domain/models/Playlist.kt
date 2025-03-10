package com.example.playlistmaker.library.domain.models

data class Playlist(var playlistId: Int,
                    var name: String,
                    var description: String,
                    var filePath: String,
                    var tracks: String,
                    var tracksCount: Int)
