package com.ghanshyam.chronosapp.ai

class GreetingRepository(
  private val api: PollinationsApi = NetworkModule.pollinationsApi
) {
  suspend fun getGreeting(prompt: String): String {
    // replace spaces with '+' for URL
    val encoded = prompt.trim().replace(" ", "+")
    return api.fetchGreeting(encoded)
  }
}
