package com.example.playlistmaker.search.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.data.SearchHistory
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.models.TracksState
import com.example.playlistmaker.util.debounce
import kotlinx.coroutines.launch


class SearchViewModel(private val tracksInteractor: TracksInteractor) : ViewModel() {

    private val stateLiveData = MutableLiveData<TracksState>()
    fun observeState(): LiveData<TracksState> = stateLiveData

    private var latestSearchText: String? = null

    private val sDebounce = debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, false) {
        searchRequest(it)
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText || changedText.isEmpty()) {
            return
        }
        this.latestSearchText = changedText

        sDebounce(changedText)
    }

    fun repeatRequest() {
        if (latestSearchText != null) {
            searchRequest(latestSearchText!!)
        }
    }

    fun forcedRequest(queryInput: String) {
        searchRequest(queryInput)
    }

    fun setState(state: TracksState) {
        stateLiveData.postValue(state)
    }

    fun getHistoryList(searchHistory: SearchHistory): List<Track> {
        return searchHistory.getTracks()
    }

    fun clearHistory() {
        SearchHistory.historyList.clear()
    }

    fun putToHistory(searchHistory: SearchHistory) {
        searchHistory.putTracks()
    }

    fun addToHistory(searchHistory: SearchHistory, track: Track) {
        searchHistory.addTrack(track)
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(TracksState.Loading)

            viewModelScope.launch {
                tracksInteractor.searchTracks(newSearchText).collect { pair ->
                    val tracks = mutableListOf<Track>()
                    val foundTracks = pair.first
                    val errorMessage = pair.second
                    if (foundTracks != null) {
                        tracks.addAll(foundTracks)
                    }
                    when {
                        errorMessage != null -> {
                            renderState(TracksState.Error)
                        }

                        tracks.isEmpty() -> {
                            renderState(TracksState.Empty)
                        }

                        else -> {
                            renderState(TracksState.Content(tracks))
                        }
                    }
                }
            }
        }
    }

    private fun renderState(state: TracksState) {
        stateLiveData.postValue(state)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}