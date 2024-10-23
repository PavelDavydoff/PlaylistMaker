package com.example.playlistmaker.library.domain.impl

import com.example.playlistmaker.library.domain.api.ImageFileInteractor
import com.example.playlistmaker.library.domain.api.ImageFileRepository
import java.io.File

class ImageFileInteractorImpl(private val imageFileRepository: ImageFileRepository): ImageFileInteractor {
    override fun saveImageToPrivateStorage(uri: String, fileName: String): File {
        return imageFileRepository.saveImageToPrivateStorage(uri, fileName)
    }
}