// app/src/main/java/com/ghanshyam/chronosapp/ui/ChronosNavGraph.kt
package com.ghanshyam.chronosapp.ui

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import coil.compose.AsyncImage
import com.ghanshyam.chronosapp.ai.AIGreetingRepository
import com.ghanshyam.chronosapp.auth.AuthViewModel
import com.ghanshyam.chronosapp.reminder.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChronosNavGraph(
    authViewModel: AuthViewModel,
    onSignIn: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope ()
    val navController = rememberNavController()
    val user by authViewModel.currentUser.collectAsState()
    val context = LocalContext.current

    // AI dialog state
    var showAIDialog by remember { mutableStateOf(false) }
    var aiPrompt by remember { mutableStateOf("") }
    var loadingAI by remember { mutableStateOf(false) }
    var aiResult by remember { mutableStateOf<String?>(null) }

    val aiRepo = remember { AIGreetingRepository() }

    NavHost(
        navController = navController,
        startDestination = if (user == null) "login" else "list"
    ) {
        composable("login") {
            SignInScreen(onSignIn = onSignIn)
        }

        composable("list") {
            if (user == null) {
                LaunchedEffect(Unit) {
                    navController.navigate("login") { popUpTo("list") { inclusive = true } }
                }
            } else {
                val vm: ReminderViewModel = viewModel(
                    factory = ReminderViewModelFactory(user!!.uid)
                )
                var showAdd by remember { mutableStateOf(false) }
                var editId by remember { mutableStateOf<String?>(null) }
                var zoomImageUrl by remember { mutableStateOf<String?>(null) }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Chronos") },
                            navigationIcon = {
                                user!!.photoUrl?.let {
                                    AsyncImage(
                                        model = it, contentDescription = "Profile",
                                        modifier = Modifier
                                            .size(36.dp)
                                            .padding(4.dp)
                                            .background(
                                                MaterialTheme.colorScheme.surface,
                                                shape = CircleShape
                                            )
                                    )
                                }
                            },
                            actions = {
                                IconButton(onClick = { showAIDialog = true }) {
                                    Icon(Icons.Filled.SmartToy, "AI Greeting")
                                }
                                IconButton(onClick = {
                                    authViewModel.signOut()
                                    navController.navigate("login") {
                                        popUpTo("list") {
                                            inclusive = true
                                        }
                                    }
                                }) {
                                    Icon(Icons.Filled.Logout, "Sign out")
                                }
                            }
                        )
                    },
                    floatingActionButton = {
                        FloatingActionButton(onClick = { showAdd = true }) {
                            Icon(Icons.Filled.Add, contentDescription = "Add reminder")
                        }
                    },
                    floatingActionButtonPosition = FabPosition.Center
                ) { padding ->
                    ReminderListScreen(
                        viewModel = vm,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        onItemClick = { editId = it },
                        onImageClick = { /* no full-screen any more */ }
                    )
                }

                if (showAdd) {
                    AddReminderDialog(vm) { showAdd = false }
                }
                editId?.let { id ->
                    val toEdit = vm.reminders.value.first { it.id == id }
                    EditReminderDialog(toEdit, vm) { editId = null }
                }
            }
        }
    }

    // ── AI DIALOG ───────────────────────────────────────────
    if (showAIDialog) {
        AlertDialog(
            onDismissRequest = { showAIDialog = false },
            title = { Text("Generate AI Greeting") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = aiPrompt,
                        onValueChange = { aiPrompt = it },
                        label = { Text("Enter your prompt") },
                        placeholder = { Text("e.g. Write a short birthday wish for Prashant.") },
                        singleLine = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (loadingAI) {
                        LinearProgressIndicator(Modifier.fillMaxWidth())
                    }
                }
            },
            // at top of your @Composable

        confirmButton = {
            TextButton(
                onClick = {
                    loadingAI = true
                    coroutineScope.launch {
                        aiResult = try {
                            aiRepo.fetchGreeting(aiPrompt)
                        } catch (e: Exception) {
                            "❌ Failed: ${e.message}"
                        }
                        loadingAI = false
                    }
                },
                enabled = aiPrompt.isNotBlank() && !loadingAI
            ) {
                Text("Generate")
            }
        }

        ,
        dismissButton = {
            TextButton(onClick = { showAIDialog = false }) {
                Text("Cancel")
            }
        }
        )
    }

    // ── AI SHARE SHEET ───────────────────────────────────────
    aiResult?.let { result ->
        // share via Android share sheet
        LaunchedEffect(result) {
            val share = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, result)
            }
            context.startActivity(
                Intent.createChooser(share, "Share your AI message")
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            aiResult = null
            showAIDialog = false
        }
    }
}
