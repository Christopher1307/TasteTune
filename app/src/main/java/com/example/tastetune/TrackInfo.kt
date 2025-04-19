package com.example.tastetune

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Track(val name: String, val artist: String)

class TrackAdapter(private val tracks: List<Track>) :
    RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    inner class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val trackName: TextView = view.findViewById(R.id.trackName)
        val artistName: TextView = view.findViewById(R.id.artistName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.trackName.text = track.name
        holder.artistName.text = track.artist
    }

    override fun getItemCount() = tracks.size
}
