package com.example.playlistmaker.settings.domain

interface ThemeInteractor {
    fun getThemePrefs(): Boolean
    fun setThemePrefs(isChecked: Boolean)
}