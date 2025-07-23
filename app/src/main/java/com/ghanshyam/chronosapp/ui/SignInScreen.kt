package com.ghanshyam.chronosapp.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ghanshyam.chronosapp.R

@Composable
fun SignInScreen(
  onSignIn: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier.fillMaxSize(),
    color = MaterialTheme.colorScheme.background
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Image(
        painter = painterResource(id = R.drawable.ic_signin_illustration),
        contentDescription = "Welcome illustration",
        modifier = Modifier
          .size(180.dp)
          .clip(RoundedCornerShape(16.dp))
      )

      Spacer(Modifier.height(32.dp))

      Text(
        text = "Welcome to Chronos",
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
      )
      Spacer(Modifier.height(8.dp))
      Text(
        text = "Your personal reminder companion",
        style = MaterialTheme.typography.bodyMedium.copy(
          fontSize = 16.sp,
          color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
      )

      Spacer(Modifier.height(48.dp))

      OutlinedButton(
        onClick = onSignIn,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        colors = ButtonDefaults.outlinedButtonColors(
          contentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Image(
            painter = painterResource(id = R.drawable.ic_google_logo),
            contentDescription = "Google logo",
            modifier = Modifier.size(24.dp)
          )
          Spacer(Modifier.width(12.dp))
          Text(
            text = "Sign in with Google",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium)
          )
        }
      }
    }
  }
}
