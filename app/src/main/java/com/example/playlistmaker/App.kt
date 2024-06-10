package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.player.di.mediaPlayerModule
import com.example.playlistmaker.player.di.playerViewModelModule
import com.example.playlistmaker.search.di.networkClientModule
import com.example.playlistmaker.search.di.searchViewModelModule
import com.example.playlistmaker.search.di.tracksInteractorModule
import com.example.playlistmaker.search.di.tracksRepositoryModule
import com.example.playlistmaker.settings.ui.THEME
import com.example.playlistmaker.settings.ui.THEME_KEY
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    private var darkTheme = false
    override fun onCreate() {
        super.onCreate()

        val sharedPrefs = getSharedPreferences(THEME, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(THEME_KEY, false)
        switchTheme(darkTheme)

        startKoin {
            androidContext(this@App)
            modules(
                mediaPlayerModule,
                playerViewModelModule,
                tracksInteractorModule,
                tracksRepositoryModule,
                networkClientModule,
                searchViewModelModule
            )
        }
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