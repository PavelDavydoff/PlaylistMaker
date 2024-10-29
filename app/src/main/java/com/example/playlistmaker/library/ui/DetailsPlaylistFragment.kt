package com.example.playlistmaker.library.ui

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.example.playlistmaker.player.data.TrackTime
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.domain.models.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class DetailsPlaylistFragment : Fragment() {
    companion object {
        const val DETAILS_BUNDLE_KEY = "details"
    }

    private var _binding: FragmentDetailsPlaylistBinding? = null

    private lateinit var tracksAdapter: DetailsTrackAdapter
    private lateinit var deleteTrackConfirmDialog: MaterialAlertDialogBuilder
    private lateinit var deletePlaylistConfirmDialog: MaterialAlertDialogBuilder
    private lateinit var message: String
    private var isClickable = true
    private lateinit var playlist: Playlist
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

        isClickable = true

        val playlistId = requireArguments().getString(DETAILS_BUNDLE_KEY)!!.toInt()

        viewModel.getTracks(playlistId)

        viewModel.observeState().observe(viewLifecycleOwner) {
            playlist = it.playlist
            render(it)
            message = getMessage(it)
        }

        tracksAdapter = DetailsTrackAdapter(
            {
                if (isClickable) {
                    val bundle =
                        bundleOf(PlayerFragment.PLAYER_BUNDLE_KEY to viewModel.trackToJson(it))
                    findNavController().navigate(
                        R.id.action_detailsPlaylistFragment_to_playerFragment,
                        bundle
                    )
                }
            },
            {
                if (isClickable) {
                    deleteTrack(it, playlist)
                }
            }
        )

        binding.recyclerViewDetails.adapter = tracksAdapter
        binding.recyclerViewDetails.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.menuBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.shareButton.setOnClickListener {
            if (isClickable) {
                showShareDialog()
            }
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.detailsOverlay.visibility = View.GONE
                        isClickable = true
                    }

                    else -> {
                        binding.detailsOverlay.visibility = View.VISIBLE
                        isClickable = false
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }



        binding.menu.setOnClickListener {
            if (isClickable) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        binding.menuShare.setOnClickListener {
            showShareDialog()
        }

        binding.menuDelete.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            deletePlaylistConfirmDialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.delete_playlist))
                .setMessage(getString(R.string.want_to_delete_playlist, playlist.name))
                .setNegativeButton(getString(R.string.no)) { _, _ ->

                }
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    viewModel.deletePlaylist(playlist, tracksAdapter.tracks)
                    parentFragmentManager.popBackStack()
                }

            deletePlaylistConfirmDialog.show()
        }

        binding.menuEdit.setOnClickListener {
            val bundle = bundleOf(NewPlaylistFragment.BUNDLE_KEY to playlistId)
            findNavController().navigate(R.id.action_detailsPlaylistFragment_to_newPlaylistFragment, bundle)
        }
    }

    private fun showShareDialog() {
        if (tracksAdapter.tracks.isEmpty()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.no_tracks_to_share),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            sharePlaylist()
        }
    }

    private fun sharePlaylist() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = getString(R.string.text_plain)
        intent.putExtra(Intent.EXTRA_TEXT, message)
        startActivity(Intent.createChooser(intent, getString(R.string.share_playlist)))
    }

    private fun getMessage(state: DetailsState): String {
        val playlist = state.playlist
        return if (state is DetailsState.Content) {
            val tracks = state.tracks
            var count = 0
            var result = "${playlist.name}\n${playlist.description}"
            result += "\n${plurals(playlist.tracksCount)}"
            for (track in tracks) {
                count++
                result += "\n$count. ${track.artistName} - ${track.trackName}(${TrackTime.get(track)}) "
            }
            result
        } else {
            ""
        }
    }

    private fun deleteTrack(track: Track, playlist: Playlist) {
        deleteTrackConfirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_track))
            .setMessage(getString(R.string.shure_to_delete))
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->

            }
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                tracksAdapter.tracks.clear()
                viewModel.removeTrack(track, playlist)
            }

        deleteTrackConfirmDialog.show()
    }

    private fun renderTracksDuration(state: DetailsState) {
        val tracksList = mutableListOf<Track>()
        if (state is DetailsState.Content) {
            tracksList.addAll(state.tracks)
        }

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
        if (state is DetailsState.Content) {
            binding.placeholderEmpty.visibility = View.GONE
            binding.textEmpty.visibility = View.GONE
            tracksAdapter.tracks.clear()
            tracksAdapter.tracks.addAll(state.tracks)
            tracksAdapter.notifyDataSetChanged()
        } else {
            binding.placeholderEmpty.visibility = View.VISIBLE
            binding.textEmpty.visibility = View.VISIBLE
        }
    }

    private fun render(state: DetailsState) {
        playlist = state.playlist
        Glide.with(this)
            .load(state.playlist.filePath)
            .centerCrop()
            .placeholder(R.drawable.placeholder312)
            .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.corner_2)))
            .into(binding.playlistImage)

        binding.playlistName.text = state.playlist.name
        binding.playlistDescription.text = state.playlist.description
        binding.tracksAmount.text = plurals(state.playlist.tracksCount)

        Glide.with(this)
            .load(state.playlist.filePath)
            .centerCrop()
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.corner_2)))
            .into(binding.playlistInMenuImage)

        binding.playlistInMenuName.text = state.playlist.name
        binding.playlistInMenuCount.text = plurals(state.playlist.tracksCount)

        renderTracks(state)
        renderTracksDuration(state)
    }

    private fun plurals(value: Int): String {
        return requireContext().resources.getQuantityString(
            R.plurals.track_postfix,
            value,
            value
        )
    }
}