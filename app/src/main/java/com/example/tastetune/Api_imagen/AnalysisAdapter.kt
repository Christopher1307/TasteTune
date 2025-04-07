// AnalysisAdapter.kt
package com.example.tastetune.Api_imagen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tastetune.R
import com.example.tastetune.data.Analysis

class AnalysisAdapter(private val analysisList: List<Analysis>) :
    RecyclerView.Adapter<AnalysisAdapter.AnalysisViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnalysisViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_analysis, parent, false)
        return AnalysisViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnalysisViewHolder, position: Int) {
        val analysis = analysisList[position]
        holder.foodLabel.text = analysis.foodLabel
        holder.timestamp.text = analysis.timestamp.toString()
    }

    override fun getItemCount() = analysisList.size

    inner class AnalysisViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foodLabel: TextView = itemView.findViewById(R.id.foodLabel)
        val timestamp: TextView = itemView.findViewById(R.id.timestamp)
    }
}
