package com.example.tastetune.Api_imagen

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.tastetune.R
import com.example.tastetune.data.Analysis
import com.example.tastetune.data.Playlist
import com.example.tastetune.data.TasteTuneDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class ImageSelectorFragment : Fragment() {

    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private var imageUri: Uri? = null
    private lateinit var imageView: ImageView
    private lateinit var database: TasteTuneDatabase

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_image_selector, container, false)

        // Inicializar la base de datos
        database = TasteTuneDatabase.getDatabase(requireContext())

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
        return File.createTempFile("IMG_${System.currentTimeMillis()}", ".jpg", storageDir)
    }

    private fun processImage(imageUri: Uri) {
        imageView.setImageURI(imageUri)

        val path = imageUri.path ?: return
        val foodLabel = "Comida Ejemplo"  // Aquí deberías reemplazar con la detección real

        // Guardar en la base de datos
        saveAnalysisToDatabase(foodLabel, path)
    }

    private fun saveAnalysisToDatabase(foodLabel: String, imagePath: String) {
        val timestamp = System.currentTimeMillis()

        lifecycleScope.launch(Dispatchers.IO) {
            val analysis = Analysis(foodLabel = foodLabel, timestamp = timestamp, imagePath = imagePath)
            val analysisId = database.analysisDao().insertAnalysis(analysis)

            val playlist = Playlist(
                analysisId = analysisId.toInt(),
                playlistId = "SPOTIFY_PLAYLIST_ID",
                name = "Playlist de $foodLabel",
                description = "Playlist generada para $foodLabel"
            )

            database.playlistDao().insertPlaylist(playlist)

            Log.d("ImageSelectorFragment", "Análisis y Playlist guardados correctamente.")
        }
    }
}
