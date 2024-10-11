package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.library.ui.NewPlaylistFragment
import com.example.playlistmaker.player.data.TrackTime
import com.example.playlistmaker.player.presentation.PlayerViewModel
import com.example.playlistmaker.player.ui.models.PlayerState
import com.example.playlistmaker.player.ui.models.ToastState
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.SearchFragment.Companion.INTENT_KEY
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var playButton: ImageView
    private lateinit var playTime: TextView

    private var isPlaying: Boolean = false

    private val viewModel: PlayerViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val track = intent.getSerializableExtra(INTENT_KEY) as Track

        viewModel.checkFavorite(track)

        val previewUrl = track.previewUrl

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }
                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        binding.addToCollectionButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.createPlaylist.setOnClickListener {

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            binding.overlay.visibility = View.GONE

            supportFragmentManager.beginTransaction()
                .replace(R.id.container_view, NewPlaylistFragment.newInstance())
                .addToBackStack(packageName)
                .commit()

            binding.playerActivity.visibility = View.GONE
        }

        binding.backButton.setOnClickListener {
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
        playButton = binding.playButton

        viewModel.observeState().observe(this) {
            render(it)
            isPlaying = it.isPlaying
        }

        viewModel.observeShowToast().observe(this) {
            showToast(it)
        }

        viewModel.observeToastState().observe(this) { toast ->
            if (toast is ToastState.Show) {
                showToast(toast.additionalMessage)
            }
        }

        viewModel.observeFavorite().observe(this){
            renderFavorite(it)
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

        binding.addToFavoritesButton.setOnClickListener {
            viewModel.onFavoriteClicked(track)
        }
    }

    private fun renderFavorite(isFavorite: Boolean){
        if (isFavorite){
            binding.addToFavoritesButton.setImageResource(R.drawable.is_favorite)
        } else {
            binding.addToFavoritesButton.setImageResource(R.drawable.is_not_favorite)
        }
    }

    private fun render(state: PlayerState) {
        playButton.setImageResource(
            if (state.isPlaying) {
                R.drawable.pause_button
            } else {
                R.drawable.play_button
            }
        )
        playTime.text = state.progress
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }
}