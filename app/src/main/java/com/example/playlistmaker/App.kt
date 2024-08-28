package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.library.di.dataModule
import com.example.playlistmaker.library.di.favoriteInteractorModule
import com.example.playlistmaker.library.di.favoriteRepositoryModule
import com.example.playlistmaker.library.di.libraryViewModel
import com.example.playlistmaker.player.di.mediaPlayerModule
import com.example.playlistmaker.player.di.playerViewModelModule
import com.example.playlistmaker.search.di.historyInteractorModule
import com.example.playlistmaker.search.di.historyRepositoryModule
import com.example.playlistmaker.search.di.networkClientModule
import com.example.playlistmaker.search.di.searchViewModelModule
import com.example.playlistmaker.search.di.tracksInteractorModule
import com.example.playlistmaker.search.di.tracksRepositoryModule
import com.example.playlistmaker.settings.di.settingsViewModelModule
import com.example.playlistmaker.settings.di.themeInteractorModule
import com.example.playlistmaker.settings.di.themeRepositoryModule
import com.example.playlistmaker.settings.ui.THEME
import com.example.playlistmaker.settings.ui.THEME_KEY
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

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
                libraryViewModel,
                tracksInteractorModule,
                tracksRepositoryModule,
                networkClientModule,
                searchViewModelModule,
                settingsViewModelModule,
                themeInteractorModule,
                themeRepositoryModule,
                historyRepositoryModule,
                historyInteractorModule,
                dataModule,
                favoriteInteractorModule,
                favoriteRepositoryModule
            )
        }
    }

    companion object {

        private var darkTheme = false

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
}