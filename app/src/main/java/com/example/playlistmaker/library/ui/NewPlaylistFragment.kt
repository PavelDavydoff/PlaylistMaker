package com.example.playlistmaker.library.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.library.ui.presentation.NewPlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class NewPlaylistFragment : Fragment() {

    companion object {
        fun newInstance() = NewPlaylistFragment()
    }

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!

    private lateinit var nameTextWatcher: TextWatcher
    private lateinit var descriptionTextWatcher: TextWatcher

    private lateinit var confirmDialog: MaterialAlertDialogBuilder

    private var hasName = false
    private var hasDescription = false
    private var hasPicture = false

    private val viewModel: NewPlaylistViewModel by viewModel()

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
                saveImageToPrivateStorage(uri)
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

        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setNegativeButton("Отмена"){dialog, which ->

            }
            .setPositiveButton("Завершить"){dialog, which ->
                parentFragmentManager.popBackStack()

            }


        nameTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString() == "") {
                    binding.createButton.isEnabled = false
                    hasName = false
                } else {
                    binding.createButton.isEnabled = true
                    hasName = true
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        }
        nameTextWatcher.let { binding.editNameField.addTextChangedListener(it) }

        descriptionTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString() !=""){
                    hasDescription = true
                } else {
                    hasDescription = false
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        }
        descriptionTextWatcher.let { binding.editDescriptionField.addTextChangedListener(it) }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (hasPicture || hasName || hasDescription){
                    confirmDialog.show()
                } else {
                    parentFragmentManager.popBackStack()
                }
            }
        })
    }

    private fun saveImageToPrivateStorage(uri: Uri){
        val filePath = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myAlbum")

        if (!filePath.exists()){
            filePath.mkdirs()
        }

        val file = File(filePath, "PlaylistPicture.jpg")

        val inputStream = requireActivity().contentResolver.openInputStream(uri)

        val outputStream = FileOutputStream(file)

        BitmapFactory.decodeStream(inputStream).compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }
}