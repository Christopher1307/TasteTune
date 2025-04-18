package com.example.tastetune

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tastetune.data.Analysis
import java.text.SimpleDateFormat
import java.util.*

class AnalysisAdapter(
    private var analysisList: List<Analysis>,
    private val onItemClick: (Analysis) -> Unit
) : RecyclerView.Adapter<AnalysisAdapter.AnalysisViewHolder>() {



    class AnalysisViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.itemImage)
        val labelView: TextView = view.findViewById(R.id.itemLabel)
        val dateView: TextView = view.findViewById(R.id.itemDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnalysisViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_analysis, parent, false)
        return AnalysisViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnalysisViewHolder, position: Int) {
        val analysis = analysisList[position]

        holder.labelView.text = analysis.foodLabel

        val formattedDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            .format(Date(analysis.timestamp))
        holder.dateView.text = formattedDate

        Glide.with(holder.itemView.context)
            .load(analysis.imagePath)
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            onItemClick(analysis)
        }
    }

    override fun getItemCount(): Int = analysisList.size

    fun updateData(newList: List<Analysis>) {
        analysisList = newList
        notifyDataSetChanged()
    }
}
