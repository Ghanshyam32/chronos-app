// app/src/main/java/com/ghanshyam/chronosapp/MainActivity.kt
package com.ghanshyam.chronosapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.ghanshyam.chronosapp.auth.AuthViewModel
import com.ghanshyam.chronosapp.ui.ChronosNavGraph
import com.ghanshyam.chronosapp.ui.theme.ChronosTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1️⃣ Configure the Google Sign‑In options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleClient = GoogleSignIn.getClient(this, gso)

        // 2️⃣ Launcher for the Google Sign‑In intent
        val signInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val acct = task.getResult(Exception::class.java)!!
                authViewModel.firebaseAuthWithGoogle(acct.idToken!!)
            } catch (_: Exception) { /* handle sign‑in failure if you like */ }
        }

        setContent {
            ChronosTheme {
                // 3️⃣ Pass the launcher into your NavGraph
                ChronosNavGraph(
                    authViewModel = authViewModel,
                    onSignIn      = { signInLauncher.launch(googleClient.signInIntent) }
                )
            }
        }
    }
}
