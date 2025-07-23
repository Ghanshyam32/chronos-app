// app/src/main/java/com/ghanshyam/chronosapp/ai/AIGreetingRepository.kt
package com.ghanshyam.chronosapp.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder

class AIGreetingRepository {
  private val client = OkHttpClient()

  /**
   * Fetches a greeting from Pollinations,
   * e.g. prompt="write a short birthday wish for Prashant"
   */
  suspend fun fetchGreeting(prompt: String): String = withContext(Dispatchers.IO) {
    val encoded = URLEncoder.encode(prompt, "utf-8")
    val url     = "https://text.pollinations.ai/prompt/$encoded"
    val req     = Request.Builder().url(url).build()
    client.newCall(req).execute().body?.string()
      ?: throw Exception("Empty response from AI")
  }
  suspend fun getGreeting(prompt: String): String {
    return PollinationsApi.fetchText(prompt)
  }
}
