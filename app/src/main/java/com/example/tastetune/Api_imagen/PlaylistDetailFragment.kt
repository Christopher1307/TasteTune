package com.example.tastetune.Api_imagen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.example.tastetune.R

class PlaylistDetailFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_playlist_detail, container, false)
        val playlistTextView = view.findViewById<TextView>(R.id.playlistTextView)

        val analysisId = arguments?.getString("analysisId")
        analysisId?.let { fetchAnalysisFromFirebase(it, playlistTextView) }

        return view
    }

    private fun fetchAnalysisFromFirebase(analysisId: String, playlistTextView: TextView) {
        db.collection("analyses").document(analysisId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val foodLabel = document.getString("foodLabel")
                    val playlistId = document.getString("playlistId")
                    playlistTextView.text = "Playlist Generada para: $foodLabel\n\nPlaylist ID: $playlistId"
                } else {
                    playlistTextView.text = "No se encontró la playlist."
                }
            }
            .addOnFailureListener { e ->
                playlistTextView.text = "Error al obtener la playlist: ${e.message}"
            }
    }
}
