package com.example.playlistmaker.library.domain.api

interface ImageFileInteractor {
    fun saveImageToPrivateStorage(uri: String, fileName: String): String
}