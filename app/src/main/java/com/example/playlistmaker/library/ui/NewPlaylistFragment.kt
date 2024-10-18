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
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.library.ui.presentation.NewPlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewPlaylistFragment : Fragment() {

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!

    private lateinit var nameTextWatcher: TextWatcher
    private lateinit var descriptionTextWatcher: TextWatcher

    private lateinit var confirmDialog: MaterialAlertDialogBuilder


    private var playlistDescription = ""
    private var hasPicture = false
    private var playlistName = ""
    private var playlistFilePath = ""

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

        val pickPicture = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){uri ->
            if (uri != null){
                binding.placeholder.setImageURI(uri)
                hasPicture = true
                viewModel.saveImage(uri)
                playlistFilePath = uri.toString()
            } else {
                Log.d("PhotoPicker","No media selected")
            }
        }

        binding.placeholder.setOnClickListener {
            pickPicture.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.createButton.setOnClickListener {
            viewModel.addNewPlaylist(Playlist(0, playlistName, playlistDescription, playlistFilePath, "", 0))
            Toast.makeText(requireContext(),
                getString(R.string.playlist_created, playlistName), Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }

        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.ending_create_playlist))
            .setMessage(getString(R.string.all_data_lost))
            .setNegativeButton(getString(R.string.cancel)){ _, _ ->

            }
            .setPositiveButton(getString(R.string.finish)){ _, _ ->
                parentFragmentManager.popBackStack()
            }


        nameTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
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
                playlistDescription = p0.toString()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        }
        descriptionTextWatcher.let { binding.editDescriptionField.addTextChangedListener(it) }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if ( hasPicture|| playlistName != "" || playlistDescription != ""){
                    confirmDialog.show()
                } else {
                    parentFragmentManager.popBackStack()
                }
            }
        })
    }
}