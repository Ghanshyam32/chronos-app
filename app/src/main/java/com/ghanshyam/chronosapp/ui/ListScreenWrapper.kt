package com.ghanshyam.chronosapp.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ghanshyam.chronosapp.reminder.ReminderListScreen
import com.ghanshyam.chronosapp.reminder.ReminderViewModel


@Composable
fun ListScreenWrapper(
  viewModel: ReminderViewModel,
  onAdd: () -> Unit,
  onEdit: (String) -> Unit,
  onImageClick: (String) -> Unit
) {
  Scaffold(
    floatingActionButton = {
      FloatingActionButton(onClick = onAdd) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
      }
    },
    floatingActionButtonPosition = FabPosition.Center
  ) { contentPadding: PaddingValues ->
    ReminderListScreen(
      viewModel     = viewModel,
      modifier      = Modifier.padding(contentPadding),
      onItemClick   = onEdit,
      onImageClick  = onImageClick
    )
  }
}
