package com.ghanshyam.chronosapp.data

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class ReminderRepository {
  private val firestore = FirebaseFirestore.getInstance()
  private val storageRef = FirebaseStorage.getInstance().reference
  private val remindersCol = firestore.collection("reminders")

  suspend fun uploadImage(userId: String, imageUri: Uri): String {
    val path = "images/$userId/${UUID.randomUUID()}.jpg"
    val imgRef = storageRef.child(path)
    imgRef.putFile(imageUri).await()
    return imgRef.downloadUrl.await().toString()
  }

  suspend fun addReminder(userId: String, reminder: Reminder) {
    val docRef = remindersCol
      .document(userId)
      .collection("userReminders")
      .document()
    val withId = reminder.copy(id = docRef.id)
    docRef.set(withId).await()
  }
  suspend fun updateReminder(
    userId: String,
    reminder: Reminder
  ) {
    remindersCol
      .document(userId)
      .collection("userReminders")
      .document(reminder.id)
      .set(reminder)
      .await()
  }

  suspend fun deleteReminder(userId: String, reminderId: String) {
    remindersCol
      .document(userId)
      .collection("userReminders")
      .document(reminderId)
      .delete()
      .await()
  }

  fun getRemindersFlow(userId: String): Flow<List<Reminder>> = callbackFlow {
    val query = firestore
      .collection("reminders")
      .document(userId)
      .collection("userReminders")
      .orderBy("timestamp", Query.Direction.DESCENDING)

    val subscription = query.addSnapshotListener { snap, err ->
      when {
        err != null -> {
          if (err.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
            close()
          } else {
            close(err)
          }
        }
        snap != null -> {
          val list = snap.documents.mapNotNull { it.toObject(Reminder::class.java) }
          trySend(list)
        }
      }
    }

    awaitClose { subscription.remove() }
  }
}
