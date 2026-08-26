package com.nfoskette.arc.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nfoskette.arc.R

// Landing/Home screen (docs/DESIGN.md ยง4): hero copy, brain-globe visual, two CTAs.
// The brain-globe here is the static placeholder render agreed for tonight — real
// 3D (Filament/SceneView) integration is a separate, not-yet-started task.
@Composable
fun HomeScreen(
    onPlanRoute: () -> Unit,
    onSeeSample: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(PaddingValues(horizontal = 24.dp, vertical = 32.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Image(
            painter = painterResource(R.drawable.brain_globe_hero),
            contentDescription = "ARC brain-globe",
            modifier = Modifier.size(220.dp)
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Fly from what you know\nto what you want to learn",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "ARC plans a route between two topics, built from sourced, " +
                "credited facts — never AI-generated images, never guesswork.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = onPlanRoute,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Plan my first route")
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onSeeSample,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("See a sample route")
        }
    }
}
