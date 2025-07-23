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
import kotlinx.coroutines.launch
import com.ghanshyam.chronosapp.auth.AuthViewModel
import com.ghanshyam.chronosapp.reminder.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChronosNavGraph(authViewModel: AuthViewModel) {
  val navController = rememberNavController()
  val user by authViewModel.currentUser.collectAsState()

  NavHost(
    navController    = navController,
    startDestination = if (user == null) "login" else "list"
  ) {
    // --- LOGIN ---
    composable("login") {
      SignInScreen(onSignIn = { navController.navigate("list") })
    }

    // --- LIST + FAB + DIALOGS + ZOOM ---
    composable("list") {
      // if user is no longer signed in, bounce back
      if (user == null) {
        LaunchedEffect(Unit) {
          navController.navigate("login") {
            popUpTo("list") { inclusive = true }
          }
        }
        // render an empty placeholder while redirecting
        Box(Modifier.fillMaxSize()) {}
      } else {
        // safe to do user!! now
        val vm: ReminderViewModel = viewModel(
          factory = ReminderViewModelFactory(user!!.uid)
        )

        var showAdd     by remember { mutableStateOf(false) }
        var editId      by remember { mutableStateOf<String?>(null) }
        var fullImageUrl by remember { mutableStateOf<String?>(null) }

        Scaffold(
          topBar = {
            TopAppBar(
              title = { Text("Chronos") },
              actions = {
                IconButton(onClick = {
                  // sign out and navigate back to login
                  authViewModel.signOut()
                  navController.navigate("login") {
                    popUpTo("list") { inclusive = true }
                  }
                }) {
                  Icon(Icons.Default.Logout, contentDescription = "Sign out")
                }
              }
            )
          },
          floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }) {
              Icon(Icons.Default.Add, contentDescription = "Add Reminder")
            }
          },
          floatingActionButtonPosition = FabPosition.Center
        ) { padding ->
          ReminderListScreen(
            viewModel    = vm,
            modifier     = Modifier
              .padding(padding)
              .fillMaxSize(),
            onItemClick  = { id  -> editId      = id      },
            onImageClick = { url -> fullImageUrl = url     }
          )
        }

        // 1️⃣ Add dialog
        if (showAdd) {
          AddReminderDialog(
            onSave   = { t, d, ts, uri ->
              vm.addReminder(t, d, ts, uri)
              showAdd = false
            },
            onCancel = { showAdd = false }
          )
        }

        // 2️⃣ Edit dialog
        editId?.let { id ->
          val toEdit = vm.reminders.value.first { it.id == id }
          EditReminderDialog(
            reminder  = toEdit,
            viewModel = vm,
            onDismiss = { editId = null }
          )
        }

        // 3️⃣ Full‑screen zoom
        fullImageUrl?.let { url ->
          BackHandler { fullImageUrl = null }
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

    // --- (Optional) Separate edit route ---
    composable(
      "edit/{reminderId}",
      arguments = listOf(navArgument("reminderId") {
        type = NavType.StringType
      })
    ) { back ->
      val rid = back.arguments!!.getString("reminderId")!!
      val vm: ReminderViewModel = viewModel(
        factory = ReminderViewModelFactory(user!!.uid)
      )
      EditReminderScreen(
        reminderId = rid,
        viewModel  = vm,
        onDone     = { navController.popBackStack() }
      )
    }
  }
}
