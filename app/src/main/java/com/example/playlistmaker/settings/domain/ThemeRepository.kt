package com.example.playlistmaker.settings.domain

interface ThemeRepository {
    fun getThemePrefs(): Boolean
    fun setThemePrefs(isChecked: Boolean)
}