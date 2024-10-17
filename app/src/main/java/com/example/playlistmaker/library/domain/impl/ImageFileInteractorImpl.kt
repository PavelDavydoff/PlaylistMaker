package com.example.playlistmaker.library.domain.impl

import com.example.playlistmaker.library.domain.api.ImageFileInteractor
import com.example.playlistmaker.library.domain.api.ImageFileRepository

class ImageFileInteractorImpl(private val imageFileRepository: ImageFileRepository): ImageFileInteractor {
    override fun saveImageToPrivateStorage(uri: String) {
        imageFileRepository.saveImageToPrivateStorage(uri)
    }
}