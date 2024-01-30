package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
private const val HISTORY_KEY = "history"
private const val MAX_SIZE = 10
private const val LAST_INDEX = 9
private const val FIRST_INDEX = 0
class SearchHistory(private val sharedPreferences: SharedPreferences){

    var historyList = mutableListOf<Track>()
    fun getTracks(){
        val s = sharedPreferences.getString(HISTORY_KEY, null)
        historyList = listFromJson(s)
    }

    fun putTracks(){
        val s = jsonFromList(historyList)
        sharedPreferences.edit().putString(HISTORY_KEY, s).apply()
    }

    private fun jsonFromList(list: MutableList<Track>): String{
        val gson = Gson()
        return gson.toJson(list)
    }

    private fun listFromJson(json: String?): MutableList<Track>{
        val gson = Gson()
        val listType = object : TypeToken<MutableList<Track>>() {}.type
        return gson.fromJson(json, listType) ?: mutableListOf()
    }

    fun addTrack(track: Track){
        if (historyList.contains(track)) {
            historyList.remove(track)
        }
        if (historyList.size >= MAX_SIZE){
            historyList.removeAt(LAST_INDEX)
        }
        historyList.add(FIRST_INDEX, track)
    }

}