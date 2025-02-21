package com.example.playlistmaker.library.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.library.ui.models.NewPlaylistState
import com.example.playlistmaker.library.ui.presentation.NewPlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.random.Random

class NewPlaylistFragment : Fragment() {
    companion object {
        const val BUNDLE_KEY = "new playlist"
        private const val NO_VALUE = ""
    }

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!

    private lateinit var nameTextWatcher: TextWatcher
    private lateinit var descriptionTextWatcher: TextWatcher

    private lateinit var confirmDialog: MaterialAlertDialogBuilder


    private var playlistDescription = NO_VALUE
    private var hasPicture = false
    private var playlistName = NO_VALUE
    private var playlistFilePath = NO_VALUE
    private var imageFilePath = NO_VALUE
    private var editablePlaylist = Playlist(-1, NO_VALUE, NO_VALUE, NO_VALUE, NO_VALUE, 0)

    private val viewModel by viewModel<NewPlaylistViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlistId = requireArguments().getInt(BUNDLE_KEY)

        viewModel.setState(playlistId)

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        val pickPicture =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    binding.placeholder.setImageURI(uri)
                    binding.placeholder.setBackgroundResource(0)
                    hasPicture = true
                    imageFilePath = uri.toString()
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        binding.placeholder.setOnClickListener {
            pickPicture.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }



        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.ending_create_playlist))
            .setMessage(getString(R.string.all_data_lost))
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->

            }
            .setPositiveButton(getString(R.string.finish)) { _, _ ->
                parentFragmentManager.popBackStack()
            }


    }

    private fun createNewPlaylist() {
        if (imageFilePath != "") {
            playlistFilePath = viewModel.saveImage(imageFilePath.toUri(), playlistName)
        }
        viewModel.addNewPlaylist(
            Playlist(
                0,
                playlistName,
                playlistDescription,
                playlistFilePath,
                NO_VALUE,
                0
            )
        )
        Toast.makeText(
            requireContext(),
            getString(R.string.playlist_created, playlistName), Toast.LENGTH_SHORT
        ).show()
    }

    private fun editPlaylist(playlist: Playlist) {
        if (imageFilePath != "") {
            playlistFilePath = viewModel.saveImage(imageFilePath.toUri(), playlistName + Random(1000).toString())
        }
        playlist.filePath = playlistFilePath
        viewModel.addNewPlaylist(playlist)
    }

    private fun render(state: NewPlaylistState) {

        if (state is NewPlaylistState.Edit) {

            editablePlaylist = state.playlist

            Glide.with(this)
                .load(state.playlist.filePath)
                .centerCrop()
                .placeholder(R.drawable.placeholder312)
                .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.corner_8)))
                .into(binding.placeholder)

            binding.title.text = getString(R.string.to_edit_playlist)
            binding.createButton.text = getString(R.string.save)
            playlistName = state.playlist.name
            binding.editNameField.setText(state.playlist.name)
            binding.placeholder.setBackgroundResource(0)

            binding.editDescriptionField.setText(state.playlist.description)


            if (binding.editNameField.text!!.isNotEmpty()) {
                binding.createButton.isEnabled = true
            }
        }

        binding.createButton.setOnClickListener {

            if (state is NewPlaylistState.New) {
                createNewPlaylist()
            } else {
                editPlaylist(editablePlaylist)
            }
            parentFragmentManager.popBackStack()

        }

        nameTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                editablePlaylist.name = p0.toString()
                playlistName = p0.toString()
                binding.createButton.isEnabled = p0.toString() != ""
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        }
        nameTextWatcher.let { binding.editNameField.addTextChangedListener(it) }

        descriptionTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                editablePlaylist.description = p0.toString()
                playlistDescription = p0.toString()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        }
        descriptionTextWatcher.let { binding.editDescriptionField.addTextChangedListener(it) }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (state is NewPlaylistState.Edit) {
                        parentFragmentManager.popBackStack()
                    } else {
                        if (hasPicture || playlistName != "" || playlistDescription != "") {
                            confirmDialog.show()
                        } else {
                            parentFragmentManager.popBackStack()
                        }
                    }
                }
            })

    }
}