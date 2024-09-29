package com.example.playlistmaker.library.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.library.ui.presentation.NewPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewPlaylistFragment : Fragment() {

    companion object {
        fun newInstance() = NewPlaylistFragment()
    }

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!

    private lateinit var nameTextWatcher: TextWatcher
    private lateinit var descriptionTextWatcher: TextWatcher

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

        binding.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        nameTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString() == "") {
                    binding.editName.setBackgroundResource(R.drawable.rounded_rectangle_grey)
                    binding.createButton.isEnabled = false

                } else {
                    binding.editName.setBackgroundResource(R.drawable.rounded_rectangle_blue)
                    binding.createButton.isEnabled = true
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        }
        nameTextWatcher.let { binding.editName.addTextChangedListener(it) }

        descriptionTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString() == ""){
                    binding.editDescription.setBackgroundResource(R.drawable.rounded_rectangle_grey)
                } else {
                    binding.editDescription.setBackgroundResource(R.drawable.rounded_rectangle_blue)
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        }
        descriptionTextWatcher.let { binding.editDescription.addTextChangedListener(it) }
    }
}