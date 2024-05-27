package com.example.playlistmaker.main.ui

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class MainViewModel(application: Application): AndroidViewModel(application) {
    private val clickLiveData = MutableLiveData<Intent>()
}