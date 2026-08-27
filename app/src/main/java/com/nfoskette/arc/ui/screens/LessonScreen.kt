package com.nfoskette.arc.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.nfoskette.arc.R
import com.nfoskette.arc.ui.RouteState
import com.nfoskette.arc.ui.theme.ArcMonoLabel

// Lesson View (docs/DESIGN.md ยง4): brain-globe + Save/Export, continuous
// scrolling chapter content, waypoint scrollspy nav.
//
// Known simplification: the spec's scrollspy is IntersectionObserver-driven;
// this uses LazyListState.firstVisibleItemIndex as a simpler proxy for "active
// chapter", which is close but not pixel-identical behavior. Save/Export buttons
// are stubs — no backend for that yet.
@Composable
fun LessonScreen(routeState: RouteState, isDarkTheme: Boolean) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val activeIndex by remember {
        androidx.compose.runtime.derivedStateOf { listState.firstVisibleItemIndex.coerceIn(0, routeState.chapters.lastIndex.coerceAtLeast(0)) }
    }

    Row(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(1f)) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(24.dp)
            ) {
                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Image(
                            painter = painterResource(
                                if (isDarkTheme) R.drawable.brain_globe_hero_dark else R.drawable.brain_globe_hero
                            ),
                            contentDescription = "ARC brain-globe",
                            modifier = Modifier.size(160.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        // Material 3 Expressive ButtonGroup (2026-08-26) - a connected,
                        // press-to-expand cluster in place of 3 separate outlined buttons.
                        ButtonGroup(
                            overflowIndicator = { menuState ->
                                ButtonGroupDefaults.OverflowIndicator(menuState = menuState)
                            }
                        ) {
                            clickableItem(onClick = { /* Save: no backend yet */ }, label = "Save")
                            clickableItem(onClick = { /* Export PDF: no backend yet */ }, label = "Export PDF")
                            clickableItem(onClick = { /* Export text: no backend yet */ }, label = "Export text")
                        }
                        Spacer(Modifier.height(24.dp))
                    }
                }

                itemsIndexed(routeState.chapters) { index, chapter ->
                    ChapterContent(index = index, title = chapter.title.ifBlank { "Chapter ${index + 1}" })
                    Spacer(Modifier.height(32.dp))
                }
            }
        }

        // Waypoint scrollspy nav (right-side dots per docs/DESIGN.md ยง4).
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(end = 12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            routeState.chapters.forEachIndexed { index, _ ->
                val isActive = index == activeIndex
                Box(
                    modifier = Modifier
                        .padding(vertical = 6.dp)
                        .size(if (isActive) 12.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (isActive) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline
                        )
                        .clickable {
                            scope.launch { listState.animateScrollToItem(index) }
                        }
                )
            }
        }
    }
}

@Composable
private fun ChapterContent(index: Int, title: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "WP-%02d".format(index),
            style = ArcMonoLabel,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(4.dp))
        Text(title, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                // 20.dp to match the Material You container roundness applied
                // across Builder's cards (2026-08-26), was 12.dp
                .clip(RoundedCornerShape(20.dp)),
            colors = androidx.compose.material3.CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "[credited photo — source cited below]",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "Source: example.edu",
            style = ArcMonoLabel,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "Fact-driven content for this chapter will be compiled here once the " +
                "research pipeline is built — this is placeholder text for layout purposes only.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
