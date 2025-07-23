// Reminder.kt
package com.ghanshyam.chronosapp.data

data class Reminder(
  val id: String = "",           // Firestore doc ID
  val title: String = "",        // user‑entered title
  val description: String = "",  // optional notes
  val timestamp: Long = 0L,      // millis since epoch
  val imageUrl: String? = null   // we’ll wire this later
)
