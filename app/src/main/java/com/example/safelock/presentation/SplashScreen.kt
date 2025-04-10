import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.safelock.R
import com.example.safelock.presentation.AnalyticsViewModel
import com.example.safelock.utils.Firebase.EventTracker
import com.example.safelock.utils.Firebase.FirebaseAnalyticsEntryPoint
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.EntryPointAccessors

@Composable
fun SplashScreen(navController: NavController) {
    val modifier = Modifier
    val linearGradientBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFB226E1),
            Color(0xFFFC6603),
            Color(0xFF5995EE),
            Color(0xFF3D3535)
        ),
        start = Offset(Float.POSITIVE_INFINITY, 0f),
        end = Offset(0f, Float.POSITIVE_INFINITY)
    )

    val context = LocalContext.current
    // Cast the context to Activity (your host activity should be annotated with @AndroidEntryPoint)
    val firebaseAnalytics = remember {
        EntryPointAccessors.fromActivity(
            context as Activity,
            FirebaseAnalyticsEntryPoint::class.java
        ).getFirebaseAnalytics()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.securityprotectionhologram),
            contentDescription = "",
            modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Button(
                onClick = {
                    navController.navigate("Getting started")
                    val bundle = EventTracker.trackEvent("SPLASH SCREEN",
                        "SplashScreen")
                    firebaseAnalytics.logEvent(
                        "continue_button_click_in_splashscreen",
                        bundle
                    )
                },
                modifier
                    .padding(bottom = 55.dp, start = 20.dp, end = 20.dp)
                    .fillMaxWidth()
                    .background(color = Color.Gray.copy(0.8f), RoundedCornerShape(16.dp))
                    .border(
                        BorderStroke(
                            3.dp,
                            linearGradientBrush
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                )
            ) {
                Text(
                    text = "Continue", style = TextStyle(
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontFamily = FontFamily(Font(R.font.cinzel_decorative)),
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
    }

}


@SuppressLint("ComposableNaming")
@Composable
fun trackEvent(name: String, firebaseAnalytics: FirebaseAnalytics) {
    DisposableEffect(Unit){
        onDispose {
            val bundle = EventTracker.trackEvent("SplashScreen", "SplashScreen")
           firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
        }
    }
}