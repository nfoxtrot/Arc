package com.nfoskette.arc.ui

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.UUID

// A single chapter in the lesson plan (docs/DESIGN.md ยง4, Builder screen).
data class Chapter(
    val id: String = UUID.randomUUID().toString(),
    var title: String = ""
)

// Shared state for the route being built (Builder screen) and then displayed
// (Lesson View screen). This is deliberately simple in-memory state, hoisted at
// the app shell level and passed down — no ViewModel/persistence/backend wired
// up yet. That's a real architecture decision still open (see docs/DESIGN.md
// ยง6 open items on locking the framework), not something decided here.
@Stable
class RouteState {
    var startTopic by mutableStateOf("")
    var endTopic by mutableStateOf("")
    val chapters = mutableStateListOf(Chapter())
    var isLocked by mutableStateOf(false)

    val canAddChapter: Boolean
        get() = chapters.size < 3

    val canConfirm: Boolean
        get() = startTopic.isNotBlank() &&
            endTopic.isNotBlank() &&
            chapters.isNotEmpty() &&
            chapters.all { it.title.isNotBlank() }

    fun addChapter() {
        if (canAddChapter) chapters.add(Chapter())
    }

    fun removeChapter(id: String) {
        if (chapters.size > 1) chapters.removeAll { it.id == id }
    }

    fun moveChapter(fromIndex: Int, toIndex: Int) {
        if (toIndex in chapters.indices) {
            val item = chapters.removeAt(fromIndex)
            chapters.add(toIndex, item)
        }
    }
}
