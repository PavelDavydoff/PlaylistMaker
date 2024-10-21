package com.example.playlistmaker.library.ui

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentDetailsPlaylistBinding
import com.example.playlistmaker.library.ui.models.DetailsState
import com.example.playlistmaker.library.ui.presentation.DetailsPlaylistViewModel
import com.example.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class DetailsPlaylistFragment: Fragment() {
    companion object{
        const val DETAILS_BUNDLE_KEY = "details"
    }
    private var _binding: FragmentDetailsPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailsPlaylistViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlistJson = requireArguments().getString(DETAILS_BUNDLE_KEY)!!
        val playlist = viewModel.jsonToPlaylist(playlistJson)

        viewModel.getTracks(playlist)

        viewModel.observeState().observe(viewLifecycleOwner){
            renderTracksDuration(it)
        }

        Glide.with(this)
            .load(playlist.filePath)
            .centerCrop()
            .placeholder(R.drawable.placeholder312)
            .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.corner_2)))
            .into(binding.playlistImage)

        binding.playlistName.text = playlist.name
        binding.playlistDescription.text = playlist.description
        binding.tracksAmount.text = requireContext().resources.getQuantityString(R.plurals.track_postfix, playlist.tracksCount, playlist.tracksCount)
    }

    private fun renderTracksDuration(state: DetailsState){
        val tracksList = mutableListOf<Track>()
        if (state is DetailsState.Content){
            tracksList.addAll(state.tracks)
        }
        var duration1 = 0L
        for (track in tracksList){
            duration1 += track.trackTimeMillis
        }
        val resultString = SimpleDateFormat("mm", Locale.getDefault()).format(duration1)
        val result = resultString.toInt()
        binding.duration.text = requireContext().resources.getQuantityString(R.plurals.minutes_postfix, result, result)
    }
}