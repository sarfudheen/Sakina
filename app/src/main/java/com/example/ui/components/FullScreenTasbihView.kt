package com.example.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.audio.SakinaAudioPlayer
import com.example.data.model.DhikrItem
import com.example.data.repository.SakinaRepository
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.SakinaLocaleManager
import com.example.ui.theme.SakinaPrimary
import com.example.ui.theme.SakinaPrimaryContainer
import com.example.ui.theme.SakinaPrimaryFixed
import com.example.ui.theme.SakinaSecondary
import com.example.ui.theme.SakinaSecondaryContainer
import com.example.ui.theme.SakinaTertiaryFixed
import com.example.ui.theme.SakinaTertiaryFixedDim
import kotlinx.coroutines.delay
import java.util.Locale

enum class AutoTasbihMode {
    OFF,
    CALIBRATING, // Learning rhythm from first 3-5 taps
    RUNNING,     // Automatically incrementing at user's personal cadence
    PAUSED
}

@Composable
fun FullScreenTasbihDialog(
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        FullScreenTasbihContent(onDismiss = onDismissRequest)
    }
}

@Composable
fun FullScreenTasbihContent(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val currentDhikr by SakinaRepository.currentDhikr.collectAsState()
    val tasbihCount by SakinaRepository.tasbihCount.collectAsState()
    val tasbihTarget by SakinaRepository.tasbihTarget.collectAsState()
    val soundEnabled by SakinaRepository.soundEnabled.collectAsState()
    val hapticEnabled by SakinaRepository.hapticEnabled.collectAsState()

    val currentLanguage by SakinaLocaleManager.currentLanguage.collectAsState()
    val isArabic = currentLanguage == AppLanguage.ARABIC

    val isAudioPlaying by SakinaAudioPlayer.isPlaying.collectAsState()

    // Auto Mode & Cadence Learning State
    var autoMode by remember { mutableStateOf(AutoTasbihMode.OFF) }
    val tapTimestamps = remember { mutableStateListOf<Long>() }
    var learnedIntervalMs by remember { mutableLongStateOf(1400L) }
    var autoCadencePaceSec by remember { mutableStateOf(1.4f) }

    fun vibrateBead(isMilestone: Boolean = false, isComplete: Boolean = false) {
        if (!hapticEnabled) return
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val pattern = when {
                    isComplete -> longArrayOf(0, 70, 70, 120)
                    isMilestone -> longArrayOf(0, 45, 50, 45)
                    else -> longArrayOf(0, 25)
                }
                vibrator?.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(if (isMilestone) 70 else 25)
            }
        } catch (_: Exception) {}
    }

    val onIncrement = {
        val nextCount = tasbihCount + 1
        SakinaRepository.incrementTasbih()
        val isTargetReached = tasbihTarget != null && nextCount >= tasbihTarget!!
        val isMilestone = nextCount % 33 == 0
        vibrateBead(isMilestone = isMilestone, isComplete = isTargetReached)

        if (isTargetReached && autoMode == AutoTasbihMode.RUNNING) {
            autoMode = AutoTasbihMode.PAUSED
        }
    }

    // Handle Tap in Calibrating Mode (learning pace from first 4-5 taps)
    val handleUserTap = {
        val now = System.currentTimeMillis()
        if (autoMode == AutoTasbihMode.CALIBRATING) {
            tapTimestamps.add(now)
            onIncrement()

            // Need 4 taps (which gives 3 intervals) to learn user's natural tempo
            if (tapTimestamps.size >= 4) {
                val intervals = mutableListOf<Long>()
                for (i in 1 until tapTimestamps.size) {
                    intervals.add(tapTimestamps[i] - tapTimestamps[i - 1])
                }
                val avg = intervals.average().toLong().coerceIn(600L, 4000L)
                learnedIntervalMs = avg
                autoCadencePaceSec = (avg / 100.0f).toInt() / 10.0f
                autoMode = AutoTasbihMode.RUNNING
                vibrateBead(isMilestone = true)
            }
        } else {
            onIncrement()
        }
    }

    // Auto-Increment Loop when in RUNNING state
    LaunchedEffect(autoMode, learnedIntervalMs) {
        if (autoMode == AutoTasbihMode.RUNNING) {
            while (autoMode == AutoTasbihMode.RUNNING) {
                delay(learnedIntervalMs)
                onIncrement()
            }
        }
    }

    // Subtle pulsing animation when auto-running
    val infiniteTransition = rememberInfiniteTransition(label = "tasbih_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (autoMode == AutoTasbihMode.RUNNING) 1.04f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (learnedIntervalMs / 2).toInt().coerceAtLeast(300),
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val progress = if (tasbihTarget != null && tasbihTarget!! > 0) {
        (tasbihCount.toFloat() / tasbihTarget!!.toFloat()).coerceIn(0f, 1f)
    } else {
        (tasbihCount % 33) / 33f
    }
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "progress")

    // Full screen canvas container
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("fullscreen_tasbih_screen"),
        color = Color(0xFF071410) // Tranquil Deep Emerald Night
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    handleUserTap()
                }
        ) {
            // Ambient Geometric Radial Background Glow
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height * 0.45f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF1B4332).copy(alpha = 0.45f),
                            Color(0xFF0F2C22).copy(alpha = 0.2f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = size.width * 0.85f
                    ),
                    center = center,
                    radius = size.width * 0.85f
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 1. Top HUD Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Close Button
                    IconButton(
                        onClick = {
                            autoMode = AutoTasbihMode.OFF
                            SakinaAudioPlayer.stop()
                            onDismiss()
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                            .testTag("close_fullscreen_tasbih")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Exit Full Screen",
                            tint = Color.White
                        )
                    }

                    // Dhikr Badge
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFF16382C))
                            .border(1.dp, Color(0xFF2D6A4F).copy(alpha = 0.6f), RoundedCornerShape(50))
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (autoMode == AutoTasbihMode.RUNNING) SakinaTertiaryFixed else Color(0xFF52B788))
                        )
                        Text(
                            text = if (tasbihTarget == null) "Target: ∞" else "Goal: $tasbihTarget",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Audio & Feedback Hub
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Play Pronunciation
                        IconButton(
                            onClick = {
                                SakinaAudioPlayer.playDhikr(context, currentDhikr)
                            },
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (isAudioPlaying) SakinaPrimaryContainer else Color.White.copy(alpha = 0.08f))
                        ) {
                            Icon(
                                imageVector = if (isAudioPlaying) Icons.Default.Pause else Icons.Default.RecordVoiceOver,
                                contentDescription = "Pronunciation Guide",
                                tint = if (isAudioPlaying) SakinaTertiaryFixed else Color.White,
                                modifier = Modifier.size(19.dp)
                            )
                        }

                        // Sound Toggle
                        IconButton(
                            onClick = { SakinaRepository.toggleSound() },
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (soundEnabled) Color.White.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.08f))
                        ) {
                            Icon(
                                imageVector = if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                                contentDescription = "Toggle Sound",
                                tint = Color.White,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }
                }

                // 2. Central Dhikr Calligraphy & Sacred Typography
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = currentDhikr.arabic,
                        fontSize = 38.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD8F3DC),
                        textAlign = TextAlign.Center,
                        lineHeight = 46.sp
                    )

                    if (currentDhikr.phoneticBreakdown != null) {
                        Text(
                            text = currentDhikr.phoneticBreakdown!!,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium,
                            color = SakinaTertiaryFixedDim
                        )
                    }

                    Text(
                        text = currentDhikr.verseMeaning,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.65f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }

                // 3. Immersive Giant Bead Dial with Responsive Pulse
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .scale(pulseScale),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeW = 12.dp.toPx()
                        // Track ring
                        drawCircle(
                            color = Color(0xFF1B4332).copy(alpha = 0.5f),
                            style = Stroke(width = strokeW)
                        )
                        // Progress Arc
                        drawArc(
                            brush = Brush.sweepGradient(
                                listOf(
                                    Color(0xFF52B788),
                                    Color(0xFF74C69D),
                                    Color(0xFFB7E4C7),
                                    Color(0xFFE9D8A6),
                                    Color(0xFF52B788)
                                )
                            ),
                            startAngle = -90f,
                            sweepAngle = animatedProgress * 360f,
                            useCenter = false,
                            style = Stroke(width = strokeW, cap = StrokeCap.Round)
                        )
                    }

                    // Center Interactive Orb
                    Box(
                        modifier = Modifier
                            .size(190.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF1B4332),
                                        Color(0xFF0F2C22),
                                        Color(0xFF0A1F18)
                                    )
                                )
                            )
                            .border(1.5.dp, Color(0xFF52B788).copy(alpha = 0.35f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "$tasbihCount",
                                fontSize = 56.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontFamily = FontFamily.SansSerif
                            )
                            Text(
                                text = if (tasbihTarget != null) "OF $tasbihTarget" else "BEADS RECITED",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF74C69D),
                                letterSpacing = 1.2.sp
                            )
                        }
                    }
                }

                // 4. Smart Cadence Calibration & Auto Mode Banner
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    when (autoMode) {
                        AutoTasbihMode.OFF -> {
                            // Instruction & Auto Mode Activation Trigger
                            Text(
                                text = if (isArabic) "المس أي مكان على الشاشة للتسبيح" else "Tap anywhere on screen to count",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(Color(0xFF16382C))
                                    .border(1.dp, Color(0xFF2D6A4F), RoundedCornerShape(50))
                                    .clickable {
                                        tapTimestamps.clear()
                                        autoMode = AutoTasbihMode.CALIBRATING
                                        vibrateBead()
                                    }
                                    .padding(horizontal = 18.dp, vertical = 10.dp)
                                    .testTag("enable_auto_cadence_btn"),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = null,
                                    tint = SakinaTertiaryFixed,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = if (isArabic) "العد التلقائي (تعلم وتيرتي)" else "Auto Mode (Learn My Cadence)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        AutoTasbihMode.CALIBRATING -> {
                            // Learning user's natural pace from first 3-5 taps
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF16382C))
                                    .border(1.5.dp, SakinaTertiaryFixed, RoundedCornerShape(16.dp))
                                    .padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = if (isArabic)
                                        "المس الشاشة 4 مرات بوتيرتك الطبيعية..."
                                    else
                                        "Tap 4 times at your natural recitation pace...",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SakinaTertiaryFixed
                                )

                                // Tap counter dots
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    for (i in 0 until 4) {
                                        val isTapped = i < tapTimestamps.size
                                        Box(
                                            modifier = Modifier
                                                .size(14.dp)
                                                .clip(CircleShape)
                                                .background(if (isTapped) SakinaTertiaryFixed else Color.White.copy(alpha = 0.2f))
                                                .border(1.dp, if (isTapped) SakinaTertiaryFixed else Color.White.copy(alpha = 0.4f), CircleShape)
                                        )
                                    }
                                }

                                Text(
                                    text = if (isArabic)
                                        "النقرات المسجلة: ${tapTimestamps.size} من 4"
                                    else
                                        "Taps captured: ${tapTimestamps.size} / 4",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )

                                Row(
                                    modifier = Modifier.clickable {
                                        autoMode = AutoTasbihMode.OFF
                                        tapTimestamps.clear()
                                    }
                                ) {
                                    Text(
                                        text = if (isArabic) "إلغاء المعايرة" else "Cancel Calibration",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }

                        AutoTasbihMode.RUNNING, AutoTasbihMode.PAUSED -> {
                            // Active Auto Mode with Tempo Control
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF112920))
                                    .border(1.dp, Color(0xFF2D6A4F), RoundedCornerShape(16.dp))
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(if (autoMode == AutoTasbihMode.RUNNING) Color(0xFF52B788) else Color(0xFFE9D8A6))
                                        )
                                        Text(
                                            text = if (autoMode == AutoTasbihMode.RUNNING) {
                                                if (isArabic) "جارٍ العد التلقائي (وتيرتك: ${autoCadencePaceSec}ث)" else "Auto Cadence: ${autoCadencePaceSec}s / dhikr"
                                            } else {
                                                if (isArabic) "متوقف مؤقتاً (${autoCadencePaceSec}ث)" else "Paused (${autoCadencePaceSec}s)"
                                            },
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }

                                    // Retrain Cadence
                                    Row(
                                        modifier = Modifier
                                            .clickable {
                                                tapTimestamps.clear()
                                                autoMode = AutoTasbihMode.CALIBRATING
                                            }
                                            .padding(4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = "Recalibrate",
                                            tint = Color(0xFF74C69D),
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = if (isArabic) "إعادة التحديد" else "Re-learn",
                                            fontSize = 11.sp,
                                            color = Color(0xFF74C69D)
                                        )
                                    }
                                }

                                // Tempo Adjusters (-0.2s, Pause/Play, +0.2s, Stop)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Faster (-0.2s)
                                    IconButton(
                                        onClick = {
                                            val newPace = (autoCadencePaceSec - 0.2f).coerceAtLeast(0.6f)
                                            autoCadencePaceSec = (newPace * 10).toInt() / 10.0f
                                            learnedIntervalMs = (autoCadencePaceSec * 1000).toLong()
                                        },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.08f))
                                    ) {
                                        Text("-0.2s", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }

                                    // Play / Pause Toggle
                                    IconButton(
                                        onClick = {
                                            autoMode = if (autoMode == AutoTasbihMode.RUNNING) {
                                                AutoTasbihMode.PAUSED
                                            } else {
                                                AutoTasbihMode.RUNNING
                                            }
                                            vibrateBead()
                                        },
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .background(SakinaPrimaryContainer)
                                    ) {
                                        Icon(
                                            imageVector = if (autoMode == AutoTasbihMode.RUNNING) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = "Toggle Play",
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    // Slower (+0.2s)
                                    IconButton(
                                        onClick = {
                                            val newPace = (autoCadencePaceSec + 0.2f).coerceAtMost(4.5f)
                                            autoCadencePaceSec = (newPace * 10).toInt() / 10.0f
                                            learnedIntervalMs = (autoCadencePaceSec * 1000).toLong()
                                        },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.08f))
                                    ) {
                                        Text("+0.2s", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }

                                    // Turn Off Auto
                                    IconButton(
                                        onClick = {
                                            autoMode = AutoTasbihMode.OFF
                                            tapTimestamps.clear()
                                        },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.08f))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Stop Auto Mode",
                                            tint = Color.White.copy(alpha = 0.8f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
