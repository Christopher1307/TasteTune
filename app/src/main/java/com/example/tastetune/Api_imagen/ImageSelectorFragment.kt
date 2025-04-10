package com.example.tastetune

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.activity.result.contract.ActivityResultContracts
import com.example.tastetune.Api_Music.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

class ImageSelectorFragment : Fragment() {

    var accessToken: String? = null

    private lateinit var imageView: ImageView
    private lateinit var selectImageButton: Button
    private val db = FirebaseFirestore.getInstance()
    private var imageUri: Uri? = null

    private val selectImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri = result.data?.data
            imageUri?.let { uri ->
                imageView.setImageURI(uri)
                processImage(uri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_image_selector, container, false)

        imageView = view.findViewById(R.id.imageView)
        selectImageButton = view.findViewById(R.id.selectImageButton)

        selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            selectImageLauncher.launch(intent)
        }

        return view
    }

    private fun processImage(uri: Uri) {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val image = InputImage.fromBitmap(bitmap, 0)
        val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

        labeler.process(image)
            .addOnSuccessListener { labels ->
                if (labels.isNotEmpty()) {
                    val foodLabel = labels[0].text
                    ensureAccessToken { accessToken ->
                        if (accessToken != null) {
                            generateSpotifyPlaylist(accessToken, foodLabel, uri.toString())
                        } else {
                            Toast.makeText(requireContext(), "Error obteniendo token de Spotify", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "No se detectó ninguna comida.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al procesar la imagen.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun ensureAccessToken(callback: (String?) -> Unit) {
        val token = SpotifyAuth.accessToken
        if (token != null) {
            callback(token)
        } else {
            val authCode = "REEMPLAZA_ESTO_POR_EL_CODIGO_REAL"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                SpotifyAuth.requestAccessToken(authCode) {
                    SpotifyAuth.accessToken = it
                    callback(it)
                }
            } else {
                callback(null)
            }
        }
    }

    private fun generateSpotifyPlaylist(accessToken: String, foodLabel: String, imagePath: String) {
        getUserProfile(accessToken) { userId ->
            createPlaylist(accessToken, userId, "Música para $foodLabel") { playlistId ->
                searchTracksSpotify(accessToken, foodLabel) { trackUris ->
                    addTracksToPlaylistSimple(accessToken, playlistId, trackUris) {
                        saveAnalysisToFirebase(foodLabel, playlistId, imagePath)
                    }

                }
            }
        }
    }

    private fun saveAnalysisToFirebase(foodLabel: String, playlistId: String, imageUri: String) {
        val analysis = hashMapOf(
            "foodLabel" to foodLabel,
            "timestamp" to System.currentTimeMillis(),
            "imagePath" to imageUri,
            "playlistId" to playlistId
        )

        db.collection("analyses").add(analysis)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Playlist creada y guardada correctamente.", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error al guardar en Firebase: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
