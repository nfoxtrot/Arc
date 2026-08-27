package com.nfoskette.arc.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.nfoskette.arc.ui.Chapter
import com.nfoskette.arc.ui.RouteState
import com.nfoskette.arc.ui.theme.ArcMonoLabel

// Builder ("Plan a route") screen (docs/DESIGN.md ยง4).
//
// As of 2026-08-26, Start topic, chapters, and End topic all render as ONE
// horizontal, snap-scrolling waypoint timeline — Start and End are the fixed
// endpoints, chapters are the waypoints in between. This replaced an earlier
// version where Start/End were plain fields above a separate chapters row;
// splitting them broke the "one journey" mental model the flight-path
// metaphor is built on, so they were merged on request. Not spec-driven —
// audited and changed directly.
//
// Known simplifications, still flagged:
// - Reordering uses left/right chevrons, not real drag-and-drop.
// - The disabled "Confirm lesson plan" button doesn't explain what's missing
//   (no inline validation messaging) — another flagged audit finding, not
//   fixed here.
//
// Fixed 2026-08-26 (user report: "when I click suggest chapters for me, I
// don't see anything load or change"): the button's onClick was a true no-op
// with zero visible feedback - indistinguishable from broken. It's still a
// stub (no AI backend wired up), but now tapping it surfaces a Snackbar
// saying so via onSuggestChapters, instead of doing nothing. Also closed the
// companion audit finding in the same pass: it was enabled before Start/End
// were filled in, which never made sense for what it will eventually do -
// now gated on both being non-blank (in addition to !isLocked).
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BuilderScreen(
    routeState: RouteState,
    onConfirmed: () -> Unit,
    onSuggestChapters: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 24.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text("Plan a route", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Your route", style = MaterialTheme.typography.titleMedium)
                TextButton(
                    onClick = onSuggestChapters,
                    enabled = !routeState.isLocked &&
                        routeState.startTopic.isNotBlank() &&
                        routeState.endTopic.isNotBlank()
                ) {
                    Text("✨ Suggest chapters for me")
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        val listState = rememberLazyListState()
        LazyRow(
            state = listState,
            flingBehavior = rememberSnapFlingBehavior(listState),
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                EndpointCard(
                    label = "START",
                    value = routeState.startTopic,
                    onValueChange = { routeState.startTopic = it },
                    isLocked = routeState.isLocked
                )
            }

            itemsIndexed(routeState.chapters, key = { _, chapter -> chapter.id }) { index, chapter ->
                WaypointCard(
                    index = index,
                    chapter = chapter,
                    isLocked = routeState.isLocked,
                    canMoveLeft = index > 0,
                    canMoveRight = index < routeState.chapters.lastIndex,
                    canRemove = true, // chapters are optional now, can go to zero
                    onTitleChange = { chapter.title = it },
                    onMoveLeft = { routeState.moveChapter(index, index - 1) },
                    onMoveRight = { routeState.moveChapter(index, index + 1) },
                    onRemove = { routeState.removeChapter(chapter.id) }
                )
            }

            if (routeState.canAddChapter && !routeState.isLocked) {
                item {
                    AddWaypointCard(onClick = { routeState.addChapter() })
                }
            }

            item {
                EndpointCard(
                    label = "END",
                    value = routeState.endTopic,
                    onValueChange = { routeState.endTopic = it },
                    isLocked = routeState.isLocked
                )
            }
        }

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Spacer(Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))

            if (routeState.isLocked) {
                Button(onClick = onConfirmed, modifier = Modifier.fillMaxWidth()) {
                    Text("View lesson")
                }
                Spacer(Modifier.height(8.dp))
                FilledTonalButton(
                    onClick = { routeState.isLocked = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Edit route")
                }
            } else {
                Button(
                    onClick = { routeState.isLocked = true },
                    enabled = routeState.canConfirm,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Confirm lesson plan")
                }
            }
        }
    }
}

private val WaypointCardWidth = 240.dp

@Composable
private fun WaypointConnector(filled: Boolean) {
    Row(
        modifier = Modifier
            .width(WaypointCardWidth)
            .height(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .weight(1f)
                .height(2.dp)
                .background(MaterialTheme.colorScheme.outline)
        )
        if (filled) {
            Box(
                Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        } else {
            // Hollow ring for the "not yet placed" add-chapter slot — a solid dot
            // in the same color as the line either blends in invisibly or needs
            // a second color, so this reads as "open waypoint" instead.
            Box(
                Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape)
            )
        }
        Box(
            Modifier
                .weight(1f)
                .height(2.dp)
                .background(MaterialTheme.colorScheme.outline)
        )
    }
}

// Start/End of the route — fixed endpoints on the timeline, not reorderable
// or removable list items like chapters, so they get a simpler card (just the
// input, no chevrons/remove button) and a text label ("START"/"END") instead
// of a WP-code.
@Composable
private fun EndpointCard(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isLocked: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        WaypointConnector(filled = true)
        Spacer(Modifier.height(6.dp))
        Text(text = label, style = ArcMonoLabel, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(8.dp))

        Card(
            modifier = Modifier.width(WaypointCardWidth),
            shape = RoundedCornerShape(20.dp) // Material You container roundness (2026-08-26), was the default 12.dp
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = { Text(if (label == "START") "Start topic" else "End topic") },
                    enabled = !isLocked,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }
    }
}

@Composable
private fun WaypointCard(
    index: Int,
    chapter: Chapter,
    isLocked: Boolean,
    canMoveLeft: Boolean,
    canMoveRight: Boolean,
    canRemove: Boolean,
    onTitleChange: (String) -> Unit,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    onRemove: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        WaypointConnector(filled = true)
        Spacer(Modifier.height(6.dp))
        Text(
            text = "WP-%02d".format(index),
            style = ArcMonoLabel,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))

        Card(
            modifier = Modifier.width(WaypointCardWidth),
            shape = RoundedCornerShape(20.dp) // Material You container roundness (2026-08-26), was the default 12.dp
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        IconButton(
                            onClick = onMoveLeft,
                            enabled = !isLocked && canMoveLeft,
                            modifier = Modifier.size(32.dp)
                        ) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Move earlier") }
                        IconButton(
                            onClick = onMoveRight,
                            enabled = !isLocked && canMoveRight,
                            modifier = Modifier.size(32.dp)
                        ) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Move later") }
                    }
                    IconButton(
                        onClick = onRemove,
                        enabled = !isLocked && canRemove,
                        modifier = Modifier.size(32.dp)
                    ) { Icon(Icons.Filled.Close, contentDescription = "Remove chapter") }
                }
                Spacer(Modifier.height(4.dp))
                TextField(
                    value = chapter.title,
                    onValueChange = onTitleChange,
                    label = { Text("Chapter ${index + 1}") },
                    enabled = !isLocked,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }
    }
}

@Composable
private fun AddWaypointCard(onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        WaypointConnector(filled = false)
        Spacer(Modifier.height(6.dp))
        Spacer(Modifier.height(20.dp)) // aligns with the WP-code text height on real cards
        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .width(WaypointCardWidth)
                .height(96.dp)
                // 20.dp to match the Material You container roundness applied to the
                // real waypoint/endpoint Cards (2026-08-26), was 12.dp
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            TextButton(onClick = onClick) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("Add chapter")
            }
        }
    }
}
