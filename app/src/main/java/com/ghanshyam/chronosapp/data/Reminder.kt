package com.ghanshyam.chronosapp.data

data class Reminder(
  val id: String = "",
  val title: String = "",
  val description: String = "",
  val timestamp: Long = 0L,
  val imageUrl: String? = null
)
