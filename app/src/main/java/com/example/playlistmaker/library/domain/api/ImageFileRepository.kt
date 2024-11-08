package com.example.playlistmaker.library.domain.api

interface ImageFileRepository {
    fun saveImageToPrivateStorage(uri: String, fileName: String): String
}