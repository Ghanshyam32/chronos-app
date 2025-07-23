package com.ghanshyam.chronosapp.ai

import retrofit2.http.GET
import retrofit2.http.Path

interface PollinationsApi {
  @GET("prompt/{prompt}")
  suspend fun fetchGreeting(@Path("prompt") prompt: String): String
}