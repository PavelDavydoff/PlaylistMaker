package com.example.playlistmaker.player.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.library.domain.models.Playlist

class PlaylistViewHolderPlayer(view: View): RecyclerView.ViewHolder(view) {
    private val playlistName: TextView = itemView.findViewById(R.id.playlist_name_player)
    private val tracksNumber: TextView = itemView.findViewById(R.id.tracks_number_player)
    private val playlistImage: ImageView = itemView.findViewById(R.id.image_playlist_player)

    fun bind(playlist: Playlist){
        Glide.with(itemView)
            .load(playlist.filePath)
            .fitCenter()
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.corner_2)))
            .into(playlistImage)

        playlistName.text = playlist.name
        tracksNumber.text = "${playlist.tracksCount} треков"
    }
}