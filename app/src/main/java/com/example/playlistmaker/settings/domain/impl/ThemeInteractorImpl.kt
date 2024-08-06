package com.example.playlistmaker.settings.domain.impl

import com.example.playlistmaker.settings.domain.ThemeInteractor
import com.example.playlistmaker.settings.domain.ThemeRepository

class ThemeInteractorImpl(private val repository: ThemeRepository): ThemeInteractor {
    override fun getThemePrefs(): Boolean{
        return repository.getThemePrefs()
    }

    override fun setThemePrefs(isChecked: Boolean){
        repository.setThemePrefs(isChecked)
    }
}