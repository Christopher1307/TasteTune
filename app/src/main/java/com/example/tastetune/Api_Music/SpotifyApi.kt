package com.example.tastetune.Api_Music

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
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
                val json = JSONObject(profile!!)
                val userId = json.getString("id")
                callback(userId)
            } else {
                println("Error fetching profile: ${response.body?.string()}")
            }
        }

        override fun onFailure(call: Call, e: IOException) {
            println("Error: ${e.message}")
        }
    })
}

fun createPlaylist(
    accessToken: String,
    userId: String,
    playlistName: String,
    callback: (String) -> Unit
) {
    val requestBody = """
        {
            "name": "$playlistName",
            "description": "Playlist generada con TasteTune",
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
                val json = JSONObject(response.body!!.string())
                val playlistId = json.getString("id")
                callback(playlistId)
            } else {
                println("Error creating playlist: ${response.body?.string()}")
            }
        }

        override fun onFailure(call: Call, e: IOException) {
            println("Error: ${e.message}")
        }
    })
}

fun searchTracksSpotify(
    accessToken: String,
    query: String,
    callback: (List<String>) -> Unit
) {
    val url = "https://api.spotify.com/v1/search?q=$query&type=track&limit=10"
    val request = Request.Builder()
        .url(url)
        .header("Authorization", "Bearer $accessToken")
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                val json = JSONObject(response.body!!.string())
                val tracks = json.getJSONObject("tracks").getJSONArray("items")
                val uris = mutableListOf<String>()
                for (i in 0 until tracks.length()) {
                    val uri = tracks.getJSONObject(i).getString("uri")
                    uris.add(uri)
                }
                callback(uris)
            } else {
                println("Error searching tracks: ${response.body?.string()}")
            }
        }

        override fun onFailure(call: Call, e: IOException) {
            println("Error: ${e.message}")
        }
    })
}

fun getPlaylistDetails(
    accessToken: String,
    playlistId: String,
    callback: (name: String, description: String, imageUrl: String) -> Unit
) {
    val request = Request.Builder()
        .url("https://api.spotify.com/v1/playlists/$playlistId")
        .header("Authorization", "Bearer $accessToken")
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                val json = JSONObject(response.body!!.string())
                val name = json.getString("name")
                val description = json.getString("description")
                val imageUrl = json.getJSONArray("images").getJSONObject(0).getString("url")
                callback(name, description, imageUrl)
            } else {
                println("Error obteniendo playlist: ${response.body?.string()}")
            }
        }

        override fun onFailure(call: Call, e: IOException) {
            println("Error de red: ${e.message}")
        }
    })
}

fun addTracksToPlaylistSimple(
    accessToken: String,
    playlistId: String,
    trackUris: List<String>,
    callback: () -> Unit
) {
    val json = JSONObject()
    json.put("uris", JSONArray(trackUris))
    val body = json.toString().toRequestBody("application/json".toMediaType())

    val request = Request.Builder()
        .url("https://api.spotify.com/v1/playlists/$playlistId/tracks")
        .header("Authorization", "Bearer $accessToken")
        .post(body)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                callback()
            } else {
                println("Error adding tracks: ${response.body?.string()}")
            }
        }

        override fun onFailure(call: Call, e: IOException) {
            println("Error: ${e.message}")
        }
    })
}
