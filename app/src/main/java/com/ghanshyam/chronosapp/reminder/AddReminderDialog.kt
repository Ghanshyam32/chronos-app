// app/src/main/java/com/ghanshyam/chronosapp/reminder/AddReminderDialog.kt
package com.ghanshyam.chronosapp.reminder

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import java.util.*

@Composable
fun AddReminderDialog(
  viewModel: ReminderViewModel,
  onCancel: () -> Unit
) {
  val context = LocalContext.current

  var title       by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var timestamp   by remember { mutableStateOf(System.currentTimeMillis()) }
  var imageUri    by remember { mutableStateOf<Uri?>(null) }

  val isSaving by viewModel.isSaving.collectAsState()

  // 1️⃣ Collect the new UiEvent.SaveSuccess(reminder)
  LaunchedEffect(viewModel.eventFlow) {
    viewModel.eventFlow.collect { event ->
      when (event) {
        is UiEvent.SaveSuccess -> {
          // schedule the notification now that we have the saved Reminder
          NotificationHelper.scheduleAlarm(
            context     = context,
            timeMillis  = event.reminder.timestamp,
            title       = event.reminder.title,
            description = event.reminder.description
          )
          onCancel()
        }
        is UiEvent.SaveError -> {
          Toast.makeText(context, "Error: ${event.message}", Toast.LENGTH_LONG).show()
        }
      }
    }
  }

  // pickers
  val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
  val dp = DatePickerDialog(
    context,
    { _, y,m,d -> calendar.set(y,m,d); timestamp = calendar.timeInMillis },
    calendar.get(Calendar.YEAR),
    calendar.get(Calendar.MONTH),
    calendar.get(Calendar.DAY_OF_MONTH)
  )
  val tp = TimePickerDialog(
    context,
    { _, h,min -> calendar.set(Calendar.HOUR_OF_DAY,h); calendar.set(Calendar.MINUTE,min); timestamp = calendar.timeInMillis },
    calendar.get(Calendar.HOUR_OF_DAY),
    calendar.get(Calendar.MINUTE),
    false
  )

  val gallery = rememberLauncherForActivityResult(GetContent()) {
    imageUri = it
  }

  Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    AlertDialog(
      onDismissRequest = onCancel,
      title   = { Text("Add Reminder") },
      text    = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          // image picker
          Box(
            Modifier
              .size(80.dp)
              .clip(CircleShape)
              .clickable { gallery.launch("image/*") }
          ) {
            if (imageUri != null) {
              Image(
                painter            = rememberAsyncImagePainter(imageUri),
                contentDescription = null,
                modifier           = Modifier.fillMaxSize()
              )
            } else {
              Surface(Modifier.fillMaxSize(), shape = CircleShape) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                  Text("Tap\nImage", textAlign = TextAlign.Center)
                }
              }
            }
          }

          OutlinedTextField(
            value       = title,
            onValueChange = { title = it },
            label       = { Text("Title") },
            singleLine  = true
          )

          OutlinedTextField(
            value         = description,
            onValueChange = { description = it },
            label         = { Text("Notes (optional)") }
          )

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { dp.show() }) { Text("Pick Date") }
            Button(onClick = { tp.show() }) { Text("Pick Time") }
          }

          Text("Notify at: ${Date(timestamp)}")
        }
      },
      confirmButton = {
        TextButton(
          onClick = {
            viewModel.addReminder(title, description, timestamp, imageUri)
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

    // 2️⃣ Block UI while saving
    if (isSaving) {
      Box(
        Modifier
          .matchParentSize()
          .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
      ) {
        CircularProgressIndicator()
      }
    }
  }
}
