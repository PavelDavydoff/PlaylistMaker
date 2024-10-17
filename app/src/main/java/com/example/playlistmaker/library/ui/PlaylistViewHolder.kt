package com.example.playlistmaker.library.ui

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.library.domain.models.Playlist

class PlaylistViewHolder(view: View, val context: Context): RecyclerView.ViewHolder(view) {
    private val playlistName: TextView = itemView.findViewById(R.id.playlist_name)
    private val tracksNumber: TextView = itemView.findViewById(R.id.tracks_number)
    private val playlistImage: ImageView = itemView.findViewById(R.id.playlist_image)

    fun bind(playlist: Playlist){
            Glide.with(itemView)
                .load(playlist.filePath)
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.corner_8)))
                .into(playlistImage)

        playlistName.text = playlist.name
        tracksNumber.text = context.resources.getQuantityString(R.plurals.track_postfix, playlist.tracksCount, playlist.tracksCount)
    }
}