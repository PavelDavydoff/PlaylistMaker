package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.player.data.TrackTime
import com.example.playlistmaker.player.domain.MediaPlayerInteractor
import com.example.playlistmaker.player.presentation.PlayerViewModel
import com.example.playlistmaker.player.ui.models.PlayerState
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.SearchActivity.Companion.INTENT_KEY
import com.example.playlistmaker.util.Creator

class PlayerActivity : AppCompatActivity() {


    private var handler: Handler? = null
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var playButton: ImageView
    private lateinit var playTime: TextView
    private lateinit var player: MediaPlayerInteractor

    private lateinit var viewModel: PlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val track = intent.getSerializableExtra(INTENT_KEY) as Track

        player = Creator.mediaPlayerInteractorProvider()

        viewModel = ViewModelProvider(this, PlayerViewModel.getViewModelFactory())[PlayerViewModel::class.java]

        binding.backButton.setOnClickListener {
            Log.d("PlayerActivity", "${player.playerState}")
            finish()
        }

        Glide.with(this)
            .load(track.artworkUrl512)
            .centerCrop()
            .placeholder(R.drawable.placeholder312)
            .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.corner_2)))
            .into(binding.artwork)


        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.collectionNameV.text = track.collectionName
        binding.lengthD.text = TrackTime.get(track)
        binding.yearV.text = track.releaseDate.substring(0, 4)
        binding.genreV.text = track.primaryGenreName
        binding.countryV.text = track.country
        playTime = findViewById(R.id.playTime)
        handler = Handler(Looper.getMainLooper())
        playButton = binding.playButton
        playTime.text = "00:00"
        player.url = track.previewUrl

        val timer = TrackTimer { text ->
            playTime.text = text
            player.playerState == MediaPlayerInteractorImpl.STATE_PLAYING
        }

        viewModel.observeState().observe(this){
            render(it)
        }


        if (player.url != null) {
            player.prepare { playButton.setImageResource(R.drawable.play_button) }
            playButton.setOnClickListener {
                player.playbackControl(
                    {
                        viewModel.pause()
                    },
                    {
                        viewModel.play()
                        timer.start()
                    }
                )
            }
        } else {
            playButton.setOnClickListener {
                showToast()
            }
        }
    }

    private fun render(state: PlayerState){
        when(state){
            is PlayerState.Playing -> playButton.setImageResource(R.drawable.pause_button)
            is PlayerState.Paused -> playButton.setImageResource(R.drawable.play_button)
        }
    }

    private fun showToast(){
        Toast.makeText(applicationContext, "Url = null", Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
        player.pause { playButton.setImageResource(R.drawable.play_button) }
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}