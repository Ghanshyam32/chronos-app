package com.ghanshyam.chronosapp.ai

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ghanshyam.chronosapp.ai.AiViewModel

@Composable
fun ShareAiMessageButton(
  prompt: String,
  aiViewModel: AiViewModel = viewModel()
) {
  val greeting by aiViewModel.greeting.collectAsState()
  val loading by aiViewModel.loading.collectAsState()
  val context = LocalContext.current

  Button(
    onClick = { aiViewModel.fetchGreeting(prompt) },
    enabled = !loading,
    modifier = Modifier.fillMaxWidth()
  ) {
    if (loading) {
      CircularProgressIndicator(
        modifier = Modifier.size(18.dp),
        strokeWidth = 2.dp
      )
      Spacer(Modifier.width(8.dp))
      Text("Generating...")
    } else {
      Text("Share AI Message")
    }
  }

  LaunchedEffect(greeting) {
    greeting?.let { text ->
      val intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
      }
      context.startActivity(Intent.createChooser(intent, "Share via"))
      aiViewModel.clearGreeting()
    }
  }
}
