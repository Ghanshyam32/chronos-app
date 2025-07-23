package com.ghanshyam.chronosapp.reminder

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun EditReminderScreen(
    reminderId: String,
    viewModel: ReminderViewModel,
    onDone: () -> Unit
) {
    val reminders by viewModel.reminders.collectAsState()
    val r = reminders.find { it.id == reminderId } ?: return

    var title by remember { mutableStateOf(r.title) }
    var description by remember { mutableStateOf(r.description) }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Notes") })
        Spacer(Modifier.height(24.dp))
        Row {
            Button(onClick = {
                viewModel.updateReminder(r.copy(title = title, description = description))
                onDone()
            }) { Text("Save") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                viewModel.deleteReminder(reminderId)
                onDone()
            }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                Text("Delete")
            }
        }
    }
}
