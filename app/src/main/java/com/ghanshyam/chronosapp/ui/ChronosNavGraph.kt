package com.ghanshyam.chronosapp.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    onSignIn: () -> Unit
) {
    val navController = rememberNavController()
    val user by authViewModel.currentUser.collectAsState()

    NavHost(
        navController = navController,
        startDestination = if (user == null) "login" else "list"
    ) {

        // LOGIN SCREEN
        composable("login") {
            SignInScreen(onSignIn = onSignIn)
        }

        // MAIN LIST, ADD, EDIT SCREENS
        composable("list") {
            if (user == null) {
                // If logged out, bounce
                LaunchedEffect(Unit) {
                    navController.navigate("login") {
                        popUpTo("list") { inclusive = true }
                    }
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
                            navigationIcon = {
                                user?.photoUrl?.let { url ->
                                    AsyncImage(
                                        model = url,
                                        "",
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .padding(8.dp)
                                    )
                                }
                            },
                            title = { Text("Hello, ${user?.displayName ?: user?.email ?: "User"}!") },
                            actions = {
                                IconButton(
                                    onClick = {
                                        authViewModel.signOut()
                                        navController.navigate("login") {
                                            popUpTo("list") { inclusive = true }
                                        }
                                    }
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.Logout,
                                        contentDescription = "Logout"
                                    )
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
                        onItemClick = { id -> editId = id },
                        onImageClick = { url -> zoomImageUrl = url }
                    )
                    // Add dialog
                    if (showAdd) {
                        AddReminderDialog(viewModel = vm, onCancel = { showAdd = false })
                    }
                    // Edit dialog
                    editId?.let { id ->
                        val toEdit = vm.reminders.value.firstOrNull { it.id == id } ?: return@let
                        EditReminderDialog(
                            reminder = toEdit,
                            viewModel = vm,
                            onDismiss = { editId = null }
                        )
                    }
                    // Enlarged image
                    zoomImageUrl?.let { url ->
                        BackHandler { zoomImageUrl = null }
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.8f)),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = url,
                                contentDescription = "Full image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Optional: Dedicated Edit Route
        composable(
            "edit/{reminderId}",
            arguments = listOf(navArgument("reminderId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val rid = backStackEntry.arguments!!.getString("reminderId")!!
            if (user != null) {
                val vm: ReminderViewModel = viewModel(
                    factory = ReminderViewModelFactory(user!!.uid)
                )
                EditReminderScreen(
                    reminderId = rid,
                    viewModel = vm,
                    onDone = { navController.popBackStack() }
                )
            }
        }
    }
}
