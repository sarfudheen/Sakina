package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material.icons.outlined.Adjust
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.SakinaRepository
import com.example.data.location.PrayerCalculator
import com.example.data.location.QiblaInfo
import com.example.ui.components.SakinaTab
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.SakinaLocaleManager
import com.example.ui.theme.SakinaPrimary
import com.example.ui.theme.SakinaPrimaryContainer
import com.example.ui.theme.SakinaPrimaryFixed
import com.example.ui.theme.SakinaPrimaryFixedDim
import com.example.ui.theme.SakinaSecondary
import com.example.ui.theme.SakinaSecondaryContainer
import com.example.ui.theme.SakinaSurfaceContainer
import com.example.ui.theme.SakinaSurfaceContainerHigh
import com.example.ui.theme.SakinaSurfaceContainerLow
import com.example.ui.theme.SakinaSurfaceContainerLowest
import com.example.ui.theme.SakinaSurfaceTint
import com.example.ui.theme.SakinaTertiaryContainer
import com.example.ui.theme.SakinaTertiaryFixed
import com.example.ui.theme.SakinaTertiaryFixedDim

@Composable
fun HomeScreen(
    onNavigateTab: (SakinaTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dailyCount by SakinaRepository.dailyDhikrCount.collectAsState()
    val streakDays by SakinaRepository.streakDays.collectAsState()
    val routines by SakinaRepository.routines.collectAsState()
    val prayerInfo by SakinaRepository.prayerInfo.collectAsState()
    val prayerTimesList by SakinaRepository.prayerTimesListState.collectAsState()
    val locationConfig by SakinaRepository.locationConfig.collectAsState()
    val currentLanguage by SakinaLocaleManager.currentLanguage.collectAsState()
    val isArabic = currentLanguage == AppLanguage.ARABIC
    var isAyahAudioPlaying by remember { mutableStateOf(false) }

    val qibla: QiblaInfo = remember(locationConfig.latitude, locationConfig.longitude) {
        PrayerCalculator.calculateQibla(locationConfig.latitude, locationConfig.longitude)
    }

    fun vibrateShort() {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(30)
            }
        } catch (_: Exception) {}
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 28.dp)
    ) {
        // 1. Gentle Greeting & Hijri Date Strip
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${prayerInfo.hijriDate.uppercase()} · ${(if (isArabic) locationConfig.arabicCityName else locationConfig.cityName).uppercase()}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SakinaSecondary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isArabic) "السلام عليكم، طارق" else "As-salamu alaykum, Tariq",
                        fontFamily = FontFamily.Serif,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = SakinaPrimary,
                        letterSpacing = (-0.5).sp
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(SakinaSecondaryContainer.copy(alpha = 0.8f))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = "Streak",
                        tint = SakinaTertiaryContainer,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "$streakDays ${if (isArabic) "يوم سكينة" else "Day Sakina"}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SakinaPrimary
                    )
                }
            }
        }

        // 2. Spiritual Prayer Focus Card (Dynamic Location & Next Prayer)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                SakinaPrimaryContainer,
                                Color(0xFF1B4332),
                                SakinaPrimary
                            )
                        )
                    )
                    .clickable { onNavigateTab(SakinaTab.HABITS) }
                    .padding(18.dp)
                    .testTag("prayer_focus_card")
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Top row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(SakinaTertiaryFixedDim)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isArabic) "الصلاة القادمة" else "NEXT PRAYER",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SakinaPrimaryFixed,
                                letterSpacing = 1.sp
                            )
                        }

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color.White.copy(alpha = 0.12f))
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Explore,
                                contentDescription = "Qibla",
                                tint = SakinaPrimaryFixed,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "${if (isArabic) "القبلة" else "Qibla"} ${qibla.bearingDegrees}° ${qibla.directionText}",
                                fontSize = 11.sp,
                                color = SakinaPrimaryFixed,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Middle Row: Next Prayer Name & Time
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isArabic) prayerInfo.nextPrayerArabic else prayerInfo.nextPrayerName,
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = prayerInfo.nextPrayerTime,
                                    fontSize = 18.sp,
                                    color = SakinaPrimaryFixedDim,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = if (isArabic) "خلال ${prayerInfo.countdownMinutes} دقيقة تقريباً" else "In approximately ${prayerInfo.countdownMinutes} minutes",
                                fontSize = 13.sp,
                                color = SakinaPrimaryFixedDim.copy(alpha = 0.9f)
                            )
                        }

                        Text(
                            text = prayerInfo.nextPrayerArabic,
                            fontSize = 26.sp,
                            color = SakinaTertiaryFixed,
                            fontWeight = FontWeight.Normal
                        )
                    }

                    // Prayer Progress Timeline Bar
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.72f)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(SakinaSecondaryContainer, SakinaTertiaryFixedDim, SakinaPrimaryFixed)
                                        )
                                    )
                            )
                        }

                        val nextPrayer = prayerTimesList.find { it.isNext } ?: prayerTimesList.getOrNull(2)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${prayerTimesList.firstOrNull()?.let { if (isArabic) it.arabicName else it.name } ?: "Fajr"} ${prayerTimesList.firstOrNull()?.time ?: ""}",
                                fontSize = 11.sp,
                                color = SakinaPrimaryFixedDim.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "${if (isArabic) (nextPrayer?.arabicName ?: "") else (nextPrayer?.name ?: "")} ${nextPrayer?.time ?: ""}",
                                fontSize = 11.sp,
                                color = SakinaTertiaryFixed,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${prayerTimesList.lastOrNull()?.let { if (isArabic) it.arabicName else it.name } ?: "Isha"} ${prayerTimesList.lastOrNull()?.time ?: ""}",
                                fontSize = 11.sp,
                                color = SakinaPrimaryFixedDim.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }

        // 3. Smart Spiritual Resume Card (Continue Tadabbur)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateTab(SakinaTab.LEARN) }
                    .testTag("continue_tadabbur_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SakinaSurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(SakinaSecondaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = null,
                                    tint = SakinaSecondary,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CONTINUE TADABBUR",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SakinaSecondary,
                                letterSpacing = 0.8.sp
                            )
                        }

                        Text(
                            text = "3 min left",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Ayat al-Kursi",
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SakinaPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "آية الكرسي",
                                    fontSize = 15.sp,
                                    color = SakinaSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "4 of 9 root words mastered · Al-Qayyum (الْقَيُّومُ)",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Resume button
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(SakinaPrimary)
                                .clickable { onNavigateTab(SakinaTab.LEARN) }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Resume",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    // Progress bar (44%)
                    LinearProgressIndicator(
                        progress = { 0.44f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(50)),
                        color = SakinaSurfaceTint,
                        trackColor = SakinaSurfaceContainer,
                        strokeCap = StrokeCap.Round
                    )
                }
            }
        }

        // 4. Daily Dhikr Goal Ring & Tactile Counter Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dhikr_cadence_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SakinaSurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(SakinaTertiaryFixed),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Adjust,
                                    contentDescription = null,
                                    tint = SakinaTertiaryContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Daily Dhikr Cadence",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = SakinaPrimary
                            )
                        }

                        Text(
                            text = "Goal: 500",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Circular Progress Ring
                        val progressFraction = (dailyCount.toFloat() / 500f).coerceIn(0f, 1f)
                        val animatedFraction by animateFloatAsState(targetValue = progressFraction, label = "dhikrProgress")

                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clickable { onNavigateTab(SakinaTab.TASBIH) },
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val strokeWidth = 8.dp.toPx()
                                // Background circle
                                drawCircle(
                                    color = Color(0xFFE0F0FB),
                                    style = Stroke(width = strokeWidth)
                                )
                                // Active arc
                                drawArc(
                                    color = SakinaPrimaryContainer,
                                    startAngle = -90f,
                                    sweepAngle = animatedFraction * 360f,
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$dailyCount",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SakinaPrimary
                                )
                                Text(
                                    text = "${(progressFraction * 100).toInt()}%",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = SakinaSecondary
                                )
                            }
                        }

                        // Info & Quick Add buttons
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Today's Remembrance",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                val left = (500 - dailyCount).coerceAtLeast(0)
                                Text(
                                    text = if (left > 0) "$left praises to reach your peace goal" else "Goal reached! Al-Hamdulillah",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SakinaPrimary
                                )
                            }

                            // Quick add pills
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SakinaSurfaceContainer)
                                        .clickable {
                                            vibrateShort()
                                            SakinaRepository.incrementDailyDhikr(33)
                                        }
                                        .padding(vertical = 8.dp, horizontal = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, tint = SakinaSurfaceTint, modifier = Modifier.size(13.dp))
                                        Text("33 SubhanAllah", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = SakinaPrimary)
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SakinaSurfaceContainer)
                                        .clickable {
                                            vibrateShort()
                                            SakinaRepository.incrementDailyDhikr(33)
                                        }
                                        .padding(vertical = 8.dp, horizontal = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, tint = SakinaSurfaceTint, modifier = Modifier.size(13.dp))
                                        Text("33 Astaghfirullah", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = SakinaPrimary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Routine Tracker Carousel (Daily Adhkar Journey)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daily Adhkar Journey",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SakinaPrimary
                    )
                    Text(
                        text = "2 of 3 Active",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = SakinaSecondary
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(routines) { routine ->
                        Card(
                            modifier = Modifier
                                .width(200.dp)
                                .clickable { onNavigateTab(SakinaTab.HABITS) },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (routine.isDone) SakinaSurfaceContainerLowest else SakinaSurfaceContainerLowest
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when (routine.iconType) {
                                                    "morning" -> SakinaSecondaryContainer
                                                    "evening" -> SakinaTertiaryFixed
                                                    else -> SakinaSurfaceContainerHigh
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = when (routine.iconType) {
                                                "morning" -> Icons.Default.WbTwilight
                                                "evening" -> Icons.Default.NightsStay
                                                else -> Icons.Default.Bedtime
                                            },
                                            contentDescription = null,
                                            tint = when (routine.iconType) {
                                                "morning" -> SakinaSecondary
                                                "evening" -> SakinaTertiaryContainer
                                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                                            },
                                            modifier = Modifier.size(17.dp)
                                        )
                                    }

                                    if (routine.isDone) {
                                        Row(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(50))
                                                .background(SakinaPrimary.copy(alpha = 0.1f))
                                                .padding(horizontal = 8.dp, vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = SakinaPrimary, modifier = Modifier.size(11.dp))
                                            Text("Done", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SakinaPrimary)
                                        }
                                    } else if (routine.scheduledTime != null) {
                                        Text(routine.scheduledTime, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                                    } else {
                                        Text(
                                            "${routine.completedItems} / ${routine.totalItems} Recited",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SakinaTertiaryContainer,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(50))
                                                .background(SakinaTertiaryFixed.copy(alpha = 0.6f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Column {
                                    Text(
                                        text = routine.title,
                                        fontFamily = FontFamily.Serif,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SakinaPrimary
                                    )
                                    Text(
                                        text = routine.arabicTitle,
                                        fontSize = 12.sp,
                                        color = SakinaSecondary
                                    )
                                }

                                if (routine.isDone) {
                                    LinearProgressIndicator(
                                        progress = { 1f },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(5.dp)
                                            .clip(RoundedCornerShape(50)),
                                        color = SakinaSurfaceTint,
                                        trackColor = SakinaSecondaryContainer
                                    )
                                } else if (routine.scheduledTime != null) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Default.Alarm, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                                        Text("Reminder set", fontSize = 11.sp, color = Color.Gray)
                                    }
                                } else {
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        LinearProgressIndicator(
                                            progress = { routine.completedItems.toFloat() / routine.totalItems },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(5.dp)
                                                .clip(RoundedCornerShape(50)),
                                            color = SakinaPrimary,
                                            trackColor = SakinaSurfaceContainer
                                        )
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(SakinaPrimaryContainer)
                                                .padding(vertical = 5.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                                            ) {
                                                Text("Continue", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
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

        // 6. Verse of the Day (آية اليوم)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("verse_of_the_day_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SakinaSurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(SakinaSecondaryContainer.copy(alpha = 0.8f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = null,
                                    tint = SakinaSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Verse of the Day", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SakinaPrimary)
                                Text("Surah Ar-Ra'd (13:28)", fontSize = 10.sp, color = SakinaSecondary)
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(
                                onClick = { isAyahAudioPlaying = !isAyahAudioPlaying },
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(if (isAyahAudioPlaying) SakinaPrimaryContainer else SakinaSurfaceContainer)
                            ) {
                                Icon(
                                    imageVector = if (isAyahAudioPlaying) Icons.Default.Pause else Icons.Default.VolumeUp,
                                    contentDescription = "Listen",
                                    tint = if (isAyahAudioPlaying) Color.White else SakinaPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            IconButton(
                                onClick = {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "\"Verily, in the remembrance of Allah do hearts find rest.\" (Surah Ar-Ra'd 13:28) - via Sakina App"
                                        )
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share Verse"))
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(SakinaSurfaceContainer)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    tint = SakinaSecondary,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }

                    // Sacred Arabic Calligraphy Display
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SakinaSurfaceContainerLow.copy(alpha = 0.6f))
                            .padding(vertical = 12.dp, horizontal = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Normal,
                            color = SakinaPrimary,
                            textAlign = TextAlign.Center,
                            lineHeight = 44.sp
                        )
                    }

                    // Transliteration
                    Text(
                        text = "“Ala bi-dhikrillahi tatma'innul-qulub”",
                        fontSize = 13.sp,
                        fontStyle = FontStyle.Italic,
                        color = SakinaSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // English Translation
                    Text(
                        text = "“Verily, in the remembrance of Allah do hearts find rest.”",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Action: Reflect / Word-by-Word
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SakinaSurfaceContainer)
                            .clickable { onNavigateTab(SakinaTab.LEARN) }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Translate, contentDescription = null, tint = SakinaSurfaceTint, modifier = Modifier.size(16.dp))
                            Text("Reflect · Word-by-Word Tadabbur", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SakinaSecondary)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = SakinaSecondary, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // 7. Gentle Spiritual Milestone Reflection Banner
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                SakinaSurfaceContainerLow,
                                SakinaSurfaceContainer,
                                SakinaSurfaceContainerLow
                            )
                        )
                    )
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(SakinaSecondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = SakinaSurfaceTint,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "A Tranquil Rhythm",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SakinaPrimary
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(SakinaTertiaryFixed)
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text("Milestone", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SakinaTertiaryContainer)
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "You have cultivated consistent peace for 14 uninterrupted days. No rush, only presence.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
