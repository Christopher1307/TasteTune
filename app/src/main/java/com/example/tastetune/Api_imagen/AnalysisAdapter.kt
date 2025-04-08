package com.example.tastetune

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tastetune.data.Analysis

class AnalysisAdapter(private var analyses: List<Analysis>) : RecyclerView.Adapter<AnalysisAdapter.AnalysisViewHolder>() {

    inner class AnalysisViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foodLabelTextView: TextView = itemView.findViewById(R.id.foodLabelTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnalysisViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_analysis, parent, false)
        return AnalysisViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnalysisViewHolder, position: Int) {
        val analysis = analyses[position]
        holder.foodLabelTextView.text = analysis.foodLabel
    }

    override fun getItemCount(): Int = analyses.size

    fun updateData(newAnalyses: List<Analysis>) {
        analyses = newAnalyses
        notifyDataSetChanged()
    }
}
