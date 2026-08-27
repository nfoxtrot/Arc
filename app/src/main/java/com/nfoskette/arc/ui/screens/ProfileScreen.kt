package com.nfoskette.arc.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nfoskette.arc.ui.UserState

// Profile/Account screen — not part of the original wireframe spec (docs/DESIGN.md
// flagged this as "not yet designed" since project start). Built without a spec to
// follow, so the content/fields here are a reasonable first pass, not a confirmed
// design:
//   - Avatar initial, editable preferred name + email, sign out.
//   - No photo upload, no password change, no account deletion — kept to what the
//     existing signup flow already collects, since there's no backend to support
//     more than that yet.
//   - The dark/light toggle deliberately stays only in the top bar, not duplicated
//     here.
// Edits to name/email are live local state only (matches the RouteState/UserState
// pattern used elsewhere) — nothing is actually persisted or sent anywhere.
@Composable
fun ProfileScreen(
    userState: UserState,
    onRequestSignIn: () -> Unit
) {
    if (!userState.isSignedIn) {
        SignedOutContent(onRequestSignIn)
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val initial = userState.preferredName.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?"
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                initial,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            userState.preferredName.ifBlank { "Your name" },
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            userState.email,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(32.dp))
        HorizontalDivider()
        Spacer(Modifier.height(24.dp))

        TextField(
            value = userState.preferredName,
            onValueChange = { userState.preferredName = it },
            label = { Text("Preferred name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(12.dp))
        TextField(
            value = userState.email,
            onValueChange = { userState.email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(32.dp))

        FilledTonalButton(
            onClick = { userState.signOut() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign out")
        }
    }
}

@Composable
private fun SignedOutContent(onRequestSignIn: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "You're not signed in",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Sign in to save your routes and see your profile here.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = onRequestSignIn, modifier = Modifier.fillMaxWidth()) {
            Text("Sign in / Create account")
        }
    }
}
