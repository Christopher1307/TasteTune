package com.example.tastetune.Api_Music

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

private val client = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .build()

/**
 * Busca canciones en Spotify basadas en un término (por ejemplo, una etiqueta de comida)
 * y devuelve una lista de URIs de tracks encontrados a través del callback.
 */
fun searchTracks(accessToken: String, query: String, callback: (List<String>) -> Unit) {
    // Construir la URL de búsqueda, limitando a 10 resultados
    val url = "https://api.spotify.com/v1/search?q=$query&type=track&limit=10"
    val request = Request.Builder()
        .url(url)
        .header("Authorization", "Bearer $accessToken")
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            println("Error en búsqueda de canciones: ${e.message}")
            callback(emptyList())
        }

        override fun onResponse(call: Call, response: Response) {
            if (!response.isSuccessful) {
                println("Error en búsqueda de canciones: ${response.body?.string()}")
                callback(emptyList())
                return
            }
            val bodyString = response.body?.string() ?: ""
            val json = JSONObject(bodyString)
            val tracks = json.getJSONObject("tracks").getJSONArray("items")
            val trackUris = mutableListOf<String>()
            for (i in 0 until tracks.length()) {
                val track = tracks.getJSONObject(i)
                val uri = track.getString("uri")
                trackUris.add(uri)
            }
            callback(trackUris)
        }
    })
}

/**
 * Agrega canciones a una playlist de Spotify.
 * @param accessToken El token de acceso para autenticar la solicitud.
 * @param playlistId El ID de la playlist a la que se agregan las canciones.
 * @param trackUris Lista de URIs de canciones a agregar.
 * @param callback Callback que devuelve true si se agregaron las canciones exitosamente, o false en caso de error.
 */
fun addTracksToPlaylist(
    accessToken: String,
    playlistId: String,
    trackUris: List<String>,
    callback: (Boolean) -> Unit
) {
    // Crear un objeto JSON con el arreglo de URIs de canciones
    val jsonBody = JSONObject().apply {
        put("uris", JSONArray(trackUris))
    }.toString()

    val body = jsonBody.toRequestBody("application/json".toMediaType())

    val request = Request.Builder()
        .url("https://api.spotify.com/v1/playlists/$playlistId/tracks")
        .header("Authorization", "Bearer $accessToken")
        .header("Content-Type", "application/json")
        .post(body)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                callback(true)
            } else {
                println("Error adding tracks: ${response.body?.string()}")
                callback(false)
            }
        }

        override fun onFailure(call: Call, e: IOException) {
            println("Error: ${e.message}")
            callback(false)
        }
    })
}
