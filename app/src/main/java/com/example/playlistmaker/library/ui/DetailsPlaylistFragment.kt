package com.example.playlistmaker.library.ui

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentDetailsPlaylistBinding
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.library.ui.models.DetailsState
import com.example.playlistmaker.library.ui.presentation.DetailsPlaylistViewModel
import com.example.playlistmaker.library.ui.presentation.DetailsTrackAdapter
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.domain.models.Track
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class DetailsPlaylistFragment : Fragment() {
    companion object {
        const val DETAILS_BUNDLE_KEY = "details"
    }

    private var _binding: FragmentDetailsPlaylistBinding? = null

    private lateinit var tracksAdapter: DetailsTrackAdapter
    private lateinit var confirmDialog: MaterialAlertDialogBuilder
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

        val playlistId = requireArguments().getString(DETAILS_BUNDLE_KEY)!!.toInt()

        viewModel.getTracks(playlistId)

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun deleteTrack(track: Track, playlist: Playlist) {
        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_track))
            .setMessage(getString(R.string.shure_to_delete))
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->

            }
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                Log.d("DetailsFragment", "launch")
                viewModel.removeTrack(track, playlist)
            }

        confirmDialog.show()
    }

    private fun renderTracksDuration(state: DetailsState) {
        val tracksList = mutableListOf<Track>()
        tracksList.addAll(state.tracks)

        var duration1 = 0L
        for (track in tracksList) {
            duration1 += track.trackTimeMillis
        }
        val resultString = SimpleDateFormat("mm", Locale.getDefault()).format(duration1)
        val result = resultString.toInt()
        binding.duration.text =
            requireContext().resources.getQuantityString(R.plurals.minutes_postfix, result, result)
    }

    private fun renderTracks(state: DetailsState) {
        Log.d("DetailsFragment", "RenderTracks")
        tracksAdapter.tracks.clear()
        Log.d("State", state.tracks.map { track -> track.trackName }.toString())
        tracksAdapter.tracks.addAll(state.tracks)
        tracksAdapter.notifyDataSetChanged()
        Log.d("Adapter", tracksAdapter.tracks.map { track -> track.trackName }.toString())
    }

    private fun render(state: DetailsState){
        Glide.with(this)
            .load(state.playlist.filePath)
            .centerCrop()
            .placeholder(R.drawable.placeholder312)
            .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.corner_2)))
            .into(binding.playlistImage)

        binding.playlistName.text = state.playlist.name
        binding.playlistDescription.text = state.playlist.description
        binding.tracksAmount.text = requireContext().resources.getQuantityString(
            R.plurals.track_postfix,
            state.playlist.tracksCount,
            state.playlist.tracksCount
        )

        tracksAdapter = DetailsTrackAdapter(
            {
                val bundle = bundleOf(PlayerFragment.PLAYER_BUNDLE_KEY to viewModel.trackToJson(it))
                findNavController().navigate(
                    R.id.action_detailsPlaylistFragment_to_playerFragment,
                    bundle
                )
            },
            {
                deleteTrack(it, state.playlist)
            }
        )

        binding.recyclerViewDetails.adapter = tracksAdapter
        binding.recyclerViewDetails.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        renderTracks(state)
        renderTracksDuration(state)
    }
}