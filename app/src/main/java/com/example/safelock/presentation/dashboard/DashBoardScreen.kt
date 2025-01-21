package com.example.safelock.presentation.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DashBoardScreen(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth()
        .fillMaxHeight()
        .padding(top = 16.dp)){

        Text("Welcome to Dashboard",
            style = MaterialTheme.typography.headlineMedium)
    }
}


@Preview(showBackground = true)
@Composable
fun DashBoardScreenPreview(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth()
        .fillMaxHeight()
        .padding(top = 16.dp)){

        Text("Welcome to Dashboard",
            style = MaterialTheme.typography.headlineMedium)
    }
}