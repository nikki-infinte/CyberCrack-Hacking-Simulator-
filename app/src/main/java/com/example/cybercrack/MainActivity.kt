package com.example.cybercrack

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.cybercrack.ui.theme.CyberCrackTheme
import androidx.core.view.WindowCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Force dark status bar icons
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false

        setContent {
            CyberCrackTheme(darkTheme = true) { // Force dark theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF121212) // Dark background color
                ) {
                    IntroScreen {
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }
}

@Composable
fun IntroScreen(onStartClick: () -> Unit) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.introanime))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header Section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 32.dp)
        ) {
            Text(
                text = "Welcome to CyberCrack",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF9FEF00), // Cyber blue color
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Learn & Test Your Cyber Security Skills",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }

        // Animation Section - takes 60% of the screen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            contentAlignment = Alignment.Center
        ) {
            LottieAnimation(
                composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Button Section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 48.dp)
        ) {
            Button(
                onClick = onStartClick,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9FEF00) // Cyber blue button
                )
            ) {
                Text(
                    "GET STARTED",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black // Dark text on bright button for contrast
                )
            }
        }
    }
}