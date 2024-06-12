package com.example.playlistmaker.search.presentation

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.models.TracksState


class SearchViewModel(private val tracksInteractor: TracksInteractor) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())

    private val stateLiveData = MutableLiveData<TracksState>()
    fun observeState(): LiveData<TracksState> = stateLiveData

    private var latestSearchText: String? = null

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText || changedText.isEmpty()) {
            return
        }
        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchRequest(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(searchRunnable, SEARCH_REQUEST_TOKEN, postTime)
    }

    fun repeatRequest(){
        if (latestSearchText != null) {
            searchRequest(latestSearchText!!)
        }
    }

    fun forcedRequest(queryInput: String){
        searchRequest(queryInput)
    }

    fun setState(state: TracksState){
        stateLiveData.postValue(state)
    }

    private fun searchRequest(newSearchText: String) {
        Log.d("SearchViewModel", "searchRequest $newSearchText")
        if (newSearchText.isNotEmpty()) {
            renderState(TracksState.Loading)
        }

        tracksInteractor.searchTracks(newSearchText, object : TracksInteractor.TrackConsumer {
            override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                val tracks = mutableListOf<Track>()
                if (foundTracks != null) {
                    tracks.addAll(foundTracks)
                }
                when {
                    errorMessage != null -> {
                        renderState(TracksState.Error)
                    }

                    tracks.isEmpty() -> {
                        Log.d("SearchViewModel", "trackIsEmpty")
                        renderState(TracksState.Empty)
                    }

                    else -> {
                        renderState(TracksState.Content(tracks))
                    }
                }
            }
        })
    }

    private fun renderState(state: TracksState) {
        Log.d("SearchViewModel", "renderState: $state")
        stateLiveData.postValue(state)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }
}