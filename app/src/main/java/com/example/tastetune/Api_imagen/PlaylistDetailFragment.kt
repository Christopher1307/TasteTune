package com.example.tastetune.Api_imagen

import SharedViewModel
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.tastetune.Api_Music.SpotifyAuth
import com.example.tastetune.R
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class PlaylistDetailFragment : Fragment() {

    private lateinit var playlistTitleTextView: TextView
    private lateinit var playlistDescTextView: TextView
    private lateinit var playlistImageView: ImageView
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val client = OkHttpClient()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_playlist_detail, container, false)

        playlistTitleTextView = view.findViewById(R.id.playlistTextView)
        playlistDescTextView = view.findViewById(R.id.playlistDescription)
        playlistImageView = view.findViewById(R.id.playlistImage)

        sharedViewModel.playlistId.observe(viewLifecycleOwner) { playlistId ->
            playlistId?.let {
                loadPlaylistDetails(it)
            } ?: run {
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
                val json = response.body?.string()
                if (response.isSuccessful && json != null) {
                    val jsonObj = JSONObject(json)
                    val name = jsonObj.getString("name")
                    val description = jsonObj.optString("description", "Sin descripción")
                    val imageUrl = jsonObj.getJSONArray("images").optJSONObject(0)?.getString("url")

                    requireActivity().runOnUiThread {
                        playlistTitleTextView.text = name
                        playlistDescTextView.text = description
                        if (!imageUrl.isNullOrEmpty()) {
                            Glide.with(requireContext()).load(imageUrl).into(playlistImageView)
                        }
                    }
                } else {
                    requireActivity().runOnUiThread {
                        playlistTitleTextView.text = "No se pudo obtener la playlist"
                    }
                }
            }
        })
    }
}
