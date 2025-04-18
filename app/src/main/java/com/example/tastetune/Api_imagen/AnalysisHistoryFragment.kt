package com.example.tastetune

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tastetune.data.Analysis
import com.google.firebase.firestore.FirebaseFirestore

class AnalysisHistoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AnalysisAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_analysis_history, container, false)

        recyclerView = view.findViewById(R.id.analysisRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = AnalysisAdapter(emptyList()) { analysis ->
            val action = AnalysisHistoryFragmentDirections
                .actionAnalysisHistoryFragmentToPlaylistDetailFragment(analysis.playlistId)
            findNavController().navigate(action)
        }
        recyclerView.adapter = adapter

        loadAnalyses()

        return view
    }

    private fun loadAnalyses() {
        db.collection("analyses")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val list = result.documents.mapNotNull { doc ->
                    val id = doc.id
                    val foodLabel = doc.getString("foodLabel") ?: ""
                    val playlistId = doc.getString("playlistId") ?: ""
                    val timestamp = doc.getLong("timestamp") ?: 0L
                    val imagePath = doc.getString("imagePath") ?: ""
                    Analysis(id, foodLabel, playlistId, timestamp, imagePath)
                }
                adapter.updateData(list)
            }
    }
}