package com.ghanshyam.chronosapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.ghanshyam.chronosapp.auth.AuthViewModel
import com.ghanshyam.chronosapp.reminder.*
import com.ghanshyam.chronosapp.ui.ChronosNavGraph
import com.ghanshyam.chronosapp.ui.SignInScreen
import com.ghanshyam.chronosapp.ui.theme.ChronosTheme

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleClient = GoogleSignIn.getClient(this, gso)
        val signInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val acct = task.getResult(ApiException::class.java)!!
                authViewModel.firebaseAuthWithGoogle(acct.idToken!!)
            } catch (_: ApiException) {
            }
        }

        setContent {
            ChronosTheme {
                ChronosNavGraph(authViewModel = authViewModel)
                val user by authViewModel.currentUser.collectAsState()
                val context = LocalContext.current

                var selectedImageUrl by remember { mutableStateOf<String?>(null) }

                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    if (user == null) {
                        SignInScreen(
                            onSignIn = { signInLauncher.launch(googleClient.signInIntent) },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        val displayName = user!!.displayName ?: user!!.email ?: "User"
                        val photoUrl = user!!.photoUrl
                        val vm: ReminderViewModel = viewModel(
                            factory = ReminderViewModelFactory(user!!.uid)
                        )

                        Scaffold(
                            topBar = {
                                TopAppBar(
                                    navigationIcon = {
                                        photoUrl?.let {
                                            AsyncImage(
                                                model = it,
                                                contentDescription = "Profile",
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .clip(CircleShape)
                                                    .padding(8.dp)
                                            )
                                        }
                                    },
                                    title = { Text("Hello, $displayName!") },
                                    actions = {
                                        IconButton(onClick = { authViewModel.signOut() }) {
                                            Icon(
                                                Icons.Default.Logout,
                                                contentDescription = "Sign out"
                                            )
                                        }
                                    }
                                )
                            },
                            floatingActionButton = {
                                FloatingActionButton(onClick = {}) {
                                    Icon(Icons.Default.Add, contentDescription = "Add reminder")
                                }
                            },
                            floatingActionButtonPosition = FabPosition.Center
                        ) { padding ->
                            ReminderListScreen(
                                viewModel = vm,
                                modifier = Modifier
                                    .padding(padding)
                                    .fillMaxSize(),
                                onItemClick = {},
                                onImageClick = { url -> selectedImageUrl = url }
                            )
                        }
                    }
                }

                selectedImageUrl?.let { url ->
                    BackHandler { selectedImageUrl = null }

                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.8f)),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = url,
                            contentDescription = "Full screen image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}
