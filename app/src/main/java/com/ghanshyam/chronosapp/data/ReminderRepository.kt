// app/src/main/java/com/ghanshyam/chronosapp/data/ReminderRepository.kt
package com.ghanshyam.chronosapp.data

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose
import com.google.firebase.firestore.ListenerRegistration
import java.util.*

class ReminderRepository {
  private val firestore = FirebaseFirestore.getInstance()
  private val col = firestore.collection("reminders")

  /** Uploads the image, returns its public URL */
  suspend fun uploadImage(userId: String, uri: Uri): String {
    val ref = FirebaseStorage.getInstance()
      .reference
      .child("images/$userId/${UUID.randomUUID()}")
    ref.putFile(uri).await()
    return ref.downloadUrl.await().toString()
  }

  /**
   * Saves the reminder under the user’s UID.
   * Returns the reminder with its new document ID filled in.
   */
  suspend fun addReminder(userId: String, reminder: Reminder): Reminder {
    val docRef = col
      .document(userId)
      .collection("userReminders")
      .document()
    val withId = reminder.copy(id = docRef.id)
    docRef.set(withId).await()
    return withId
  }

  suspend fun updateReminder(userId: String, reminder: Reminder) {
    firestore
      .collection("reminders")
      .document(userId)
      .collection("userReminders")
      .document(reminder.id)
      .set(reminder)
      .await()
  }

  suspend fun deleteReminder(userId: String, reminderId: String) {
    firestore
      .collection("reminders")
      .document(userId)
      .collection("userReminders")
      .document(reminderId)
      .delete()
      .await()
  }

  fun getRemindersFlow(userId: String): Flow<List<Reminder>> = callbackFlow {
    val colRef = firestore
      .collection("reminders")
      .document(userId)
      .collection("userReminders")
      .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.ASCENDING)
    val sub: ListenerRegistration = colRef.addSnapshotListener { snap, err ->
      if (err != null || snap == null) {
        close(err ?: Exception("FireStore error"))
        return@addSnapshotListener
      }
      val list = snap.documents.mapNotNull { it.toObject(Reminder::class.java) }
      trySend(list)
    }
    awaitClose { sub.remove() }
  }
}
