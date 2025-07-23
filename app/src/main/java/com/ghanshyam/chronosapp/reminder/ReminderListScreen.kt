package com.ghanshyam.chronosapp.reminder

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReminderListScreen(
    viewModel: ReminderViewModel,
    modifier: Modifier = Modifier,
    onItemClick: (String) -> Unit,
    onImageClick: (String) -> Unit
) {
    val reminders by viewModel.reminders.collectAsState()

    if (reminders.isEmpty()) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No reminders yet", style = MaterialTheme.typography.bodyMedium)
        }
    } else {
        LazyColumn(
            modifier = modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(reminders) { r ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(r.id) },
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(r.title, style = MaterialTheme.typography.titleMedium)
                            if (r.description.isNotBlank()) {
                                Text(r.description, style = MaterialTheme.typography.bodySmall)
                            }
                            val fmt = remember {
                                SimpleDateFormat(
                                    "MMM dd, yyyy h:mm a",
                                    Locale.getDefault()
                                )
                            }
                            Text(
                                fmt.format(Date(r.timestamp)),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        r.imageUrl?.let { url ->
                            AsyncImage(
                                model = url,
                                contentDescription = "Reminder Image",
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        }
                        IconButton(onClick = { viewModel.deleteReminder(r.id) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete reminder",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}
