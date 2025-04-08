package com.example.tastetune.Api_imagen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.tastetune.R
import com.example.tastetune.data.Analysis

class PlaylistDetailFragment : Fragment() {

    private lateinit var playlistTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_playlist_detail, container, false)
        playlistTextView = view.findViewById(R.id.playlistTextView)

        val analysis = arguments?.getSerializable("analysis") as? Analysis
        analysis?.let {
            playlistTextView.text = "Playlist Generada para: ${it.foodLabel}\n\nPlaylist ID: ${it.playlistId}"
        }

        return view
    }

    companion object {
        fun newInstance(analysis: Analysis): PlaylistDetailFragment {
            val fragment = PlaylistDetailFragment()
            val args = Bundle()
            args.putSerializable("analysis", analysis)
            fragment.arguments = args
            return fragment
        }
    }
}
