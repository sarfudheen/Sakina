package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.location.PrayerCalculator
import com.example.data.location.QiblaInfo
import com.example.data.model.SinglePrayerTime
import com.example.data.repository.SakinaRepository
import com.example.ui.components.BackupRestoreDialog
import com.example.ui.components.DailyHabitChecklistComponent
import com.example.ui.components.LocationSelectorDialog
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.ui.theme.SakinaSurfaceContainerHigh
import com.example.ui.theme.SakinaSurfaceContainerLow
import com.example.ui.theme.SakinaSurfaceContainerLowest
import com.example.ui.theme.SakinaSurfaceTint
import com.example.ui.theme.SakinaTertiaryContainer
import com.example.ui.theme.SakinaTertiaryFixed
import com.example.ui.theme.SakinaTertiaryFixedDim

@Composable
fun HabitsScreen(
    onNavigateTab: (SakinaTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val routines by SakinaRepository.routines.collectAsState()
    val streakDays by SakinaRepository.streakDays.collectAsState()
    val prayerTimes by SakinaRepository.prayerTimesListState.collectAsState()
    val locationConfig by SakinaRepository.locationConfig.collectAsState()
    val prayerInfo by SakinaRepository.prayerInfo.collectAsState()
    val currentLanguage by SakinaLocaleManager.currentLanguage.collectAsState()
    val isArabic = currentLanguage == AppLanguage.ARABIC

    val qibla: QiblaInfo = remember(locationConfig.latitude, locationConfig.longitude) {
        PrayerCalculator.calculateQibla(locationConfig.latitude, locationConfig.longitude)
    }

    var showBackupDialog by remember { mutableStateOf(false) }
    var showLocationDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 40.dp)
    ) {
        // 1. Streak & Hijri Calendar Strip
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("habits_streak_card"),
                shape = RoundedCornerShape(18.dp),
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(SakinaSecondaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Spa, contentDescription = null, tint = SakinaPrimary, modifier = Modifier.size(20.dp))
                            }
                            Column {
                                Text(
                                    text = if (isArabic) "$streakDays أيام متتالية من السكينة" else "$streakDays-Day Sakina Streak",
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SakinaPrimary
                                )
                                Text(
                                    text = "${prayerInfo.hijriDate} · ${if (isArabic) locationConfig.arabicCityName else locationConfig.cityName}",
                                    fontSize = 11.sp,
                                    color = SakinaSecondary
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(SakinaTertiaryFixed)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isArabic) "مستمر" else "Consistent",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SakinaTertiaryContainer
                            )
                        }
                    }

                    // 7-day past week row
                    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        days.forEachIndexed { idx, day ->
                            val isToday = idx == 6
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(day, fontSize = 10.sp, color = if (isToday) SakinaPrimary else Color.Gray, fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal)
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(if (isToday) SakinaPrimary else SakinaSecondaryContainer.copy(alpha = 0.7f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Completed",
                                        tint = if (isToday) SakinaTertiaryFixed else SakinaPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text("${8 + idx}", fontSize = 10.sp, color = SakinaSecondary)
                            }
                        }
                    }
                }
            }
        }

        // Data Backup & Protection Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showBackupDialog = true }
                    .testTag("habits_backup_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SakinaSecondaryContainer.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(SakinaPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Spiritual Progress Protection",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SakinaPrimary
                            )
                            Text(
                                text = "Export or import your streaks, dhikr & notes anytime",
                                fontSize = 11.sp,
                                color = SakinaSecondary
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SakinaPrimary)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("Export / Import", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        // Daily Habit Checklist UI Component
        item {
            DailyHabitChecklistComponent(
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 2. Prayer Schedule & Athan Table
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = SakinaPrimary, modifier = Modifier.size(20.dp))
                            Column {
                                Text(
                                    text = if (isArabic) "مواقيت الصلاة" else "Prayer Times",
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SakinaPrimary
                                )
                                Text(
                                    text = "${if (isArabic) locationConfig.arabicCityName else locationConfig.cityName} · ${locationConfig.calculationMethod.displayName}",
                                    fontSize = 10.sp,
                                    color = SakinaSecondary
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Location Change Button
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(SakinaPrimary.copy(alpha = 0.1f))
                                    .clickable { showLocationDialog = true }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .testTag("habits_location_button"),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = SakinaPrimary, modifier = Modifier.size(12.dp))
                                Text(
                                    text = if (isArabic) "الموقع" else "Location",
                                    fontSize = 10.sp,
                                    color = SakinaPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Qibla Pill
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(SakinaSurfaceContainer)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Explore, contentDescription = null, tint = SakinaSecondary, modifier = Modifier.size(12.dp))
                                Text(
                                    text = "${if (isArabic) "القبلة" else "Qibla"} ${qibla.bearingDegrees}°",
                                    fontSize = 10.sp,
                                    color = SakinaSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Table items
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (prayer in prayerTimes) {
                            val isNext = prayer.isNext
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isNext) SakinaPrimaryContainer else SakinaSurfaceContainerLow)
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = when (prayer.name) {
                                            "Fajr" -> Icons.Default.WbTwilight
                                            "Sunrise" -> Icons.Default.WbSunny
                                            "Dhuhr" -> Icons.Default.WbSunny
                                            "Asr" -> Icons.Default.WbTwilight
                                            "Maghrib" -> Icons.Default.NightsStay
                                            else -> Icons.Default.Bedtime
                                        },
                                        contentDescription = null,
                                        tint = if (isNext) SakinaTertiaryFixed else SakinaSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )

                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = if (isArabic) prayer.arabicName else prayer.name,
                                                fontSize = 14.sp,
                                                fontWeight = if (isNext) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isNext) Color.White else MaterialTheme.colorScheme.onSurface
                                            )
                                            if (isNext) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(SakinaTertiaryFixed)
                                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                                ) {
                                                    Text(
                                                        text = if (isArabic) "القادمة" else "NEXT",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = SakinaTertiaryContainer
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = if (isArabic) prayer.name else prayer.arabicName,
                                            fontSize = 11.sp,
                                            color = if (isNext) SakinaPrimaryFixedDim else SakinaSecondary
                                        )
                                    }
                                }

                                Text(
                                    text = prayer.time,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (isNext) SakinaTertiaryFixed else SakinaPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Daily Adhkar Routines Checklist
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
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
                        Text(
                            text = "Daily Adhkar Habits",
                            fontFamily = FontFamily.Serif,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SakinaPrimary
                        )
                        Text("3 Scheduled", fontSize = 11.sp, color = SakinaSecondary)
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        routines.forEach { routine ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SakinaSurfaceContainerLow)
                                    .clickable {
                                        SakinaRepository.toggleRoutineCompleted(routine.id)
                                    }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(if (routine.isDone) SakinaPrimary else Color.Transparent)
                                            .padding(2.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (routine.isDone) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .clip(CircleShape)
                                                    .background(SakinaSurfaceContainer)
                                            )
                                        }
                                    }

                                    Column {
                                        Text(routine.title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = SakinaPrimary)
                                        Text("${routine.arabicTitle} · ${routine.completedItems}/${routine.totalItems} completed", fontSize = 11.sp, color = SakinaSecondary)
                                    }
                                }

                                if (routine.isDone) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50))
                                            .background(SakinaSecondaryContainer)
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text("Completed", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SakinaPrimary)
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50))
                                            .background(SakinaPrimary)
                                            .clickable { onNavigateTab(SakinaTab.TASBIH) }
                                            .padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Text("Recite", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showBackupDialog) {
        BackupRestoreDialog(onDismissRequest = { showBackupDialog = false })
    }

    if (showLocationDialog) {
        LocationSelectorDialog(onDismissRequest = { showLocationDialog = false })
    }
}
