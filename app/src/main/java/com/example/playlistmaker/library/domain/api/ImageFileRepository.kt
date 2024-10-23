package com.example.playlistmaker.library.domain.api

import java.io.File

interface ImageFileRepository {
    fun saveImageToPrivateStorage(uri: String, fileName: String): File
}