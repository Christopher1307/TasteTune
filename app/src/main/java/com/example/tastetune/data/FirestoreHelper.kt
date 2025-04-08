package com.example.tastetune.data

import com.google.firebase.firestore.FirebaseFirestore
import com.example.tastetune.data.Analysis

object FirestoreHelper {

    private val db = FirebaseFirestore.getInstance()

    fun getAllAnalyses(onComplete: (List<Analysis>) -> Unit) {
        db.collection("analyses")
            .get()
            .addOnSuccessListener { result ->
                val analyses = result.map { document ->
                    Analysis(
                        id = document.id,
                        foodLabel = document.getString("foodLabel") ?: "",
                        playlistId = document.getString("playlistId") ?: "",
                        timestamp = document.getLong("timestamp") ?: 0L,
                        imagePath = document.getString("imagePath") ?: ""
                    )
                }
                onComplete(analyses)
            }
            .addOnFailureListener {
                onComplete(emptyList())
            }
    }

    fun saveAnalysis(analysis: Analysis, onComplete: (Boolean) -> Unit) {
        val analysisMap = hashMapOf(
            "foodLabel" to analysis.foodLabel,
            "playlistId" to analysis.playlistId,
            "timestamp" to analysis.timestamp,
            "imagePath" to analysis.imagePath
        )

        db.collection("analyses").add(analysisMap)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
}
