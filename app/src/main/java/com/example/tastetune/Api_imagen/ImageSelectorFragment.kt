import android.content.Context
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

private val db = FirebaseFirestore.getInstance()

suspend fun saveAnalysisToFirebase(context: Context, foodLabel: String, playlistSpotifyId: String, imageUri: String) {
    val analysis = hashMapOf(
        "foodLabel" to foodLabel,
        "timestamp" to System.currentTimeMillis(),
        "imagePath" to imageUri,
        "playlistId" to playlistSpotifyId
    )

    db.collection("analyses").add(analysis)
        .addOnSuccessListener {
            Toast.makeText(context, "Playlist creada y guardada correctamente.", Toast.LENGTH_LONG).show()
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Error al guardar en Firebase: ${e.message}", Toast.LENGTH_LONG).show()
        }
}
