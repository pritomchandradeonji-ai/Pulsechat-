package com.example.ui.screens.auth

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.PulseViewModel
import com.example.ui.theme.*

@Composable
fun AuthScreen(
    viewModel: PulseViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val appName by viewModel.appName.collectAsState()

    var isPhoneMode by remember { mutableStateOf(true) }
    var phoneNumber by remember { mutableStateOf("+1 555 019 2834") }
    var emailAddress by remember { mutableStateOf("alex.vance@pulse.io") }
    var otpCode by remember { mutableStateOf("") }
    var isOtpStep by remember { mutableStateOf(false) }
    var twoFactorPin by remember { mutableStateOf("") }
    var isPinStep by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 440.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // App Logo and Hero graphic
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(PulseIndigo),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubble,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Text(
                    text = appName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = PulseIndigoLight
                )

                Text(
                    text = if (isPinStep) "Enter Two-Step Verification PIN"
                    else if (isOtpStep) "Enter the 6-digit verification code"
                    else "Private, secure & fast communication platform",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                if (isPinStep) {
                    // Two-Step Verification PIN step
                    OutlinedTextField(
                        value = twoFactorPin,
                        onValueChange = { if (it.length <= 6) twoFactorPin = it },
                        label = { Text("6-Digit Security PIN") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("auth_pin_input")
                    )

                    Button(
                        onClick = {
                            viewModel.authenticateSuccess()
                            Toast.makeText(context, "Welcome back to $appName!", Toast.LENGTH_SHORT).show()
                        },
                        enabled = twoFactorPin.length >= 4,
                        colors = ButtonDefaults.buttonColors(containerColor = PulseIndigo),
                        modifier = Modifier.fillMaxWidth().testTag("auth_verify_pin_btn")
                    ) {
                        Text("Verify & Continue", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                } else if (isOtpStep) {
                    // OTP Step
                    OutlinedTextField(
                        value = otpCode,
                        onValueChange = { if (it.length <= 6) otpCode = it },
                        label = { Text("SMS Verification Code") },
                        placeholder = { Text("123456") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("auth_otp_input")
                    )

                    Button(
                        onClick = {
                            isOtpStep = false
                            isPinStep = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PulseIndigo),
                        modifier = Modifier.fillMaxWidth().testTag("auth_verify_otp_btn")
                    ) {
                        Text("Verify Code", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    TextButton(onClick = { isOtpStep = false }) {
                        Text("Change phone/email")
                    }
                } else {
                    // Login / Registration Form
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        FilterChip(
                            selected = isPhoneMode,
                            onClick = { isPhoneMode = true },
                            label = { Text("Phone Number") },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        FilterChip(
                            selected = !isPhoneMode,
                            onClick = { isPhoneMode = false },
                            label = { Text("Email Address") }
                        )
                    }

                    if (isPhoneMode) {
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = { Text("Phone Number") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("auth_phone_input")
                        )
                    } else {
                        OutlinedTextField(
                            value = emailAddress,
                            onValueChange = { emailAddress = it },
                            label = { Text("Email Address") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("auth_email_input")
                        )
                    }

                    Button(
                        onClick = {
                            isOtpStep = true
                            Toast.makeText(context, "OTP Code sent: 492810", Toast.LENGTH_LONG).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PulseIndigo),
                        modifier = Modifier.fillMaxWidth().testTag("auth_continue_btn")
                    ) {
                        Text("Continue", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Divider(modifier = Modifier.padding(vertical = 4.dp))

                    // Social login options
                    OutlinedButton(
                        onClick = {
                            viewModel.authenticateSuccess()
                            Toast.makeText(context, "Logged in via Google Secure Identity", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Continue with Google")
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.authenticateSuccess()
                            Toast.makeText(context, "Logged in via Apple ID", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Continue with Apple")
                    }
                }
            }
        }
    }
}
