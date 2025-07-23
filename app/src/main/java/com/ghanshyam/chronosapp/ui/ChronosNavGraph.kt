// app/src/main/java/com/ghanshyam/chronosapp/ui/ChronosNavGraph.kt
package com.ghanshyam.chronosapp.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.ghanshyam.chronosapp.auth.AuthViewModel
import com.ghanshyam.chronosapp.reminder.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChronosNavGraph(
  authViewModel: AuthViewModel,
  onSignIn: () -> Unit             // ← NEW
) {
  val navController = rememberNavController()
  val user by authViewModel.currentUser.collectAsState()

  NavHost(
    navController    = navController,
    startDestination = if (user == null) "login" else "list"
  ) {
    // --- LOGIN SCREEN ---
    composable("login") {
      SignInScreen(
        onSignIn = onSignIn       // ← CALL the launcher from MainActivity
      )
    }

    // --- LIST + FAB + DIALOGS + ZOOM ---
    composable("list") {
      // If user becomes null, bounce back to login
      if (user == null) {
        LaunchedEffect(Unit) {
          navController.navigate("login") {
            popUpTo("list") { inclusive = true }
          }
        }
        Box(Modifier.fillMaxSize()) {} // placeholder
      } else {
        val vm: ReminderViewModel = viewModel(
          factory = ReminderViewModelFactory(user!!.uid)
        )
        var showAdd     by remember { mutableStateOf(false) }
        var editId      by remember { mutableStateOf<String?>(null) }
        var zoomUrl     by remember { mutableStateOf<String?>(null) }

        Scaffold(
          topBar = {
            TopAppBar(
              title   = { Text("Chronos") },
              actions = {
                IconButton(onClick = {
                  authViewModel.signOut()
                  navController.navigate("login") {
                    popUpTo("list") { inclusive = true }
                  }
                }) {
                  Icon(Icons.Default.Logout, "Sign out")
                }
              }
            )
          },
          floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }) {
              Icon(Icons.Default.Add, "Add reminder")
            }
          },
          floatingActionButtonPosition = FabPosition.Center
        ) { padding ->
          ReminderListScreen(
            viewModel    = vm,
            modifier     = Modifier
              .fillMaxSize()
              .padding(padding),
            onItemClick  = { id  -> editId  = id  },
            onImageClick = { url -> zoomUrl = url }
          )
        }

        if (showAdd) {
          AddReminderDialog(
            onSave   = { t,d,ts,uri -> vm.addReminder(t,d,ts,uri); showAdd = false },
            onCancel = { showAdd = false }
          )
        }
        editId?.let { id ->
          val toEdit = vm.reminders.value.first { it.id == id }
          EditReminderDialog(
            reminder  = toEdit,
            viewModel = vm,
            onDismiss = { editId = null }
          )
        }
        zoomUrl?.let { url ->
          BackHandler { zoomUrl = null }
          Box(
            Modifier
              .fillMaxSize()
              .background(Color.Black.copy(alpha = 0.8f)),
            contentAlignment = Alignment.Center
          ) {
            AsyncImage(
              model              = url,
              contentDescription = "Full‑screen image",
              modifier           = Modifier
                .fillMaxWidth()
                .padding(16.dp)
            )
          }
        }
      }
    }

    // Optional fallback edit route…
    composable(
      "edit/{reminderId}",
      arguments = listOf(navArgument("reminderId") {
        type = NavType.StringType
      })
    ) { back ->
      val id = back.arguments!!.getString("reminderId")!!
      val vm: ReminderViewModel = viewModel(
        factory = ReminderViewModelFactory(user!!.uid)
      )
      EditReminderScreen(
        reminderId = id,
        viewModel  = vm,
        onDone     = { navController.popBackStack() }
      )
    }
  }
}
