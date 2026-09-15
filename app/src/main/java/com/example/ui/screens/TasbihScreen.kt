package com.example.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterTiltShift
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.SakinaRepository
import com.example.ui.theme.SakinaError
import com.example.ui.theme.SakinaPrimary
import com.example.ui.theme.SakinaPrimaryContainer
import com.example.ui.theme.SakinaPrimaryFixed
import com.example.ui.theme.SakinaSecondary
import com.example.ui.theme.SakinaSecondaryContainer
import com.example.ui.theme.SakinaSurfaceContainer
import com.example.ui.theme.SakinaSurfaceContainerHigh
import com.example.ui.theme.SakinaSurfaceContainerLow
import com.example.ui.theme.SakinaSurfaceContainerLowest
import com.example.ui.theme.SakinaTertiary
import com.example.ui.theme.SakinaTertiaryContainer
import com.example.ui.theme.SakinaTertiaryFixed
import com.example.ui.theme.SakinaTertiaryFixedDim

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TasbihScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentDhikr by SakinaRepository.currentDhikr.collectAsState()
    val tasbihCount by SakinaRepository.tasbihCount.collectAsState()
    val tasbihTarget by SakinaRepository.tasbihTarget.collectAsState()
    val tasbihLap by SakinaRepository.tasbihLap.collectAsState()
    val totalLaps by SakinaRepository.totalLaps.collectAsState()
    val soundEnabled by SakinaRepository.soundEnabled.collectAsState()
    val hapticEnabled by SakinaRepository.hapticEnabled.collectAsState()

    var showDhikrDropdown by remember { mutableStateOf(false) }
    var showCustomTargetDialog by remember { mutableStateOf(false) }
    var customTargetInput by remember { mutableStateOf("") }
    var showResetDialog by remember { mutableStateOf(false) }
    var showSessionLoggedToast by remember { mutableStateOf(false) }
    var isZenMode by remember { mutableStateOf(false) }

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
                    isComplete -> longArrayOf(0, 60, 80, 100)
                    isMilestone -> longArrayOf(0, 40, 50, 40)
                    else -> longArrayOf(0, 25)
                }
                vibrator?.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(if (isMilestone) 80 else 25)
            }
        } catch (_: Exception) {}
    }

    val onIncrement = {
        val nextCount = tasbihCount + 1
        SakinaRepository.incrementTasbih()
        val isTargetReached = tasbihTarget != null && nextCount >= tasbihTarget!!
        val isMilestone = nextCount % 33 == 0
        vibrateBead(isMilestone = isMilestone, isComplete = isTargetReached)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp)
    ) {
        // 1. Top Controls & Status Hub
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Row 1: Dhikr selector trigger + Audio/Haptic Switches
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Dhikr Dropdown Trigger
                    Box {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(SakinaSurfaceContainer)
                                .clickable { showDhikrDropdown = true }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                                .testTag("dhikr_selector_btn"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(SakinaSecondary)
                            )
                            Text(
                                text = currentDhikr.arabic,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = SakinaPrimary
                            )
                            Text("·", color = SakinaSecondary.copy(alpha = 0.5f))
                            Text(
                                text = currentDhikr.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Icon(
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = SakinaSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showDhikrDropdown,
                            onDismissRequest = { showDhikrDropdown = false }
                        ) {
                            SakinaRepository.dhikrList.forEach { item ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(item.title, fontWeight = FontWeight.SemiBold)
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(item.arabic, color = SakinaSecondary)
                                        }
                                    },
                                    onClick = {
                                        SakinaRepository.setDhikr(item)
                                        showDhikrDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    // Sensory Feedback Controls
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(SakinaSurfaceContainerLow)
                            .padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { SakinaRepository.toggleSound() },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (soundEnabled) SakinaSurfaceContainerLowest else Color.Transparent)
                        ) {
                            Icon(
                                imageVector = if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                                contentDescription = "Toggle Sound",
                                tint = if (soundEnabled) SakinaPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(17.dp)
                            )
                        }

                        IconButton(
                            onClick = { SakinaRepository.toggleHaptic() },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (hapticEnabled) SakinaSecondaryContainer else Color.Transparent)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Vibration,
                                contentDescription = "Toggle Haptics",
                                tint = SakinaPrimary,
                                modifier = Modifier.size(17.dp)
                            )
                        }

                        IconButton(
                            onClick = { isZenMode = !isZenMode },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (isZenMode) SakinaTertiaryFixed else Color.Transparent)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterTiltShift,
                                contentDescription = "Zen Mode",
                                tint = if (isZenMode) SakinaTertiaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                }

                // Row 2: Target Badge, Lap Counter & Session Pace
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(SakinaPrimaryContainer)
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Flag, contentDescription = null, tint = SakinaTertiaryFixed, modifier = Modifier.size(12.dp))
                            Text(
                                text = if (tasbihTarget == null) "Target: ∞" else "Target: $tasbihTarget",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(SakinaSurfaceContainerHigh)
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Repeat, contentDescription = null, tint = SakinaSecondary, modifier = Modifier.size(12.dp))
                            Text(
                                text = "Lap $tasbihLap of $totalLaps",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(SakinaSecondary)
                        )
                        Text(
                            text = "Full Screen Tap Active",
                            fontSize = 11.sp,
                            color = SakinaSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // 2. Dhikr Context Card (Sacred scripture framing)
        if (!isZenMode) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SakinaSurfaceContainerLowest),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoStories,
                                contentDescription = null,
                                tint = SakinaTertiaryFixedDim,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "AUTHENTIC DHIKR",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                color = SakinaTertiaryContainer
                            )
                        }

                        Text(
                            text = currentDhikr.verseArabic,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Normal,
                            color = SakinaPrimary,
                            textAlign = TextAlign.Center,
                            lineHeight = 44.sp
                        )

                        Text(
                            text = currentDhikr.verseTranslit,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = SakinaSecondary,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = currentDhikr.verseMeaning,
                            fontSize = 12.sp,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(SakinaSurfaceContainer)
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = SakinaSecondary,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = currentDhikr.reference,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // 3. Core Interactive Tap Zone (Centered Tactile Counter Dial)
        item {
            val target = tasbihTarget
            val fraction = if (target != null && target > 0) {
                (tasbihCount.toFloat() / target.toFloat()).coerceIn(0f, 1f)
            } else {
                1f
            }
            val animatedFraction by animateFloatAsState(targetValue = fraction, label = "tasbihArcProgress")

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                // Main Big Tap Dial
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onIncrement() }
                        .testTag("tap-button"),
                    contentAlignment = Alignment.Center
                ) {
                    // SVG / Canvas Circular Progress Arc (Emerald & Gold)
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 10.dp.toPx()
                        // Track
                        drawCircle(
                            color = Color(0xFFE0F0FB),
                            style = Stroke(width = strokeWidth)
                        )
                        // Gradient Arc
                        if (target != null) {
                            drawArc(
                                brush = Brush.sweepGradient(
                                    listOf(SakinaPrimaryContainer, SakinaSecondary, SakinaTertiaryFixedDim, SakinaPrimaryContainer)
                                ),
                                startAngle = -90f,
                                sweepAngle = animatedFraction * 360f,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        } else {
                            drawCircle(
                                color = SakinaPrimaryContainer,
                                style = Stroke(width = strokeWidth)
                            )
                        }

                        // Subtle Star Arabesque Watermark in background
                        val center = Offset(size.width / 2, size.height / 2)
                        val radius = size.width * 0.32f
                        val path = Path()
                        val points = 8
                        for (i in 0 until points * 2) {
                            val r = if (i % 2 == 0) radius else radius * 0.5f
                            val angle = (i * Math.PI / points) - (Math.PI / 2)
                            val x = center.x + (r * Math.cos(angle)).toFloat()
                            val y = center.y + (r * Math.sin(angle)).toFloat()
                            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                        }
                        path.close()
                        drawPath(path, color = Color(0xFF1B4332).copy(alpha = 0.04f))
                    }

                    // Inner Dial Surface
                    Box(
                        modifier = Modifier
                            .size(208.dp)
                            .shadow(12.dp, CircleShape, spotColor = SakinaPrimary.copy(alpha = 0.15f))
                            .clip(CircleShape)
                            .background(SakinaSurfaceContainerLowest)
                            .border(1.dp, Color(0xFFE0F0FB), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "TASBIH COUNT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SakinaSecondary.copy(alpha = 0.7f),
                                letterSpacing = 1.2.sp
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "$tasbihCount",
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 44.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SakinaPrimary,
                                    letterSpacing = (-1).sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (target == null) "/ ∞" else "/ $target",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(SakinaSecondaryContainer.copy(alpha = 0.6f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.TouchApp, contentDescription = null, tint = SakinaSecondary, modifier = Modifier.size(12.dp))
                                Text(
                                    text = "Tap anywhere",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = SakinaPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Bottom Tactile Action Strip (Undo, Reset, Log Session)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Undo
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SakinaSurfaceContainerLowest)
                        .clickable {
                            SakinaRepository.decrementTasbih()
                            vibrateBead()
                        }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Undo, contentDescription = "Undo", tint = SakinaSecondary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Undo (-1)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                }

                // Reset
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SakinaSurfaceContainerLowest)
                        .clickable { showResetDialog = true }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = "Reset", tint = SakinaError, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SakinaError)
                }

                // Log Session
                Row(
                    modifier = Modifier
                        .weight(1.2f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SakinaPrimary)
                        .clickable {
                            showSessionLoggedToast = true
                            vibrateBead(isComplete = true)
                        }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.BookmarkAdd, contentDescription = null, tint = SakinaTertiaryFixed, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Log Session", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        // 5. Target Preset Chips Strip
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    TargetChip(
                        label = "33",
                        subtext = "(Post-Salah)",
                        isSelected = tasbihTarget == 33,
                        onClick = { SakinaRepository.setTarget(33) }
                    )
                }
                item {
                    TargetChip(
                        label = "100",
                        subtext = "(Daily Sunnah)",
                        isSelected = tasbihTarget == 100,
                        onClick = { SakinaRepository.setTarget(100) }
                    )
                }
                item {
                    TargetChip(
                        label = "1,000",
                        subtext = "(Salawat)",
                        isSelected = tasbihTarget == 1000,
                        onClick = { SakinaRepository.setTarget(1000) }
                    )
                }
                item {
                    TargetChip(
                        label = "∞",
                        subtext = "(Free Flow)",
                        isSelected = tasbihTarget == null,
                        onClick = { SakinaRepository.setTarget(null) }
                    )
                }
                item {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(SakinaSurfaceContainerHigh)
                            .clickable { showCustomTargetDialog = true }
                            .padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = SakinaSecondary, modifier = Modifier.size(14.dp))
                        Text("Custom", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SakinaSecondary)
                    }
                }
            }
        }

        // 6. Quick-Switch Dhikr Tray
        if (!isZenMode) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SakinaSurfaceContainerLow),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = SakinaTertiaryContainer, modifier = Modifier.size(15.dp))
                                Text(
                                    text = "QUICK DHIKR SWITCH",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SakinaSecondary,
                                    letterSpacing = 1.sp
                                )
                            }
                            Text(
                                text = "Tap to switch phrase",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SakinaRepository.dhikrList.forEach { dhikr ->
                                val isCurrent = dhikr.id == currentDhikr.id
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isCurrent) SakinaSurfaceContainerLowest else SakinaSurfaceContainer.copy(alpha = 0.6f))
                                        .border(
                                            width = if (isCurrent) 1.5.dp else 0.dp,
                                            color = if (isCurrent) SakinaPrimary else Color.Transparent,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable {
                                            SakinaRepository.setDhikr(dhikr)
                                            vibrateBead(isMilestone = true)
                                        }
                                        .padding(8.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = dhikr.arabic,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Normal,
                                            color = if (isCurrent) SakinaPrimary else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = dhikr.title,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 7. Session Micro-Analytics Pill
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SakinaSurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(SakinaSecondaryContainer.copy(alpha = 0.7f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Insights, contentDescription = null, tint = SakinaPrimary, modifier = Modifier.size(16.dp))
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("Today: $tasbihCount reps", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text("·", color = Color.Gray)
                                Text("Avg: 1.2s", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(SakinaTertiaryFixedDim))
                                Text("Gentle pulse every 33rd bead", fontSize = 11.sp, color = SakinaSecondary)
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(SakinaSurfaceContainer)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "SYNCED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SakinaPrimary,
                            letterSpacing = 0.8.sp
                        )
                    }
                }
            }
        }
    }

    // Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Count?", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to reset your current count back to 0?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        SakinaRepository.resetTasbih()
                        showResetDialog = false
                    }
                ) {
                    Text("Reset", color = SakinaError, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = SakinaPrimary)
                }
            }
        )
    }

    // Custom Target Input Dialog
    if (showCustomTargetDialog) {
        AlertDialog(
            onDismissRequest = { showCustomTargetDialog = false },
            title = { Text("Set Custom Target", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter the repetition goal for this remembrance:")
                    OutlinedTextField(
                        value = customTargetInput,
                        onValueChange = { customTargetInput = it.filter { ch -> ch.isDigit() } },
                        placeholder = { Text("e.g. 70") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val num = customTargetInput.toIntOrNull()
                        if (num != null && num > 0) {
                            SakinaRepository.setTarget(num)
                        }
                        showCustomTargetDialog = false
                    }
                ) {
                    Text("Set", color = SakinaPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomTargetDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    // Log Session Feedback Banner
    AnimatedVisibility(
        visible = showSessionLoggedToast,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = Modifier.padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SakinaPrimary)
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SakinaTertiaryFixed, modifier = Modifier.size(22.dp))
                    Column {
                        Text("Session Logged!", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        Text("May Allah accept your mindful remembrance.", fontSize = 12.sp, color = SakinaPrimaryFixed)
                    }
                }
                IconButton(onClick = { showSessionLoggedToast = false }) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun TargetChip(
    label: String,
    subtext: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (isSelected) SakinaPrimary else SakinaSurfaceContainer)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = subtext,
            fontSize = 10.sp,
            color = if (isSelected) SakinaPrimaryFixed else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
