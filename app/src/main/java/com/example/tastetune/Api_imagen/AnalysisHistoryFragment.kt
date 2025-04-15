package com.example.tastetune.Api_imagen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tastetune.AnalysisAdapter
import com.example.tastetune.R
import com.example.tastetune.data.FirestoreHelper
import com.example.tastetune.data.Analysis

class AnalysisHistoryFragment : Fragment() {

    private lateinit var foodLabelTextView: TextView
    private lateinit var analysisRecyclerView: RecyclerView
    private lateinit var analysisAdapter: AnalysisAdapter
    private val firestoreHelper = FirestoreHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_analysis_history, container, false)

        foodLabelTextView = view.findViewById(R.id.foodLabelTextView)
        analysisRecyclerView = view.findViewById(R.id.analysisRecyclerView)

        analysisAdapter = AnalysisAdapter(listOf())
        analysisRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        analysisRecyclerView.adapter = analysisAdapter

        loadAnalysisData()

        return view
    }

    private fun loadAnalysisData() {
        firestoreHelper.getAllAnalyses { analyses ->
            analysisAdapter.updateData(analyses)
            if (analyses.isNotEmpty()) {
                foodLabelTextView.text = "Comida detectada: ${analyses[0].foodLabel}"
            } else {
                foodLabelTextView.text = "No hay análisis disponibles."
            }
        }
    }
}
