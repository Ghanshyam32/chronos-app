// app/src/main/java/com/ghanshyam/chronosapp/reminder/AddReminderDialog.kt
package com.ghanshyam.chronosapp.reminder

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import java.util.*

@Composable
fun AddReminderDialog(
  onSave: (title: String, description: String, timestamp: Long, imageUri: Uri?) -> Unit,
  onCancel: () -> Unit
) {
  val context = LocalContext.current

  // Form state
  var title by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var timestamp by remember { mutableStateOf(System.currentTimeMillis()) }
  var imageUri by remember { mutableStateOf<Uri?>(null) }

  // Date/time pickers
  val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
  val datePicker = DatePickerDialog(
    context,
    { _, y, m, d ->
      calendar.set(y, m, d)
      timestamp = calendar.timeInMillis
    },
    calendar.get(Calendar.YEAR),
    calendar.get(Calendar.MONTH),
    calendar.get(Calendar.DAY_OF_MONTH)
  )
  val timePicker = TimePickerDialog(
    context,
    { _, h, min ->
      calendar.set(Calendar.HOUR_OF_DAY, h)
      calendar.set(Calendar.MINUTE, min)
      timestamp = calendar.timeInMillis
    },
    calendar.get(Calendar.HOUR_OF_DAY),
    calendar.get(Calendar.MINUTE),
    false
  )

  // Image picker
  val launcher = rememberLauncherForActivityResult(GetContent()) { uri ->
    imageUri = uri
  }

  AlertDialog(
    onDismissRequest = onCancel,
    title = { Text("Add Reminder") },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Image picker
        Box(
          Modifier
            .size(80.dp)
            .clip(CircleShape)
            .clickable { launcher.launch("image/*") },
          contentAlignment = Alignment.Center
        ) {
          if (imageUri != null) {
            Image(
              painter = rememberAsyncImagePainter(imageUri),
              contentDescription = "Picked image",
              modifier = Modifier.fillMaxSize()
            )
          } else {
            Surface(shape = CircleShape, tonalElevation = 2.dp) {
              Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Tap\nImage", textAlign = TextAlign.Center)
              }
            }
          }
        }

        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Title") },
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("Notes (optional)") },
          modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Button(onClick = { datePicker.show() }) { Text("Pick Date") }
          Button(onClick = { timePicker.show() }) { Text("Pick Time") }
        }

        Text(
          text = "Will notify: ${Date(timestamp)}",
          style = MaterialTheme.typography.bodySmall
        )
      }
    },
    confirmButton = {
      TextButton(
        onClick = {
          onSave(title.trim(), description.trim(), timestamp, imageUri)
        },
        enabled = title.isNotBlank()
      ) {
        Text("Save")
      }
    },
    dismissButton = {
      TextButton(onClick = onCancel) {
        Text("Cancel")
      }
    }
  )
}
