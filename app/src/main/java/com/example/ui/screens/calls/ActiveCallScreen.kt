package com.example.ui.screens.calls

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CallType
import com.example.data.repository.PulseRepository
import com.example.ui.PulseViewModel
import com.example.ui.components.PulseAvatar
import com.example.ui.theme.*

@Composable
fun ActiveCallScreen(
    session: PulseRepository.ActiveCallSession,
    viewModel: PulseViewModel,
    modifier: Modifier = Modifier
) {
    val durationText = remember(session.durationSeconds) {
        val mins = session.durationSeconds / 60
        val secs = session.durationSeconds % 60
        "${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF090D16), Color(0xFF0F172A), Color(0xFF1E1B4B))
                )
            )
            .testTag("active_call_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Status Bar: Peer Name, Encryption status, Call duration, Network quality
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 28.dp)
            ) {
                Surface(
                    color = PulseEmerald.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = PulseEmerald,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "End-to-End Encrypted WebRTC",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PulseEmerald
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = session.peerName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = durationText,
                    style = MaterialTheme.typography.titleMedium,
                    color = PulseCyanLight,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.NetworkCheck,
                        contentDescription = null,
                        tint = PulseEmerald,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = session.networkQuality,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            // Center Call Animation / Video simulation
            if (session.callType == CallType.VIDEO && session.isVideoEnabled) {
                Box(
                    modifier = Modifier
                        .size(240.dp, 320.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF1E293B), Color(0xFF0F172A), Color(0xFF334155))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    PulseAvatar(
                        name = session.peerName,
                        avatarColor = session.peerAvatarColor,
                        size = 90.dp
                    )
                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = if (session.isFrontCamera) "Front Camera HD" else "Rear Camera HD",
                            fontSize = 10.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            } else {
                // Audio Wave Pulse Rings
                Box(
                    modifier = Modifier.size(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color = PulseIndigoLight.copy(alpha = 0.15f),
                            radius = size.minDimension / 2
                        )
                        drawCircle(
                            color = PulseIndigo.copy(alpha = 0.25f),
                            radius = size.minDimension / 2.5f
                        )
                    }
                    PulseAvatar(
                        name = session.peerName,
                        avatarColor = session.peerAvatarColor,
                        size = 110.dp
                    )
                }
            }

            // Bottom Call Controls Bar
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Surface(
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(32.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Mute button
                        IconButton(
                            onClick = { viewModel.toggleCallMute() },
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(if (session.isMuted) PulseRose else Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = if (session.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = "Mute",
                                tint = Color.White
                            )
                        }

                        // Video toggle
                        IconButton(
                            onClick = { viewModel.toggleCallVideo() },
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(if (!session.isVideoEnabled) PulseRose else Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = if (session.isVideoEnabled) Icons.Default.Videocam else Icons.Default.VideocamOff,
                                contentDescription = "Video",
                                tint = Color.White
                            )
                        }

                        // Speaker button
                        IconButton(
                            onClick = { viewModel.toggleCallSpeaker() },
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(if (session.isSpeakerOn) PulseIndigo else Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = if (session.isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeDown,
                                contentDescription = "Speaker",
                                tint = Color.White
                            )
                        }

                        // Camera Flip button
                        IconButton(
                            onClick = { viewModel.flipCamera() },
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlipCameraAndroid,
                                contentDescription = "Flip Camera",
                                tint = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // End Call Button
                IconButton(
                    onClick = { viewModel.endCall() },
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(PulseRose)
                        .testTag("end_call_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CallEnd,
                        contentDescription = "End Call",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}
