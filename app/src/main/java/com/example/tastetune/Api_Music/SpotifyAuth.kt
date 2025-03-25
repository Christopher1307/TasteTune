package com.example.tastetune.Api_Music

import android.os.Build
import androidx.annotation.RequiresApi
import okhttp3.*
import java.io.IOException
import java.util.Base64

object SpotifyAuth {
    private const val CLIENT_ID = "b50b2c4af35b42469127fbbc9e71b158"
    private const val CLIENT_SECRET = "70d3d41d86e345fcbce376004d76a173"
    private const val REDIRECT_URI = "http://localhost:8888/callback"
    private const val AUTH_URL = "https://accounts.spotify.com/authorize"
    private const val TOKEN_URL = "https://accounts.spotify.com/api/token"
    private const val SCOPES = "user-read-private user-read-email playlist-modify-public playlist-modify-private"

    private val client = OkHttpClient()

    fun getAuthUrl(): String {
        return "$AUTH_URL?client_id=$CLIENT_ID&response_type=code&redirect_uri=$REDIRECT_URI&scope=$SCOPES"
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
            .addHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString("$CLIENT_ID:$CLIENT_SECRET".toByteArray()))
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val json = response.body?.string()
                    val sanitizedJson = json ?: ""
                    val accessToken = Regex("\"access_token\":\"(.*?)\"").find(sanitizedJson)?.groupValues?.get(1)
                    callback(accessToken)
                } else {
                    callback(null)
                }
            }
        })
    }
}
