package com.nfoskette.arc.ui

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

// Local, in-memory identity state — no backend/persistence exists yet (see
// docs/DESIGN.md ยง6). Before this, SignupSheet captured preferred name/email
// into fields that were discarded on dismiss; nothing else in the app could
// read them. This is the first place that data actually goes, so the Profile
// screen has something real to show. Still not persisted across app restarts.
@Stable
class UserState {
    var isSignedIn by mutableStateOf(false)
    var preferredName by mutableStateOf("")
    var email by mutableStateOf("")

    fun signOut() {
        isSignedIn = false
        preferredName = ""
        email = ""
    }
}
