package com.example.safelock.utils.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.safelock.presentation.onboarding.SignUpScreen


@Composable
fun ValidationFailureDialog(
    title: String,
    message: String,
    onCancelClick: () -> Unit,
    onTryAgainClick: (() -> Unit)? = null // Nullable to hide "Try Again"
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.BottomCenter,

        ) {
        Column(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .clip(
                    RoundedCornerShape(
                        topStart = 8.dp, topEnd = 8.dp,
                        bottomEnd = 8.dp, bottomStart = 8.dp
                    )
                )
                .background(Color.White)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Warning Icon
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.Red, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning Icon",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )

            }
            Spacer(modifier = Modifier.height(15.dp))

            // Title
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Message
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(25.dp))

            // Divider
            Divider(color = Color.LightGray, thickness = 1.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Cancel Button
                TextButton(onClick = onCancelClick) {
                    Text(
                        text = "Cancel",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Try Again Button
                if (onTryAgainClick != null) {
                    Spacer(modifier = Modifier.width(8.dp)) // Adjust spacing for better layout
                    Divider(
                        color = Color.LightGray,
                        modifier = Modifier
                            .height(32.dp)
                            .width(1.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp)) // Adjust spacing for better layout

                    TextButton(onClick = onTryAgainClick) {
                        Text(
                            text = "Try Again",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

        }
    }
}

@Composable
@Preview(showBackground = true)
fun ValidationFailureDialogPreview() {
    ValidationFailureDialog("Transfer",
        "Approved to Dayo",
        onCancelClick = {

        },
        onTryAgainClick = {})
}


