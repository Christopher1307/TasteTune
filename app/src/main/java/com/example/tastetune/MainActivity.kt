package com.example.tastetune

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.tastetune.Api_Music.SpotifyAuth
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var tvResults: TextView

    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_PICK = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Verificar token en SharedPreferences
        val token = SpotifyAuth.loadAccessToken(this)
        if (token == null) {
            Toast.makeText(this, "Token no encontrado, lanzando Login", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        // ✅ Configurar navegación con BottomNavigationView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setupWithNavController(navController)

        // Configuración para pruebas (si decides mantener botones directos)
        imageView = findViewById(R.id.imageView)
        tvResults = findViewById(R.id.tvResults)

        findViewById<Button>(R.id.btnCaptureImage)?.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }

        findViewById<Button>(R.id.btnSelectImage)?.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    imageView.setImageBitmap(imageBitmap)
                    processImage(InputImage.fromBitmap(imageBitmap, 0))
                }

                REQUEST_IMAGE_PICK -> {
                    val imageUri: Uri? = data?.data
                    if (imageUri != null) {
                        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            val source = ImageDecoder.createSource(this.contentResolver, imageUri)
                            ImageDecoder.decodeBitmap(source)
                        } else {
                            MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                        }
                        imageView.setImageBitmap(bitmap)
                        processImage(InputImage.fromBitmap(bitmap, 0))
                    }
                }
            }
        }
    }

    private fun processImage(image: InputImage) {
        val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

        labeler.process(image)
            .addOnSuccessListener { labels ->
                val resultText = labels.joinToString("\n") { "${it.text} - Precisión: ${(it.confidence * 100).toInt()}%" }
                tvResults.text = resultText
            }
            .addOnFailureListener { e ->
                tvResults.text = "Error: ${e.message}"
            }
    }
}
