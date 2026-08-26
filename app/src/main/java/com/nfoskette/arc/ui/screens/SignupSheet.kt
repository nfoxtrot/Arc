package com.nfoskette.arc.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.nfoskette.arc.ui.UserState

// One-time signup bottom sheet (docs/DESIGN.md ยง4). NOT a permanent screen/tab —
// shown automatically the first time the app opens while signed out.
// "Continue with Google" is a stub here — real OAuth wiring is backend/auth work
// not yet started; it deliberately does NOT set isSignedIn, since faking a
// successful OAuth sign-in would misrepresent what's actually implemented.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupSheet(userState: UserState, onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState()
    var preferredName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val passwordsMismatch = confirmPassword.isNotEmpty() && password != confirmPassword

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Welcome to ARC", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(4.dp))
            Text(
                "Create an account to save and export your routes.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = preferredName,
                onValueChange = { preferredName = it },
                label = { Text("Preferred name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = passwordsMismatch,
                supportingText = {
                    if (passwordsMismatch) Text("Passwords don't match")
                },
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(Modifier.height(20.dp))

            val canSubmit = preferredName.isNotBlank() && email.isNotBlank() &&
                password.isNotEmpty() && password == confirmPassword

            Button(
                onClick = {
                    userState.preferredName = preferredName
                    userState.email = email
                    userState.isSignedIn = true
                    onDismiss()
                },
                enabled = canSubmit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (preferredName.isNotBlank()) "Let's get started, $preferredName"
                    else "Let's get started"
                )
            }

            Spacer(Modifier.height(8.dp))

            FilledTonalButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue with Google")
            }

            Spacer(Modifier.height(4.dp))

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Skip for now")
            }
        }
    }
}
