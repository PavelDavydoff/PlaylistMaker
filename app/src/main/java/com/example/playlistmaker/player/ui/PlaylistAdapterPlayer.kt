package com.example.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.library.domain.models.Playlist

class PlaylistAdapterPlayer(private val addTrack: (Playlist) -> Unit): RecyclerView.Adapter<PlaylistViewHolderPlayer>() {
    var playlists = ArrayList<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolderPlayer {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_playlist_linear, parent, false)
        return PlaylistViewHolderPlayer(view)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistViewHolderPlayer, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            addTrack(playlists[position])
        }
    }

}