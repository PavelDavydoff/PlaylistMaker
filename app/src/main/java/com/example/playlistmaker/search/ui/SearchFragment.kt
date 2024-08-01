package com.example.playlistmaker.search.ui

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.presentation.SearchViewModel
import com.example.playlistmaker.search.ui.models.TracksState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    companion object {
        private const val KEY = "text"
        private const val EMPTY = ""
        const val INTENT_KEY = "key"
        private const val HISTORY_KEY = "history"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private var isClickAllowed = true

    private lateinit var tracksAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var queryInput: String
    private lateinit var textWatcher: TextWatcher
    private lateinit var historyPrefs: SharedPreferences

    private var job: Job? = null

    private var _binding: FragmentSearchBinding? = null

    private val binding get() = _binding!!

    private val viewModel by viewModel<SearchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playerIntent = Intent(activity, PlayerActivity::class.java)

        queryInput = ""

        historyPrefs = requireActivity().getSharedPreferences(HISTORY_KEY, MODE_PRIVATE)

        tracksAdapter = TrackAdapter { track ->
            viewModel.addToHistory(historyPrefs, track)
            if (clickDebounce()) {
                startActivity(playerIntent.putExtra(INTENT_KEY, track))
            }
        }

        historyAdapter = TrackAdapter { track ->
            viewModel.addToHistory(historyPrefs, track)
            if (clickDebounce()) {
                startActivity(playerIntent.putExtra(INTENT_KEY, track))
            }
        }

        binding.trackRecycler.adapter = tracksAdapter
        binding.trackRecycler.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.historyRecycler.adapter = historyAdapter
        binding.historyRecycler.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.clearHistory.setOnClickListener {
            historyPrefs.edit().clear().apply()
            viewModel.clearHistory()
            binding.historyLayout.visibility = View.GONE
            historyAdapter.notifyDataSetChanged()
        }

        binding.clearImageView.setOnClickListener {
            binding.editText.setText(EMPTY)
            viewModel.setState(TracksState.History(viewModel.getHistoryList(historyPrefs)))
            updateAdapter(historyAdapter, viewModel.getHistoryList(historyPrefs))

            val inputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.editText.windowToken, 0)
        }

        binding.editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                viewModel.setState(TracksState.History(viewModel.getHistoryList(historyPrefs)))
                updateAdapter(historyAdapter, viewModel.getHistoryList(historyPrefs))
            }
        }

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.clearImageView.visibility = clearButtonVisibility(p0)
                viewModel.searchDebounce(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
                queryInput = p0.toString()
            }
        }
        textWatcher.let { binding.editText.addTextChangedListener(it) }

        binding.editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.forcedRequest(queryInput)
            }
            false
        }

        binding.refreshButton.setOnClickListener {
            viewModel.repeatRequest()
            binding.noInternet.visibility = View.GONE
        }

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    override fun onResume() {
        super.onResume()

        updateAdapter(historyAdapter, viewModel.getHistoryList(historyPrefs))
    }

    override fun onStop() {
        super.onStop()
        viewModel.putToHistory(historyPrefs)
    }

    override fun onDestroy() {
        super.onDestroy()
        textWatcher.let { binding.editText.removeTextChangedListener(it) }
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY, queryInput)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        val restoredText = savedInstanceState?.getString(KEY)
        binding.editText.setText(restoredText)
    }

    private fun render(state: TracksState) {
        when (state) {
            is TracksState.Loading -> showLoading()
            is TracksState.Content -> showContent(state.tracks)
            is TracksState.Error -> showError()
            is TracksState.Empty -> showEmpty()
            is TracksState.History -> showHistory(viewModel.getHistoryList(historyPrefs))
        }
    }

    private fun showHistory(tracks: List<Track>) {
        updateAdapter(historyAdapter, tracks)

        binding.historyLayout.visibility =
            if (viewModel.getHistoryList(historyPrefs).isEmpty()) View.GONE else View.VISIBLE

        binding.trackRecycler.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.noInternet.visibility = View.GONE
        binding.notFound.visibility = View.GONE
    }

    private fun showLoading() {
        binding.trackRecycler.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        binding.noInternet.visibility = View.GONE
        binding.notFound.visibility = View.GONE
        binding.historyLayout.visibility = View.GONE
    }

    private fun showError() {
        binding.trackRecycler.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.noInternet.visibility = View.VISIBLE
        binding.notFound.visibility = View.GONE
        binding.historyLayout.visibility = View.GONE
    }

    private fun showEmpty() {
        binding.trackRecycler.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.noInternet.visibility = View.GONE
        binding.notFound.visibility = View.VISIBLE
        binding.historyLayout.visibility = View.GONE
    }

    private fun showContent(tracks: List<Track>) {
        binding.trackRecycler.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.noInternet.visibility = View.GONE
        binding.notFound.visibility = View.GONE
        binding.historyLayout.visibility = View.GONE

        updateAdapter(tracksAdapter, tracks)
    }

    private fun updateAdapter(adapter: TrackAdapter, tracks: List<Track>) {
        adapter.tracks.clear()
        adapter.tracks.addAll(tracks)
        adapter.notifyDataSetChanged()
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            job = viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }
}