package com.example.playlistmaker.library.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import androidx.core.net.toUri
import com.example.playlistmaker.R
import com.example.playlistmaker.library.domain.api.ImageFileRepository
import java.io.File
import java.io.FileOutputStream

class ImageFileRepositoryImpl(private val context: Context) : ImageFileRepository {
    override fun saveImageToPrivateStorage(uri: String) {

        val url = uri.toUri()

        val filePath = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            context.getString(
                R.string.myalbum
            )
        )

        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val file = File(filePath, context.getString(R.string.playlistpicture_jpg))

        val inputStream = context.contentResolver.openInputStream(url)

        val outputStream = FileOutputStream(file)

        BitmapFactory.decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }
}