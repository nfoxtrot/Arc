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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
// Chapters render as a horizontal, snap-scrolling waypoint timeline rather than
// the wireframe spec's vertical list — a deliberate change (2026-08-26) that
// leans into the ARC flight-path/waypoint visual language already used in
// Lesson View (WP-00 codes, mono font), not something the original spec asked
// for. Scoped to just the chapters — Start/End topic stay as plain fields
// above, since that's what was actually described as "list view."
//
// Known simplification vs. the wireframe spec, still flagged: the spec calls
// for drag-and-drop chapter reordering (⠿ handle). This uses left/right
// buttons instead — real drag gestures are a follow-up polish item.
// "✨ Suggest chapters for me" is a stub (no AI backend wired up yet).
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BuilderScreen(
    routeState: RouteState,
    onConfirmed: () -> Unit
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

            OutlinedTextField(
                value = routeState.startTopic,
                onValueChange = { routeState.startTopic = it },
                label = { Text("Start topic") },
                enabled = !routeState.isLocked,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = routeState.endTopic,
                onValueChange = { routeState.endTopic = it },
                label = { Text("End topic") },
                enabled = !routeState.isLocked,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Chapters", style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = { /* AI suggestion backend not wired up yet */ }, enabled = !routeState.isLocked) {
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
            itemsIndexed(routeState.chapters, key = { _, chapter -> chapter.id }) { index, chapter ->
                WaypointCard(
                    index = index,
                    chapter = chapter,
                    isLocked = routeState.isLocked,
                    canMoveLeft = index > 0,
                    canMoveRight = index < routeState.chapters.lastIndex,
                    canRemove = routeState.chapters.size > 1,
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
                OutlinedButton(
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

        Card(modifier = Modifier.width(WaypointCardWidth)) {
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
                OutlinedTextField(
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
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
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
