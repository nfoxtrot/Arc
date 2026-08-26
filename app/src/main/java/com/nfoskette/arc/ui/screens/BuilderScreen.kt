package com.nfoskette.arc.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.ui.unit.dp
import com.nfoskette.arc.ui.RouteState

// Builder ("Plan a route") screen (docs/DESIGN.md ยง4).
//
// Known simplification vs. the wireframe spec, flagged rather than silently done:
// the spec calls for drag-and-drop chapter reordering (⠿ handle). This uses
// up/down buttons instead for tonight's pass — real drag gestures are a follow-up
// polish item, not implemented here.
// "✨ Suggest chapters for me" is a stub (no AI backend wired up yet).
@Composable
fun BuilderScreen(
    routeState: RouteState,
    onConfirmed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
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

        Spacer(Modifier.height(8.dp))

        routeState.chapters.forEachIndexed { index, chapter ->
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        IconButton(
                            onClick = { routeState.moveChapter(index, index - 1) },
                            enabled = !routeState.isLocked && index > 0
                        ) { Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "Move up") }
                        IconButton(
                            onClick = { routeState.moveChapter(index, index + 1) },
                            enabled = !routeState.isLocked && index < routeState.chapters.lastIndex
                        ) { Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Move down") }
                    }

                    OutlinedTextField(
                        value = chapter.title,
                        onValueChange = { chapter.title = it },
                        label = { Text("Chapter ${index + 1}") },
                        enabled = !routeState.isLocked,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        singleLine = true
                    )

                    IconButton(
                        onClick = { routeState.removeChapter(chapter.id) },
                        enabled = !routeState.isLocked && routeState.chapters.size > 1
                    ) { Icon(Icons.Filled.Close, contentDescription = "Remove chapter") }
                }
            }
        }

        if (routeState.canAddChapter && !routeState.isLocked) {
            OutlinedButton(
                onClick = { routeState.addChapter() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("Add chapter")
            }
        }

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
