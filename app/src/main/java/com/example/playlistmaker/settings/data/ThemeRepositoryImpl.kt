package com.example.playlistmaker.settings.data

import android.content.Context
import com.example.playlistmaker.settings.domain.ThemeRepository
import com.example.playlistmaker.settings.ui.THEME
import com.example.playlistmaker.settings.ui.THEME_KEY

class ThemeRepositoryImpl(context: Context) : ThemeRepository {

    private val sharedPrefs = context.getSharedPreferences(THEME, Context.MODE_PRIVATE)

    override fun getThemePrefs(): Boolean {
        return sharedPrefs.getBoolean(THEME_KEY, false)
    }
    override fun setThemePrefs(isChecked: Boolean){
        sharedPrefs.edit().putBoolean(THEME_KEY, isChecked).apply()
    }
}