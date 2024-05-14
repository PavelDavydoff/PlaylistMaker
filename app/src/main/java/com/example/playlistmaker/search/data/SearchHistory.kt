package com.example.playlistmaker.search.data

import android.content.SharedPreferences
import com.example.playlistmaker.search.data.dto.TrackDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
class SearchHistory(private val sharedPreferences: SharedPreferences){
    companion object{
        private const val HISTORY_KEY = "history"
        private const val MAX_SIZE = 10
        private const val LAST_INDEX = 9
        private const val FIRST_INDEX = 0
    }

    var historyList = mutableListOf<TrackDto>()
    fun getTracks(){
        val s = sharedPreferences.getString(HISTORY_KEY, null)
        historyList = listFromJson(s)
    }

    fun putTracks(){
        val s = jsonFromList(historyList)
        sharedPreferences.edit().putString(HISTORY_KEY, s).apply()
    }

    private fun jsonFromList(list: MutableList<TrackDto>): String{
        val gson = Gson()
        return gson.toJson(list)
    }

    private fun listFromJson(json: String?): MutableList<TrackDto>{
        val gson = Gson()
        val listType = object : TypeToken<MutableList<TrackDto>>() {}.type
        return gson.fromJson(json, listType) ?: mutableListOf()
    }

    fun addTrack(track: TrackDto){
        if (historyList.contains(track)) {
            historyList.remove(track)
        }
        if (historyList.size >= MAX_SIZE){
            historyList.removeAt(LAST_INDEX)
        }
        historyList.add(FIRST_INDEX, track)
    }

}