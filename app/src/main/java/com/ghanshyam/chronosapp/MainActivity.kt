package com.ghanshyam.chronosapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.ghanshyam.chronosapp.auth.AuthViewModel
import com.ghanshyam.chronosapp.ui.ChronosNavGraph
import com.ghanshyam.chronosapp.ui.theme.ChronosTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import androidx.activity.result.contract.ActivityResultContracts
import com.ghanshyam.chronosapp.R

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    companion object {
        private const val REQ_NOTIF = 1001
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    REQ_NOTIF
                )
            }
        }
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
                val acct = task.getResult(Exception::class.java)!!
                authViewModel.firebaseAuthWithGoogle(acct.idToken!!)
            } catch (_: Exception) {
            }
        }

        setContent {
            ChronosTheme {
                ChronosNavGraph(
                    authViewModel = authViewModel,
                    onSignIn = { signInLauncher.launch(googleClient.signInIntent) }
                )
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "chronos_reminders",
                "Reminder Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Notifications for your Chronos reminders" }
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }
}
