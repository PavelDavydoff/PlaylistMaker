package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.player.data.TrackTime
import com.example.playlistmaker.player.presentation.PlayerViewModel
import com.example.playlistmaker.player.ui.models.PlayerState
import com.example.playlistmaker.player.ui.models.ToastState
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.SearchActivity.Companion.INTENT_KEY
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerActivity : AppCompatActivity() {

    private var handler: Handler? = null
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var playButton: ImageView
    private lateinit var playTime: TextView

    private val viewModel: PlayerViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val track = intent.getSerializableExtra(INTENT_KEY) as Track

        val previewUrl = track.previewUrl

        binding.backButton.setOnClickListener {
            Log.d("PlayerActivity", "${viewModel.observeState()}")
            onBackPressedDispatcher.onBackPressed()
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
        playTime.text = PlayerViewModel.ZERO_TIME

        var isPlaying = false

        viewModel.observePosition().observe(this){
            updateTimer(it)
        }

        viewModel.observeState().observe(this) {
            render(it)
        }

        viewModel.observeShowToast().observe(this) {
            showToast(it)
        }

        viewModel.observeToastState().observe(this) { toast ->
            if (toast is ToastState.Show) {
                showToast(toast.additionalMessage)
            }
        }

        viewModel.prepare(previewUrl)

        playButton.setOnClickListener {
            isPlaying = if (isPlaying) {
                viewModel.pause()
                false
            } else {
                viewModel.play()
                true
            }
        }
    }

    private fun render(state: PlayerState) {
        when (state) {
            is PlayerState.Prepare -> {
                playButton.setImageResource(R.drawable.play_button)
            }
            is PlayerState.Default -> {
                playButton.setImageResource(R.drawable.play_button)
            }
            is PlayerState.Playing -> {
                playButton.setImageResource(R.drawable.pause_button)
                handler?.post(updateTimer())
            }
            is PlayerState.Paused -> {
                handler?.removeCallbacksAndMessages(null)
                playButton.setImageResource(R.drawable.play_button)
            }
        }
    }

    private fun updateTimer(): Runnable{
        return object : Runnable {
            override fun run() {
                viewModel.updatePosition()
                Log.d("Timer", "work")
                handler?.postDelayed(this, 1000L)
            }
        }
    }

    private fun updateTimer(value: String){
        playTime.text = value
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
        Log.d("PlayerActivity", "onPause")
        viewModel.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("PlayerActivity", "onDestroy")
        viewModel.release()
    }
}