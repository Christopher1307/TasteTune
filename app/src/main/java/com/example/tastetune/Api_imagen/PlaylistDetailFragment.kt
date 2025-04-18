package com.example.tastetune.Api_imagen

import SharedViewModel
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.tastetune.Api_Music.getPlaylistDetails
import com.example.tastetune.Api_Music.SpotifyAuth
import com.example.tastetune.R

class PlaylistDetailFragment : Fragment() {

    private lateinit var playlistTitleTextView: TextView
    private lateinit var playlistDescTextView: TextView
    private lateinit var playlistImageView: ImageView
    private val sharedViewModel: SharedViewModel by activityViewModels()

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
                val token = SpotifyAuth.accessToken
                if (token != null) {
                    getPlaylistDetails(token, it) { name, description, imageUrl ->
                        requireActivity().runOnUiThread {
                            playlistTitleTextView.text = name
                            playlistDescTextView.text = description
                            Glide.with(requireContext()).load(imageUrl).into(playlistImageView)
                        }
                    }
                } else {
                    playlistTitleTextView.text = "Token no disponible"
                }
            } ?: run {
                playlistTitleTextView.text = "No hay playlist generada"
            }
        }

        return view
    }
}
