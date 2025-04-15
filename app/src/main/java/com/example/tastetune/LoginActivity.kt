package com.example.tastetune

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.tastetune.Api_Music.SpotifyAuth

class LoginActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Solo lanzar navegador si aún no hay token
        if (SpotifyAuth.accessToken == null) {
            val authUrl = SpotifyAuth.getAuthUrl()
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(authUrl))
            startActivity(intent)
            // 👇 No hacemos finish(), dejamos que la actividad reciba el intent cuando regrese
        } else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        handleRedirect(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleRedirect(intent: Intent?) {
        val uri = intent?.data
        if (uri != null && uri.toString().startsWith("tastetune://callback")) {
            val code = uri.getQueryParameter("code")
            if (code != null) {
                SpotifyAuth.requestAccessToken(code) { token ->
                    runOnUiThread {
                        if (token != null) {
                            Toast.makeText(this, "Token OK", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Error obteniendo token", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}

