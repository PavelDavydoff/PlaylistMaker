package com.example.playlistmaker.settings.presentaion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.ThemeInteractor
import com.example.playlistmaker.settings.ui.models.ThemeState

class SettingsViewModel(private val themeInteractor: ThemeInteractor) : ViewModel() {
    private val themeLiveData = MutableLiveData<ThemeState>()
    fun observeTheme(): LiveData<ThemeState> = themeLiveData

    fun getTheme(): Boolean{
        return themeInteractor.getThemePrefs()
    }

    fun switchTheme(isChecked: Boolean){
        if (isChecked) {
            renderState(ThemeState.Dark)
        } else {
            renderState(ThemeState.Light)
        }
    }

    fun setTheme(isChecked: Boolean){
        themeInteractor.setThemePrefs(isChecked)
    }

    private fun renderState(state: ThemeState){
        themeLiveData.postValue(state)
    }
}