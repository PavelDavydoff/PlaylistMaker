package com.example.playlistmaker.main.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.playlistmaker.R
import com.example.playlistmaker.search.ui.SearchFragment
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.library.ui.LibraryFragment
import com.example.playlistmaker.settings.ui.SettingsFragment

class MainFragment : Fragment() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.search.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                replace(R.id.container_view, SearchFragment.newInstance())
                addToBackStack("stack")
            }
        }

        binding.library.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                replace(R.id.container_view, LibraryFragment.newInstance())
                addToBackStack("stack")
            }
        }

        binding.settings.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                replace(R.id.container_view, SettingsFragment.newInstance())
                addToBackStack("stack")
            }
        }
    }
}