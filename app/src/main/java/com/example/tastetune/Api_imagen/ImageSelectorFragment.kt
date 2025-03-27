package com.example.tastetune.Api_imagen

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.tastetune.R
import com.example.tastetune.Api_Music.searchTracks
import com.example.tastetune.Api_Music.addTracksToPlaylist
import com.example.tastetune.Api_Music.createPlaylist
import java.io.File

class ImageSelectorFragment : Fragment() {

    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private var imageUri: Uri? = null
    private lateinit var imageView: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_image_selector, container, false)

        // Verificación de permisos de cámara
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 100)
        }

        imageView = view.findViewById(R.id.img_selected)
        val galleryButton: Button = view.findViewById(R.id.btn_select_gallery)
        val cameraButton: Button = view.findViewById(R.id.btn_take_photo)

        galleryButton.setOnClickListener { openGallery() }
        cameraButton.setOnClickListener { openCamera() }

        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri = result.data?.data
                selectedImageUri?.let { processImage(it) }
            } else {
                Toast.makeText(requireContext(), "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT).show()
            }
        }

        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                imageUri?.let { processImage(it) }
            } else {
                Toast.makeText(requireContext(), "No se tomó ninguna foto", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun openCamera() {
        val context = requireContext()
        val localUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            createImageFile()
        )
        imageUri = localUri
        cameraLauncher.launch(localUri)
    }

    private fun createImageFile(): File {
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("IMG_\${System.currentTimeMillis()}", ".jpg", storageDir)
    }

    private fun getRealPathFromUri(uri: Uri): String? {
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            val index = it.getColumnIndex(MediaStore.Images.Media.DATA)
            if (index != -1 && it.moveToFirst()) it.getString(index) else null
        }
    }

    private fun processImage(imageUri: Uri) {
        Log.d("ImageSelectorFragment", "Procesando imagen: $imageUri")
        imageView.setImageURI(imageUri)

        val path = getRealPathFromUri(imageUri)
        if (path == null) {
            Toast.makeText(requireContext(), "No se pudo obtener la ruta de la imagen", Toast.LENGTH_SHORT).show()
            return
        }

        Thread {
            try {
                val labels = ClarifaiApi.analyzeImage(path)
                requireActivity().runOnUiThread {
                    if (labels.isNotEmpty()) {
                        val foodLabel = labels.first()
                        // Registro normal sin 'e'
                        Log.d("ImageSelectorFragment", "Comida detectada: ${labels.joinToString(", ")}")

                        val accessToken = "TU_SPOTIFY_ACCESS_TOKEN"
                        val userId = "TU_SPOTIFY_USER_ID"
                        createPlaylist(accessToken, userId, "Playlist de $foodLabel") { playlistId ->
                            if (playlistId != null) {
                                searchTracks(accessToken, foodLabel) { trackUris ->
                                    if (trackUris.isNotEmpty()) {
                                        addTracksToPlaylist(accessToken, playlistId, trackUris) { success ->
                                            if (success) {
                                                Toast.makeText(requireContext(), "Playlist creada y actualizada", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(requireContext(), "Error al agregar canciones", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } else {
                                        Toast.makeText(requireContext(), "No se encontraron canciones para $foodLabel", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(requireContext(), "Error al crear la playlist", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "No se detectó comida en la imagen", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }
}