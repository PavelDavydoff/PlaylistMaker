package com.example.playlistmaker.library.domain.api

import java.io.File

interface ImageFileInteractor {
    fun saveImageToPrivateStorage(uri: String, fileName: String): File
}