package com.example.safelock.presentation.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Preview(showBackground = true)
@Composable
fun LoginScreen(modifier: Modifier = Modifier) {

    Box(modifier = Modifier.fillMaxWidth()){

        Text("Hello Login Screen")
    }

}