package com.ghanshyam.chronosapp.reminder

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import java.util.*

@Composable
fun AddReminderScreen(
  viewModel: ReminderViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var title by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var timestamp by remember { mutableStateOf(System.currentTimeMillis()) }
  var imageUri by remember { mutableStateOf<Uri?>(null) }
  val isSaving by viewModel.isSaving.collectAsState()

  // Robust Notification permission handling
  val permLauncher = rememberLauncherForActivityResult(RequestPermission()) { granted ->
    if (!granted) {
      Toast.makeText(
        context,
        "Enable notification permission in settings for reminder alerts.",
        Toast.LENGTH_LONG
      ).show()
    }
  }
  LaunchedEffect(Unit) {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
      permLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
  }

  val calendar = remember { Calendar.getInstance() }
  val datePicker = DatePickerDialog(
    context,
    { _, y, m, d ->
      calendar.set(Calendar.YEAR, y)
      calendar.set(Calendar.MONTH, m)
      calendar.set(Calendar.DAY_OF_MONTH, d)
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

  val galleryLauncher = rememberLauncherForActivityResult(GetContent()) { uri ->
    imageUri = uri
  }

  Box(modifier = modifier) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Image picker & preview
      Box(
        modifier = Modifier
          .size(100.dp)
          .clip(CircleShape)
          .clickable { galleryLauncher.launch("image/*") }
      ) {
        if (imageUri != null) {
          Image(
            painter = rememberAsyncImagePainter(imageUri),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
          )
        } else {
          Surface(
            shape = CircleShape,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxSize()
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = Icons.Default.Image,
                contentDescription = null,
                tint = Color.Gray
              )
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
        text = "Will notify at: ${Date(timestamp)}",
        style = MaterialTheme.typography.bodySmall
      )

      Spacer(Modifier.height(24.dp))
      Button(
        onClick = {
          NotificationHelper.scheduleAlarm(
            context = context,
            timeMillis = timestamp,
            title = title,
            description = description
          )
          viewModel.addReminder(title, description, timestamp, imageUri)
        },
        enabled = title.isNotBlank(),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text("Save Reminder")
      }
    }
    if (isSaving) {
      Box(
        Modifier
          .matchParentSize()
          .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
      ) {
        CircularProgressIndicator()
      }
    }
  }
}
