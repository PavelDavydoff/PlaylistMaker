package com.example.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FavoriteFragmentBinding
import com.example.playlistmaker.library.ui.models.FavoriteState
import com.example.playlistmaker.library.ui.presentation.FavoriteViewModel
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.util.TrackStorage
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteFragment : Fragment() {

    companion object {
        fun newInstance() = FavoriteFragment()
    }

    private lateinit var binding: FavoriteFragmentBinding

    private lateinit var favoriteAdapter: TrackAdapter

    private val viewModel: FavoriteViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FavoriteFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getFavorites()

        favoriteAdapter = TrackAdapter { track ->
            (requireActivity() as TrackStorage).setTrack(viewModel.trackToJson(track))
            findNavController().navigate(R.id.action_libraryFragment_to_playerFragment)
        }

        binding.favoriteRecycler.adapter = favoriteAdapter
        binding.favoriteRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        viewModel.observeState().observe(viewLifecycleOwner){
            render(it)
        }

    }

    private fun render(state: FavoriteState){
        if (state is FavoriteState.Content){
            favoriteAdapter.tracks.clear()
            favoriteAdapter.tracks.addAll(state.tracks)
            favoriteAdapter.notifyDataSetChanged()
            binding.favoriteRecycler.visibility = View.VISIBLE
            binding.placeholder.visibility = View.GONE
        } else {
            binding.favoriteRecycler.visibility = View.GONE
            binding.placeholder.visibility = View.VISIBLE
        }
    }
}