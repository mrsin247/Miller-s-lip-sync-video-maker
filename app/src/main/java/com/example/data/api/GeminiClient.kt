package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiClient {
    private const val TAG = "GeminiClient"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun generateContent(prompt: String, systemInstruction: String? = null): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read GEMINI_API_KEY from BuildConfig: ${e.message}")
            ""
        }

        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w(TAG, "GEMINI_API_KEY is not configured or is the default placeholder.")
            return@withContext "API Key not configured. Please add your GEMINI_API_KEY in the AI Studio Secrets panel."
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()

        // Build request body manually using org.json for robustness
        val requestJson = JSONObject()
        val contentsArray = JSONArray()
        val contentObj = JSONObject()
        val partsArray = JSONArray()
        val partObj = JSONObject()

        partObj.put("text", prompt)
        partsArray.put(partObj)
        contentObj.put("parts", partsArray)
        contentsArray.put(contentObj)
        requestJson.put("contents", contentsArray)

        if (systemInstruction != null) {
            val systemInstructionObj = JSONObject()
            val systemInstructionPartsArray = JSONArray()
            val systemInstructionPartObj = JSONObject()
            systemInstructionPartObj.put("text", systemInstruction)
            systemInstructionPartsArray.put(systemInstructionPartObj)
            systemInstructionObj.put("parts", systemInstructionPartsArray)
            requestJson.put("systemInstruction", systemInstructionObj)
        }

        val requestBody = requestJson.toString().toRequestBody(mediaType)
        val request = Request.Builder()
            .url("$BASE_URL?key=$apiKey")
            .post(requestBody)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val bodyString = response.body?.string()
                if (!response.isSuccessful) {
                    Log.e(TAG, "Request failed with code: ${response.code}, body: $bodyString")
                    return@withContext "Error calling Gemini API: HTTP ${response.code}\n$bodyString"
                }

                if (bodyString.isNullOrEmpty()) {
                    return@withContext "Error: Empty response from Gemini API"
                }

                val responseJson = JSONObject(bodyString)
                val candidates = responseJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        return@withContext parts.getJSONObject(0).optString("text", "No text found in response part.")
                    }
                }
                return@withContext "Error: Could not parse response content."
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during Gemini API call", e)
            return@withContext "Network error: ${e.message}"
        }
    }
}
