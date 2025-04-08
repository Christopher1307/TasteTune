// AnalysisHistoryFragment.kt
package com.example.tastetune.Api_imagen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.fragment.app.commit
import com.example.tastetune.R
import com.example.tastetune.data.Analysis
import com.example.tastetune.data.TasteTuneDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AnalysisHistoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AnalysisAdapter
    private lateinit var database: TasteTuneDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_analysis_history, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        database = TasteTuneDatabase.getDatabase(requireContext())

        loadAnalysisHistory()

        return view
    }

    private fun loadAnalysisHistory() {
        lifecycleScope.launch(Dispatchers.IO) {
            val analysisList = database.analysisDao().getAllAnalysis()

            withContext(Dispatchers.Main) {
                adapter = AnalysisAdapter(analysisList) { analysis ->
                    val fragment = PlaylistDetailFragment.newInstance(analysis)
                    parentFragmentManager.commit {
                        replace(R.id.fragment_container, fragment)
                        addToBackStack(null)
                    }
                }
                recyclerView.adapter = adapter
            }
        }
    }
}
