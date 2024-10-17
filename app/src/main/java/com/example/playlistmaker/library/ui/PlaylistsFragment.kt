package com.example.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.library.ui.models.PlaylistsState
import com.example.playlistmaker.library.ui.presentation.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    companion object {
        fun newInstance() = PlaylistsFragment()
    }

    private lateinit var adapter: PlaylistAdapter

    private lateinit var binding: FragmentPlaylistsBinding

    private val viewModel: PlaylistsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getPlaylists()

        adapter = PlaylistAdapter(requireContext())

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = GridLayoutManager(context, 2)

        viewModel.observeState().observe(viewLifecycleOwner){
            render(it)
        }

        binding.createPlaylist.setOnClickListener {
            findNavController().navigate(R.id.newPlaylistFragment)
        }
    }

    private fun render(state: PlaylistsState){
        if (state is PlaylistsState.Content){
            adapter.playlists.clear()
            adapter.playlists.addAll(state.playlists)
            adapter.notifyDataSetChanged()
            binding.recyclerView.visibility = View.VISIBLE
            binding.noPlaylistsImage.visibility = View.GONE
            binding.noPlaylistsText.visibility = View.GONE
        } else {
            binding.recyclerView.visibility = View.GONE
            binding.noPlaylistsImage.visibility = View.VISIBLE
            binding.noPlaylistsText.visibility = View.VISIBLE
        }
    }
}