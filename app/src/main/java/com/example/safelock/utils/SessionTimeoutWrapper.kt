package com.example.safelock.utils

import android.os.Process
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay

@Composable
fun SessionTimeoutWrapper(
    timeoutMillis: Long = 10 * 60 * 10L, // 10 minutes
    content: @Composable () -> Unit
) {
    val lastActivityTime = remember { mutableLongStateOf(System.currentTimeMillis()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                // This suspends and waits for pointer events.
                detectTapGestures(
                    onPress = {
                        // Any tap (or press) resets the timer.
                        lastActivityTime.longValue = System.currentTimeMillis()
                        tryAwaitRelease()
                    }
                )
            }
    ) {
        content()
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(60 * 1000L) // Check every minute.
            val elapsed = System.currentTimeMillis() - lastActivityTime.longValue
            if (elapsed >= timeoutMillis) {
                // Timeout reached; kill the process.
                Process.killProcess(Process.myPid())
            }
        }
    }
}
