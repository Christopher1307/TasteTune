package com.example.tastetune


import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.tastetune.Api_Music.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import java.io.File

class ImageSelectorFragment : Fragment() {

    private lateinit var imageView: ImageView
    private lateinit var selectImageButton: Button
    private lateinit var takePhotoButton: Button
    private lateinit var photoUri: Uri

    private val db = FirebaseFirestore.getInstance()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val selectImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            uri?.let {
                try {
                    sharedViewModel.setImageUri(it)
                    loadImageSafely(it)
                    processImageSafely(it)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error al acceder a la imagen", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val takePhotoLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            sharedViewModel.setImageUri(photoUri)
            loadImageSafely(photoUri)
            processImageSafely(photoUri)
        } else {
            Toast.makeText(requireContext(), "No se tomó la foto", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_image_selector, container, false)

        imageView = view.findViewById(R.id.imageView)
        selectImageButton = view.findViewById(R.id.selectImageButton)
        takePhotoButton = view.findViewById(R.id.btn_take_photo)

        sharedViewModel.clear()
        loadLastImageFromFirebase()

        selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            selectImageLauncher.launch(intent)
        }

        takePhotoButton.setOnClickListener {
            val photoFile = File.createTempFile("photo_", ".jpg", requireContext().cacheDir)
            photoUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.provider",
                photoFile
            )
            takePhotoLauncher.launch(photoUri)
        }

        return view
    }

    private fun loadImageSafely(uri: Uri) {
        try {
            Glide.with(requireContext())
                .load(uri)
                .into(imageView)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error al mostrar la imagen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun processImageSafely(uri: Uri) {
        try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("temp_image", ".jpg", requireContext().cacheDir)
            inputStream?.copyTo(tempFile.outputStream())
            inputStream?.close()

            val bitmap = BitmapFactory.decodeFile(tempFile.absolutePath)
            val image = InputImage.fromBitmap(bitmap, 0)

            processImage(image)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error al procesar la imagen", Toast.LENGTH_LONG).show()
        }
    }

    private fun processImage(image: InputImage) {
        val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

        labeler.process(image)
            .addOnSuccessListener { labels ->
                if (labels.isNotEmpty()) {
                    val foodLabel = labels[0].text
                    val imagePath = sharedViewModel.imageUri.value?.toString() ?: ""
                    val token = SpotifyAuth.accessToken
                    if (token != null) {
                        generateSpotifyPlaylist(token, foodLabel, imagePath)
                    } else {
                        Toast.makeText(requireContext(), "Token de Spotify no disponible", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "No se detectó comida", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al analizar la imagen", Toast.LENGTH_SHORT).show()
            }
    }

    private fun generateSpotifyPlaylist(accessToken: String, foodLabel: String, imagePath: String) {
        getUserProfile(accessToken) { userId ->
            createPlaylist(accessToken, userId, "Música para $foodLabel") { playlistId ->
                sharedViewModel.postPlaylistId(playlistId)
                searchTracksSpotify(accessToken, foodLabel) { trackUris ->
                    addTracksToPlaylist(accessToken, playlistId, trackUris) {
                        saveAnalysisToFirebase(foodLabel, playlistId, imagePath)
                        requireActivity().runOnUiThread {
                            findNavController().navigate(R.id.playlistDetailFragment)
                        }
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
                Toast.makeText(requireContext(), "Playlist creada y guardada", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun loadLastImageFromFirebase() {
        db.collection("analyses")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val doc = documents.documents[0]
                    val imagePath = doc.getString("imagePath")
                    if (!imagePath.isNullOrEmpty()) {
                        val uri = Uri.parse(imagePath)
                        sharedViewModel.setImageUri(uri)
                        loadImageSafely(uri)
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "No se pudo cargar la última imagen", Toast.LENGTH_SHORT).show()
            }
    }
}
