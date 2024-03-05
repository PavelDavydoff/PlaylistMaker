package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private lateinit var binding: ActivityPlayerBinding
    private var url: String? = null
    private var mediaPlayer = MediaPlayer()
    private lateinit var playButton: ImageView
    private var playerState = STATE_DEFAULT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val track = intent.getSerializableExtra(SearchActivity.INTENT_KEY) as Track

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        Glide.with(this)
            .load(track.artworkUrl512)
            .centerCrop()
            .placeholder(R.drawable.placeholder312)
            .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.corner_2)))
            .into(binding.artwork)

        url = track.previewUrl
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.collectionNameV.text = track.collectionName
        binding.lengthD.text = track.trackTime
        binding.yearV.text = track.releaseDate.substring(0, 4)
        binding.genreV.text = track.primaryGenreName
        binding.countryV.text = track.country

        playButton = findViewById(R.id.playButton)

        if (url != null) {
            preparePlayer()
            playButton.setOnClickListener {
                playbackControl()
            }
        } else {
            playButton.setOnClickListener {
                Toast.makeText(applicationContext, "Url = null", Toast.LENGTH_LONG).show()
            }
        }


    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playButton.setImageResource(R.drawable.play_button)
            playerState = STATE_PREPARED
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setImageResource(R.drawable.pause_button)
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.setImageResource(R.drawable.play_button)
        playerState = STATE_PAUSED
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }
}