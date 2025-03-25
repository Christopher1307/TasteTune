package com.example.tastetune.Api_imagen

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit

object ClarifaiApi {
    private const val API_KEY = "2862e768e87e4570974695517a4678d4"
    private const val MODEL_ID = "food-item-recognition" // Modelo de comida
    private const val API_URL = "https://api.clarifai.com/v2/models/$MODEL_ID/outputs"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    fun analyzeImage(imagePath: String): List<String> {
        val imageBase64 = File(imagePath).readBytes().encodeBase64()
        val requestBody = JSONObject().apply {
            put("inputs", listOf(
                JSONObject().apply {
                    put("data", JSONObject().apply {
                        put("image", JSONObject().apply {
                            put("base64", imageBase64)
                        })
                    })
                }
            ))
        }.toString()

        val request = Request.Builder()
            .url(API_URL)
            .post(RequestBody.create("application/json".toMediaTypeOrNull(), requestBody))
            .header("Authorization", "Key $API_KEY")
            .header("Content-Type", "application/json")
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) error("Error en la API: ${response.body?.string()}")

        val jsonResponse = JSONObject(response.body?.string() ?: "")
        val concepts = jsonResponse
            .getJSONArray("outputs")
            .getJSONObject(0)
            .getJSONObject("data")
            .getJSONArray("concepts")

        return List(concepts.length()) { i -> concepts.getJSONObject(i).getString("name") }
    }

    private fun ByteArray.encodeBase64(): String {
        return android.util.Base64.encodeToString(this, android.util.Base64.NO_WRAP)
    }
}
