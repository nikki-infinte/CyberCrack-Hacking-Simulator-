package com.example.cybercrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.cybercrack.ui.theme.CyberCrackTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Force dark status bar icons if needed
        // WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false

        setContent {
            CyberCrackTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF121212) // Dark background color
                ) {
                    LoginScreen(
                        onLoginSuccess = {
                            startActivity(Intent(this, MainHome::class.java))
                            finish()
                        },
                        onNavigateToSignup = {
                             startActivity(Intent(this, SignupActivity::class.java))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToSignup: () -> Unit
) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    // Load Lottie animation
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.intro))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Title
        Text(
            text = "CyberCrack",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF9FEF00), // Cyber blue color
            textAlign = TextAlign.Center
        )

        // Lottie Animation at the top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.fillMaxSize(0.8f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Username field
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF9FEF00),
                focusedLabelColor = Color(0xFF9FEF00),
                cursorColor = Color(0xFF9FEF00),
                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF9FEF00),
                focusedLabelColor = Color(0xFF9FEF00),
                cursorColor = Color(0xFF9FEF00),
                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
            )
        )

        // Error message
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Login button
        Button(
            onClick = {
                if (validateLogin(context, username, password)) {
                    onLoginSuccess()
                } else {
                    errorMessage = "Invalid credentials"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF9FEF00) // Cyber blue button
            )
        ) {
            Text(
                "LOGIN",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black // Dark text on bright button for contrast
            )
        }

        // Sign up link
        TextButton(
            onClick = onNavigateToSignup,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(
                "Don't have an account? Sign up",
                color = Color(0xFF9FEF00)
            )
        }
    }
}

fun validateLogin(context: Context, username: String, password: String): Boolean {
    val sharedPref = context.getSharedPreferences("CyberCrackPrefs", Context.MODE_PRIVATE)
    val savedUsername = sharedPref.getString("username", "")
    val savedPassword = sharedPref.getString("password", "")

    return username == savedUsername && password == savedPassword
}