package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.theme.SakinaSurfaceContainer
import com.example.ui.theme.SakinaSurfaceContainerHigh
import com.example.ui.theme.SakinaSurfaceContainerLow
import com.example.ui.theme.SakinaSurfaceContainerLowest
import com.example.ui.theme.SakinaTertiaryFixed

@Composable
fun DhikrAudioLibraryDialog(
    onDismissRequest: () -> Unit,
    onSelectDhikrForTasbih: (DhikrItem) -> Unit
) {
    val context = LocalContext.current
    val currentLanguage by SakinaLocaleManager.currentLanguage.collectAsState()
    val isArabic = currentLanguage == AppLanguage.ARABIC

    val isPlaying by SakinaAudioPlayer.isPlaying.collectAsState()
    val currentPlayingId by SakinaAudioPlayer.currentPlayingId.collectAsState()
    val playbackRate by SakinaAudioPlayer.playbackRate.collectAsState()

    var expandedDhikrId by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = {
            SakinaAudioPlayer.stop()
            onDismissRequest()
        },
        modifier = Modifier.testTag("dhikr_audio_library_dialog"),
        title = {
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
                            .background(SakinaPrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = null,
                            tint = SakinaTertiaryFixed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = if (isArabic) "مكتبة التلاوة والأذكار الصوتية" else "Dhikr Audio Pronunciation",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isArabic) "دليل مخارج الحروف والتدبر الصوتي" else "Authentic recitation & Tajweed guide",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = {
                        SakinaAudioPlayer.stop()
                        onDismissRequest()
                    }
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Playback Speed Controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SakinaSurfaceContainer)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = SakinaSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (isArabic) "سرعة النطق:" else "Recitation Pace:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = playbackRate <= 0.8f,
                            onClick = { SakinaAudioPlayer.setPlaybackRate(0.75f) },
                            label = { Text("0.75x (بطيء / تلاوة)", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SakinaPrimaryContainer,
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = playbackRate > 0.8f,
                            onClick = { SakinaAudioPlayer.setPlaybackRate(1.0f) },
                            label = { Text("1.0x (طبيعي)", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SakinaPrimaryContainer,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                // List of Dhikr Audio Items
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(SakinaRepository.dhikrList, key = { it.id }) { dhikr ->
                        val isCurrentPlaying = isPlaying && currentPlayingId == dhikr.id
                        val isExpanded = expandedDhikrId == dhikr.id

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .border(
                                    width = if (isCurrentPlaying) 1.5.dp else 1.dp,
                                    color = if (isCurrentPlaying) SakinaPrimary else Color(0xFFE2E8F0),
                                    shape = RoundedCornerShape(14.dp)
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCurrentPlaying) SakinaSurfaceContainerHigh else SakinaSurfaceContainerLowest
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Play Button
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(if (isCurrentPlaying) SakinaPrimary else SakinaPrimaryContainer)
                                            .clickable {
                                                SakinaAudioPlayer.playDhikr(context, dhikr)
                                            }
                                            .testTag("play_dhikr_${dhikr.id}"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isCurrentPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = "Play ${dhikr.title}",
                                            tint = if (isCurrentPlaying) SakinaTertiaryFixed else Color.White,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    // Titles and Arabic
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = dhikr.arabic,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Serif,
                                            color = SakinaPrimary
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = dhikr.title,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            if (dhikr.phoneticBreakdown != null) {
                                                Text(
                                                    text = "· ${dhikr.phoneticBreakdown}",
                                                    fontSize = 11.sp,
                                                    color = SakinaSecondary,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                            }
                                        }
                                    }

                                    // Select for Tasbih Counter
                                    IconButton(
                                        onClick = {
                                            onSelectDhikrForTasbih(dhikr)
                                            SakinaAudioPlayer.stop()
                                            onDismissRequest()
                                        },
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(SakinaSurfaceContainer)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.TouchApp,
                                            contentDescription = "Count in Tasbih",
                                            tint = SakinaPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                // Makharij and Tajweed breakdown
                                if (dhikr.makharijTips != null) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(SakinaSecondaryContainer.copy(alpha = 0.5f))
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.Top,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.GraphicEq,
                                                contentDescription = null,
                                                tint = SakinaSecondary,
                                                modifier = Modifier.size(14.dp).padding(top = 2.dp)
                                            )
                                            Text(
                                                text = dhikr.makharijTips,
                                                fontSize = 11.sp,
                                                lineHeight = 15.sp,
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
        },
        confirmButton = {
            TextButton(
                onClick = {
                    SakinaAudioPlayer.stop()
                    onDismissRequest()
                }
            ) {
                Text(if (isArabic) "إغلاق" else "Done", fontWeight = FontWeight.Bold)
            }
        }
    )
}
