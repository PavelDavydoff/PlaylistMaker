package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.settings.ui.THEME
import com.example.playlistmaker.settings.ui.THEME_KEY

class App: Application() {
    private var darkTheme = false
    override fun onCreate() {
        super.onCreate()

        val sharedPrefs = getSharedPreferences(THEME, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(THEME_KEY, false)
        switchTheme(darkTheme)
    }
    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}