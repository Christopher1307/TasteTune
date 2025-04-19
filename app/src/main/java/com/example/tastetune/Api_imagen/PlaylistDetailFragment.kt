package com.example.tastetune.Api_imagen

import SharedViewModel
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tastetune.*
import com.example.tastetune.Api_Music.SpotifyAuth
import com.example.tastetune.R
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class PlaylistDetailFragment : Fragment() {

    private lateinit var playlistTitleTextView: TextView
    private lateinit var playlistDescTextView: TextView
    private lateinit var playlistImageView: ImageView
    private lateinit var tracksRecyclerView: RecyclerView

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val trackList = mutableListOf<Track>()
    private lateinit var trackAdapter: TrackAdapter
    private val client = OkHttpClient()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_playlist_detail, container, false)

        playlistTitleTextView = view.findViewById(R.id.playlistTextView)
        playlistDescTextView = view.findViewById(R.id.playlistDescription)
        playlistImageView = view.findViewById(R.id.playlistImage)
        tracksRecyclerView = view.findViewById(R.id.tracksRecyclerView)

        trackAdapter = TrackAdapter(trackList)
        tracksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        tracksRecyclerView.adapter = trackAdapter

        sharedViewModel.playlistId.observe(viewLifecycleOwner) { playlistId ->
            if (!playlistId.isNullOrEmpty()) {
                loadPlaylistDetails(playlistId)
                loadTracks(playlistId)
            } else {
                playlistTitleTextView.text = "No hay playlist generada"
            }
        }

        return view
    }

    private fun loadPlaylistDetails(playlistId: String) {
        val token = SpotifyAuth.accessToken ?: return
        val request = Request.Builder()
            .url("https://api.spotify.com/v1/playlists/$playlistId")
            .addHeader("Authorization", "Bearer $token")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    playlistTitleTextView.text = "Error al cargar: ${e.message}"
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val json = response.body?.string() ?: return
                if (response.isSuccessful) {
                    val obj = JSONObject(json)
                    val name = obj.getString("name")
                    val description = obj.optString("description", "Sin descripción")
                    val imageUrl = obj.getJSONArray("images").optJSONObject(0)?.getString("url")

                    requireActivity().runOnUiThread {
                        playlistTitleTextView.text = name
                        playlistDescTextView.text = description
                        if (!imageUrl.isNullOrEmpty()) {
                            Glide.with(requireContext()).load(imageUrl).into(playlistImageView)
                        }
                    }
                }
            }
        })
    }

    private fun loadTracks(playlistId: String) {
        val token = SpotifyAuth.accessToken ?: return
        val request = Request.Builder()
            .url("https://api.spotify.com/v1/playlists/$playlistId/tracks")
            .addHeader("Authorization", "Bearer $token")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                val json = response.body?.string() ?: return
                if (response.isSuccessful) {
                    val items = JSONObject(json).getJSONArray("items")
                    trackList.clear()
                    for (i in 0 until items.length()) {
                        val track = items.getJSONObject(i).getJSONObject("track")
                        val name = track.getString("name")
                        val artist = track.getJSONArray("artists").getJSONObject(0).getString("name")
                        trackList.add(Track(name, artist))
                    }
                    requireActivity().runOnUiThread {
                        trackAdapter.notifyDataSetChanged()
                    }
                }
            }
        })
    }
}
