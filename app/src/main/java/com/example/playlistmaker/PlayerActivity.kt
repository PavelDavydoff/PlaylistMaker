package com.example.playlistmaker

import android.content.Intent
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import java.util.Locale

private const val INTENT_KEY = "key"

class PlayerActivity : AppCompatActivity() {

    private lateinit var artwork: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var collectionNameV: TextView
    private lateinit var trackTime: TextView
    private lateinit var year: TextView
    private lateinit var genre: TextView
    private lateinit var country: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.player)

        artwork = findViewById(R.id.artwork)
        trackName = findViewById(R.id.trackName)
        artistName = findViewById(R.id.artistName)
        collectionNameV = findViewById(R.id.collectionNameV)
        trackTime = findViewById(R.id.lengthD)
        year = findViewById(R.id.yearV)
        genre = findViewById(R.id.genreV)
        country = findViewById(R.id.countryV)
    }

    override fun onResume() {
        super.onResume()

        val gson = Gson()
        val json = intent.getStringExtra(INTENT_KEY)
        val track = gson.fromJson(json, Track::class.java)

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            val backIntent = Intent(this, SearchActivity::class.java)
            startActivity(backIntent)
        }

        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .centerCrop()
            .placeholder(R.drawable.placeholder312)
            .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.corner_2)))
            .into(artwork)

        trackName.text = track.trackName
        artistName.text = track.artistName
        collectionNameV.text = track.collectionName
        trackTime.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        year.text = track.releaseDate.substring(0, 4)
        genre.text = track.primaryGenreName
        country.text = track.country
    }

}