package com.example.tastetune.Api_Music

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import okhttp3.*
import java.io.IOException
import java.util.Base64

object SpotifyAuth {

    // 🔹 Aquí defines el token para que otras clases lo puedan acceder o modificar
    var accessToken: String? = null

    private const val CLIENT_ID = "" // 🔹 Aquí debes poner tu CLIENT_ID si lo coloco yo me roban el token
    private const val CLIENT_SECRET = "" // 🔹 Aquí debes poner tu CLIENT_SECRET
    private const val REDIRECT_URI = "tastetune://callback"
    private const val AUTH_URL = "https://accounts.spotify.com/authorize"
    private const val TOKEN_URL = "https://accounts.spotify.com/api/token"
    private const val SCOPES = "user-read-private user-read-email playlist-modify-public playlist-modify-private"

    private val client = OkHttpClient()

    fun getAuthUrl(): String {
        return "$AUTH_URL?client_id=$CLIENT_ID&response_type=code&redirect_uri=$REDIRECT_URI&scope=$SCOPES"
    }

    fun saveAccessToken(context: Context, token: String) {
        val prefs = context.getSharedPreferences("spotify_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("access_token", token).apply()
        accessToken = token
    }

    fun loadAccessToken(context: Context): String? {
        val prefs = context.getSharedPreferences("spotify_prefs", Context.MODE_PRIVATE)
        accessToken = prefs.getString("access_token", null)
        return accessToken
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun requestAccessToken(code: String, callback: (String?) -> Unit) {
        val requestBody = FormBody.Builder()
            .add("grant_type", "authorization_code")
            .add("code", code)
            .add("redirect_uri", REDIRECT_URI)
            .build()

        val request = Request.Builder()
            .url(TOKEN_URL)
            .addHeader(
                "Authorization", "Basic " +
                        Base64.getEncoder().encodeToString("$CLIENT_ID:$CLIENT_SECRET".toByteArray())
            )
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val json = response.body?.string()
                    val token = Regex("\"access_token\":\"(.*?)\"").find(json ?: "")?.groupValues?.get(1)
                    accessToken = token // 🔹 Guardamos el token aquí
                    callback(token)
                } else {
                    callback(null)
                }
            }
        })
    }

    fun clearAccessToken(context: Context) {
        val prefs = context.getSharedPreferences("spotify_prefs", Context.MODE_PRIVATE)
        prefs.edit().remove("access_token").apply()
        accessToken = null
    }

}
