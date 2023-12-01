package com.example.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val trackList: ArrayList<Track> = ArrayList()
    trackList.add(Track("Smells like a teen spirit", "Nirvana", "5:01", getString(R.string.smells_like)))
    trackList.add(Track("Billie Jean", "Michael Jackson", "4:35", getString(R.string.billie_jean)))
    trackList.add(Track("Stayin' Alive", "Bee Gees", "4:10", getString(R.string.stayin_alive)))
    trackList.add(Track("Whole Lotta Love", "Led Zeppelin", "5:33", getString(R.string.whole_lotta)))
    trackList.add(Track("Sweet Child O'Mine", "Guns N' Roses", "5:03", getString(R.string.sweet_child)))


    val searchButton = findViewById<Button>(R.id.search)
    searchButton.setOnClickListener {
        val searchIntent = Intent(this, SearchActivity::class.java)
        startActivity(searchIntent)
    }

    val libraryButton = findViewById<Button>(R.id.library)
    libraryButton.setOnClickListener {
        val libraryIntent = Intent(this, LibraryActivity::class.java)
        startActivity(libraryIntent)
    }

    val settingsButton = findViewById<Button>(R.id.settings)
    settingsButton.setOnClickListener {
        val settingsIntent = Intent (this, SettingsActivity::class.java)
        startActivity(settingsIntent)
    }

}
}