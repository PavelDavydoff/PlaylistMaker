package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.player.data.TrackTime
import com.example.playlistmaker.player.presentation.PlayerViewModel
import com.example.playlistmaker.player.ui.models.AddTrackToastState
import com.example.playlistmaker.player.ui.models.PlayerState
import com.example.playlistmaker.player.ui.models.ToastState
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null

    private val binding get() = _binding!!

    private lateinit var playButton: ImageView
    private lateinit var playTime: TextView

    private lateinit var playlistAdapter: PlaylistAdapterPlayer

    private var isPlaying: Boolean = false

    private val viewModel: PlayerViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val trackJson = requireArguments().getString("track")!!
        val track = viewModel.jsonToTrack(trackJson)

        viewModel.checkFavorite(track)

        val previewUrl = track.previewUrl

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

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

        viewModel.getPlaylists()

        playlistAdapter = PlaylistAdapterPlayer(requireContext()) {
            viewModel.addTrackToPlaylist(it, track)
        }
        binding.recyclerViewPlayer.adapter = playlistAdapter
        binding.recyclerViewPlayer.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        viewModel.observePlaylist().observe(viewLifecycleOwner) {
            renderPlaylists(it)
        }

        binding.addToCollectionButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.createPlaylist.setOnClickListener {

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            binding.overlay.visibility = View.GONE

            findNavController().navigate(R.id.action_playerFragment_to_newPlaylistFragment)

            binding.playerActivity.visibility = View.GONE
        }

        binding.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(enabled = true) {
            override fun handleOnBackPressed() {
                viewModel.stop()
                parentFragmentManager.popBackStack()
            }
        })

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
        playTime = binding.playTime
        playButton = binding.playButton

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
            isPlaying = it.isPlaying
        }

        viewModel.observeShowToast().observe(viewLifecycleOwner) {
            showToast(it)
        }

        viewModel.observeTrackToast().observe(viewLifecycleOwner){
            showAddTrackToast(it)
        }

        viewModel.observeToastState().observe(viewLifecycleOwner) { toast ->
            if (toast is ToastState.Show) {
                showToast(toast.additionalMessage)
            }
        }

        viewModel.observeFavorite().observe(viewLifecycleOwner) {
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

    private fun renderFavorite(isFavorite: Boolean) {
        if (isFavorite) {
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

    private fun renderPlaylists(playlists: MutableList<Playlist>) {
        playlistAdapter.playlists.clear()
        playlistAdapter.playlists.addAll(playlists)
        playlistAdapter.notifyDataSetChanged()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showAddTrackToast(state: AddTrackToastState) {
        when (state) {
            is AddTrackToastState.IsAdded -> {
                Toast.makeText(requireContext(), getString(R.string.track_added) + " ${state.playlistName}", Toast.LENGTH_SHORT).show()
            }

            is AddTrackToastState.IsNotAdded -> {
                Toast.makeText(requireContext(),
                    getString(R.string.track_already_added) + " ${state.playlistName}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }
}