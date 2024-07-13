package com.example.playlistmaker.search.ui

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.data.SearchHistory
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.presentation.SearchViewModel
import com.example.playlistmaker.search.ui.models.TracksState
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
    private var handler = Handler(Looper.getMainLooper())

    private lateinit var tracksAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var searchHistory: SearchHistory
    private lateinit var queryInput: String
    private lateinit var textWatcher: TextWatcher

    private lateinit var binding: FragmentSearchBinding

    private val viewModel by viewModel<SearchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        queryInput = ""

        val historyPrefs = requireActivity().getSharedPreferences(HISTORY_KEY, MODE_PRIVATE)
        searchHistory = SearchHistory(historyPrefs)
        searchHistory.getTracks()

        val playerIntent = Intent(activity, PlayerActivity::class.java)

        tracksAdapter = TrackAdapter { track ->
            searchHistory.addTrack(track)
            if (clickDebounce()) {
                startActivity(playerIntent.putExtra(INTENT_KEY, track))
            }
        }

        historyAdapter = TrackAdapter { track ->
            searchHistory.addTrack(track)
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
            searchHistory.historyList.clear()
            binding.historyLayout.visibility = View.GONE
            historyAdapter.notifyDataSetChanged()
        }

        binding.clearImageView.setOnClickListener {
            binding.editText.setText(EMPTY)
            viewModel.setState(TracksState.History(searchHistory.historyList))
            updateAdapter(historyAdapter, searchHistory.historyList)

            val inputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.editText.windowToken, 0)
        }

        binding.editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                viewModel.setState(TracksState.History(searchHistory.historyList))
                updateAdapter(historyAdapter, searchHistory.historyList)
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

        updateAdapter(historyAdapter, searchHistory.historyList)
    }

    override fun onStop() {
        super.onStop()
        searchHistory.putTracks()
    }

    override fun onDestroy() {
        super.onDestroy()
        textWatcher.let { binding.editText.removeTextChangedListener(it) }
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
            is TracksState.History -> showHistory(searchHistory.historyList)
        }
    }

    private fun showHistory(tracks: List<Track>) {
        Log.d("SearchFragment", "showHistory")
        updateAdapter(historyAdapter, tracks)

        binding.historyLayout.visibility =
            if (searchHistory.historyList.isEmpty()) View.GONE else View.VISIBLE

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
        Log.d("SearchFragment", "showContent")
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
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
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