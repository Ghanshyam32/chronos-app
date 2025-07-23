// app/src/main/java/com/ghanshyam/chronosapp/ai/PollinationsApi.kt
package com.ghanshyam.chronosapp.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder

object PollinationsApi {
  private val client = OkHttpClient()

  suspend fun fetchText(prompt: String): String = withContext(Dispatchers.IO) {
    // Build the URL-encoded endpoint
    val encoded = URLEncoder.encode(prompt, "UTF-8")
    val url = "https://text.pollinations.ai/prompt/$encoded"
    val request = Request.Builder()
      .url(url)
      .get()
      .build()

    client.newCall(request).execute().use { resp ->
      if (!resp.isSuccessful) throw Exception("HTTP ${resp.code}")
      resp.body?.string() ?: throw Exception("Empty response")
    }
  }
}
