package com.example.playlistmaker.settings.presentaion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.App
import com.example.playlistmaker.settings.domain.ThemeInteractor
import com.example.playlistmaker.settings.ui.models.ThemeState

class SettingsViewModel(private val themeInteractor: ThemeInteractor) : ViewModel() {
    private val themeLiveData = MutableLiveData<ThemeState>()
    fun observeTheme(): LiveData<ThemeState> = themeLiveData

    fun switchTheme(isChecked: Boolean){
        App.switchTheme(isChecked)
    }

    fun renderState(state: ThemeState){
        themeLiveData.postValue(state)
    }
}