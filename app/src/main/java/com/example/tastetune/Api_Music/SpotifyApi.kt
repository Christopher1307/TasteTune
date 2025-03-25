package com.example.tastetune.Api_Music

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

private val client = OkHttpClient()

fun getUserProfile(accessToken: String, callback: (String) -> Unit) {
    val request = Request.Builder()
        .url("https://api.spotify.com/v1/me")
        .header("Authorization", "Bearer $accessToken")
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                val profile = response.body?.string()
                callback(profile!!)
            } else {
                println("Error fetching profile: ${response.body?.string()}")
            }
        }
        override fun onFailure(call: Call, e: IOException) {
            println("Error: ${e.message}")
        }
    })
}

fun createPlaylist(accessToken: String, userId: String, playlistName: String, callback: (String) -> Unit) {
    val requestBody = """
        {
            "name": "$playlistName",
            "description": "Playlist",
            "public": false
        }
    """.trimIndent()

    val body = requestBody.toRequestBody("application/json".toMediaType())

    val request = Request.Builder()
        .url("https://api.spotify.com/v1/users/$userId/playlists")
        .header("Authorization", "Bearer $accessToken")
        .header("Content-Type", "application/json")
        .post(body)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                val json = response.body?.string()
                val playlistId = Regex("\"id\":\"(.*?)\"").find(json!!)?.groupValues?.get(1)
                if (playlistId != null) {
                    callback(playlistId)
                } else {
                    println("Error: No playlist ID found.")
                }
            } else {
                println("Error creating playlist: ${response.body?.string()}")
            }
        }
        override fun onFailure(call: Call, e: IOException) {
            println("Error: ${e.message}")
        }
    })
}