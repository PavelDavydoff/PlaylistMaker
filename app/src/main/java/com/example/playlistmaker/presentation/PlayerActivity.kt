package com.example.playlistmaker.presentation

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.SearchActivity.Companion.INTENT_KEY
import com.example.playlistmaker.data.TrackGetterImpl
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.MediaPlayerInteractor
import com.example.playlistmaker.domain.TrackGetter
import java.util.Date

class PlayerActivity : AppCompatActivity() {


    private var handler: Handler? = null//
    private var startTime = 0L//
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var playButton: ImageView
    private lateinit var playTime: TextView//
    private val player: MediaPlayerInteractor = MediaPlayerInteractorImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val trackGetter: TrackGetter = TrackGetterImpl()
        val track = trackGetter.getTrack(INTENT_KEY, intent)
        binding.backButton.setOnClickListener {
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
        binding.lengthD.text = track.trackTime
        binding.yearV.text = track.releaseDate.substring(0, 4)
        binding.genreV.text = track.primaryGenreName
        binding.countryV.text = track.country
        playTime = findViewById(R.id.playTime) //
        handler = Handler(Looper.getMainLooper())//
        playButton = binding.playButton
        playTime.text = "00:00"
        player.url = track.previewUrl


        if (player.url != null) {
            player.prepare (callback = {playButton.setImageResource(R.drawable.play_button)})
            playButton.setOnClickListener {
                player.playbackControl(
                    callback1 = {playButton.setImageResource(R.drawable.play_button)},
                    callback2 = {playButton.setImageResource(R.drawable.pause_button)}
                )
            }
        } else {
            playButton.setOnClickListener {
                Toast.makeText(applicationContext, "Url = null", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        player.pause {playButton.setImageResource(R.drawable.play_button)}
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    private fun startTimer() {
        startTime = System.currentTimeMillis()
        handler?.post(updateTimer(startTime))
    }

    private fun updateTimer(start: Long): Runnable {
        return object : Runnable {
            override fun run() {
                if (player.playerState == MediaPlayerInteractorImpl.STATE_PLAYING) {
                    val current = System.currentTimeMillis()
                    val time = current - start
                    playTime.text = formatMilliseconds(time)
                    handler?.postDelayed(this, 1000L)
                    print(123)
                }
            }
        }
    }
    fun formatMilliseconds(milliseconds: Long): String {
        val format = SimpleDateFormat("mm:ss")
        return format.format(Date(milliseconds))
    }
}