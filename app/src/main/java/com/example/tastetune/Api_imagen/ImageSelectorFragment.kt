package com.example.tastetune.Api_imagen

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.tastetune.R
import java.io.File

class ImageSelectorFragment : Fragment() {

    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri = result.data?.data
                selectedImageUri?.let { uri ->
                    processImage(uri)
                }
            }
        }

        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                val capturedUri = imageUri
                if (capturedUri != null) {
                    processImage(capturedUri)
                }
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_image_selector, container, false)

        val galleryButton: Button = view.findViewById(R.id.btn_select_gallery)
        val cameraButton: Button = view.findViewById(R.id.btn_take_photo)

        galleryButton.setOnClickListener { openGallery() }
        cameraButton.setOnClickListener { openCamera() }

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
        Log.d("ImageSelectorFragment", "Processing image: $imageUri")
        // Call Clarifai here or your desired image analysis
    }
}