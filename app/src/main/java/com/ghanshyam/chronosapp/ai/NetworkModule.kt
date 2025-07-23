package com.ghanshyam.chronosapp.ai

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object NetworkModule {
  val pollinationsApi: PollinationsApi by lazy {
    Retrofit.Builder()
      .baseUrl("https://text.pollinations.ai/")
      .addConverterFactory(ScalarsConverterFactory.create())
      .build()
      .create(PollinationsApi::class.java)
  }
}