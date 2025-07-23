package com.ghanshyam.chronosapp.reminder

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ghanshyam.chronosapp.data.Reminder
import java.util.*

@Composable
fun EditReminderDialog(
  reminder: Reminder,
  viewModel: ReminderViewModel,
  onDismiss: () -> Unit
) {
  var title by remember { mutableStateOf(reminder.title) }
  var description by remember { mutableStateOf(reminder.description) }
  var timestamp by remember { mutableStateOf(reminder.timestamp) }

  // Date/time pickers as before...
  val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
  val datePicker = DatePickerDialog(
    LocalContext.current,
    { _, y, m, d -> calendar.set(y, m, d); timestamp = calendar.timeInMillis },
    calendar.get(Calendar.YEAR),
    calendar.get(Calendar.MONTH),
    calendar.get(Calendar.DAY_OF_MONTH)
  )
  val timePicker = TimePickerDialog(
    LocalContext.current,
    { _, h, min -> calendar.set(Calendar.HOUR_OF_DAY, h); calendar.set(Calendar.MINUTE, min); timestamp = calendar.timeInMillis },
    calendar.get(Calendar.HOUR_OF_DAY),
    calendar.get(Calendar.MINUTE),
    false
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    title   = { Text("Edit Reminder") },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(title, { title = it }, label = { Text("Title") })
        OutlinedTextField(description, { description = it }, label = { Text("Notes") })

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Button(onClick = { datePicker.show() }) { Text("Date") }
          Button(onClick = { timePicker.show() }) { Text("Time") }
        }
      }
    },
    confirmButton = {
      TextButton(onClick = {
        viewModel.updateReminder(
          reminder.copy(title = title, description = description, timestamp = timestamp)
        )
        onDismiss()
      }) { Text("Save") }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel") }
    }
  )
}
