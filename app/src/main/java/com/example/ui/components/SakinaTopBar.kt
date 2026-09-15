package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.SakinaRepository
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.SakinaLocaleManager
import com.example.ui.theme.SakinaPrimary
import com.example.ui.theme.SakinaPrimaryContainer
import com.example.ui.theme.SakinaSecondary
import com.example.ui.theme.SakinaSecondaryContainer
import com.example.ui.theme.SakinaSurfaceContainer
import com.example.ui.theme.SakinaTertiaryContainer
import com.example.ui.theme.SakinaTertiaryFixed

@Composable
fun SakinaTopBar(
    subtitle: String,
    onPrayerClick: () -> Unit = {}
) {
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var showBackupDialog by remember { mutableStateOf(false) }
    var showLocationDialog by remember { mutableStateOf(false) }

    val prayerInfo by SakinaRepository.prayerInfo.collectAsState()
    val locationConfig by SakinaRepository.locationConfig.collectAsState()
    val currentLanguage by SakinaLocaleManager.currentLanguage.collectAsState()
    val isArabic = currentLanguage == AppLanguage.ARABIC

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Logo & Brand
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Crescent App Emblem
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(SakinaPrimaryContainer, SakinaPrimary)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "☾",
                        color = SakinaTertiaryFixed,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Sakina",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp,
                            color = SakinaPrimary,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "سكينة",
                            fontSize = 17.sp,
                            color = SakinaSecondary,
                            fontWeight = FontWeight.Normal
                        )
                    }
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Right: Next prayer badge pill, tune settings, profile
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Next Prayer Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(SakinaSecondaryContainer.copy(alpha = 0.7f))
                        .clickable { onPrayerClick() }
                        .padding(horizontal = 9.dp, vertical = 5.dp)
                        .testTag("prayer_time_pill"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Prayer Time",
                        tint = SakinaTertiaryContainer,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "${if (isArabic) prayerInfo.nextPrayerArabic else prayerInfo.nextPrayerName} ${prayerInfo.nextPrayerTime}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Location Quick Selector Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(SakinaPrimary.copy(alpha = 0.08f))
                        .clickable { showLocationDialog = true }
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                        .testTag("location_top_button"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = SakinaPrimary,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = if (isArabic) locationConfig.arabicCityName else locationConfig.cityName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SakinaPrimary
                    )
                }

                // Language Quick Toggle Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(SakinaSecondaryContainer.copy(alpha = 0.7f))
                        .clickable { SakinaLocaleManager.toggleLanguage() }
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                        .testTag("language_toggle_button"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = "Switch Language",
                        tint = SakinaSecondary,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = if (isArabic) "EN" else "عربي",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SakinaPrimary
                    )
                }

                // Data Backup & Restore Quick Button
                IconButton(
                    onClick = { showBackupDialog = true },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("backup_quick_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudSync,
                        contentDescription = "Data Backup & Restore",
                        tint = SakinaSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Tune / Settings Button
                IconButton(
                    onClick = { showSettingsDialog = true },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Settings",
                        tint = SakinaSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Profile Avatar Button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(SakinaPrimary)
                        .clickable { showProfileDialog = true }
                        .testTag("profile_avatar_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }

    // Settings Dialog
    if (showSettingsDialog) {
        var prayerNotification by remember { mutableStateOf(true) }
        var audioAutoPlay by remember { mutableStateOf(true) }
        var hapticVibe by remember { mutableStateOf(true) }

        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Tune, contentDescription = null, tint = SakinaPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sakina Preferences", fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        "Spiritual & Tactile Feedback",
                        fontSize = 12.sp,
                        color = SakinaSecondary,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Prayer Time Callouts", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            Text("Umm al-Qura calculation · Riyadh", fontSize = 11.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = prayerNotification,
                            onCheckedChange = { prayerNotification = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = SakinaPrimary, checkedTrackColor = SakinaSecondaryContainer)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Tactile Tasbih Bead Pulses", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            Text("Gentle buzz on every bead & 33rd", fontSize = 11.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = hapticVibe,
                            onCheckedChange = { hapticVibe = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = SakinaPrimary, checkedTrackColor = SakinaSecondaryContainer)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Audio Pronunciation", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            Text("Sheikh Mishary Rashid Alafasy", fontSize = 11.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = audioAutoPlay,
                            onCheckedChange = { audioAutoPlay = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = SakinaPrimary, checkedTrackColor = SakinaSecondaryContainer)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Language Selection Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(if (isArabic) "لغة التطبيق" else "App Language", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            Text(if (isArabic) "English / العربية (RTL)" else "English / Arabic (RTL)", fontSize = 11.sp, color = Color.Gray)
                        }
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(SakinaSecondaryContainer)
                                .clickable { SakinaLocaleManager.toggleLanguage() }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Translate, contentDescription = null, tint = SakinaPrimary, modifier = Modifier.size(14.dp))
                            Text(
                                text = if (isArabic) "العربية" else "English",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = SakinaPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Location & Calculation Method Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showSettingsDialog = false
                                showLocationDialog = true
                            }
                            .testTag("settings_location_card"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SakinaSecondaryContainer.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(SakinaPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isArabic) "موقع الصلاة وطريقة الحساب" else "Prayer Location & Method",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = SakinaPrimary
                                )
                                Text(
                                    text = "${if (isArabic) locationConfig.arabicCityName else locationConfig.cityName} · ${locationConfig.calculationMethod.displayName}",
                                    fontSize = 11.sp,
                                    color = SakinaSecondary
                                )
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = SakinaPrimary, modifier = Modifier.size(18.dp))
                        }
                    }

                    // Backup & Restore Action Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showSettingsDialog = false
                                showBackupDialog = true
                            }
                            .testTag("settings_backup_restore_card"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SakinaPrimary.copy(alpha = 0.08f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(SakinaPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CloudSync, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Data Backup & Restore", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SakinaPrimary)
                                Text("Export/Import tracking, habits & notes", fontSize = 11.sp, color = SakinaSecondary)
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = SakinaPrimary, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text("Done", color = SakinaPrimary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Profile Dialog
    if (showProfileDialog) {
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(SakinaPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Tariq Al-Mansoor", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
                        Text("Riyadh, Saudi Arabia", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SakinaSecondaryContainer.copy(alpha = 0.6f))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = SakinaPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("14 Day Sakina Streak", fontWeight = FontWeight.Bold, color = SakinaPrimary, fontSize = 13.sp)
                            }
                            Text(
                                "Consistent peace & daily remembrance cultivated uninterruptedly.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    Text("• Daily Dhikr Goal: 500 praises", fontSize = 13.sp)
                    Text("• Quranic Root Words Mastered: 4 / 9", fontSize = 13.sp)
                    Text("• Recited Adhkar Sessions: 84 completed", fontSize = 13.sp)

                    // Profile Export / Restore Data trigger
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showProfileDialog = false
                                showBackupDialog = true
                            },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = SakinaSecondaryContainer)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.CloudSync, contentDescription = null, tint = SakinaPrimary, modifier = Modifier.size(18.dp))
                            Text("Export / Import Tracking Data", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SakinaPrimary)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showProfileDialog = false }) {
                    Text("Close", color = SakinaPrimary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showBackupDialog) {
        BackupRestoreDialog(onDismissRequest = { showBackupDialog = false })
    }

    if (showLocationDialog) {
        LocationSelectorDialog(onDismissRequest = { showLocationDialog = false })
    }
}
