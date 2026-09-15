package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.filled.Splitscreen
import androidx.compose.material.icons.filled.Translate
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WordToken
import com.example.data.repository.SakinaRepository
import com.example.ui.components.SakinaTab
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
fun LearnScreen(
    onNavigateTab: (SakinaTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val personalNotes by SakinaRepository.personalNotes.collectAsState()

    var selectedWord by remember { mutableStateOf(SakinaRepository.wordTokensList.last()) }
    var activeMode by remember { mutableStateOf("word_by_word") }
    var isAudioPlaying by remember { mutableStateOf(false) }
    var audioSpeedIdx by remember { mutableStateOf(1) }
    val audioSpeeds = listOf("0.75x", "1.0x", "1.25x", "1.5x")
    var isLooping by remember { mutableStateOf(false) }
    var isBookmarked by remember { mutableStateOf(false) }
    var showJournalDialog by remember { mutableStateOf(false) }
    var journalInput by remember { mutableStateOf(personalNotes) }
    var isMasteredState by remember { mutableStateOf(selectedWord.isMastered) }

    var selectedLang by remember { mutableStateOf("English") }
    var showLangDropdown by remember { mutableStateOf(false) }
    val languages = listOf("English", "Français", "اردو", "Türkçe", "Bahasa")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp)
    ) {
        // 1. Top Meta Context Strip & Language Selector
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(SakinaSurfaceContainerHigh)
                        .padding(horizontal = 12.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = SakinaTertiaryContainer,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Tadabbur · تدبّر",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Multilingual Selector Pill
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(SakinaSurfaceContainerLowest)
                            .clickable { showLangDropdown = true }
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Translate, contentDescription = null, tint = SakinaSecondary, modifier = Modifier.size(13.dp))
                        Text(selectedLang, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = SakinaPrimary)
                    }

                    DropdownMenu(
                        expanded = showLangDropdown,
                        onDismissRequest = { showLangDropdown = false }
                    ) {
                        languages.forEach { lang ->
                            DropdownMenuItem(
                                text = { Text(lang, fontWeight = if (lang == selectedLang) FontWeight.Bold else FontWeight.Normal) },
                                onClick = {
                                    selectedLang = lang
                                    showLangDropdown = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // 2. Mode Switcher Segmented Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50))
                    .background(SakinaSurfaceContainerHigh)
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Word-by-Word
                ModeSegmentButton(
                    label = "Word-by-Word",
                    icon = Icons.Default.Splitscreen,
                    isSelected = activeMode == "word_by_word",
                    onClick = { activeMode = "word_by_word" },
                    modifier = Modifier.weight(1f)
                )
                // Flowing Recitation
                ModeSegmentButton(
                    label = "Flowing Recitation",
                    icon = Icons.Default.GraphicEq,
                    isSelected = activeMode == "flowing",
                    onClick = { activeMode = "flowing" },
                    modifier = Modifier.weight(1.1f)
                )
                // Root Map
                ModeSegmentButton(
                    label = "Root Map",
                    icon = Icons.Default.Hub,
                    isSelected = activeMode == "root_map",
                    onClick = { activeMode = "root_map" },
                    modifier = Modifier.weight(0.9f)
                )
            }
        }

        // 3. Surah Title & Verse Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Ayat al-Kursi",
                            fontFamily = FontFamily.Serif,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = SakinaPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(SakinaSecondaryContainer)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Verse 255", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SakinaPrimary)
                        }
                    }
                    Text("Surah Al-Baqarah · The Throne Verse", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = { isBookmarked = !isBookmarked },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SakinaSurfaceContainer)
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) SakinaPrimary else SakinaSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            val intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "Contemplating Ayat al-Kursi (2:255) on Sakina App")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Verse"))
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SakinaSurfaceContainer)
                    ) {
                        Icon(Icons.Default.IosShare, contentDescription = "Share", tint = SakinaSecondary, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // 4. Floating Audio Playback Control Strip
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SakinaSurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Top mini bar
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
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(SakinaPrimaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = SakinaPrimaryFixed, modifier = Modifier.size(15.dp))
                            }
                            Column {
                                Text("Mishary Rashid Alafasy", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SakinaPrimary)
                                Text("Hafs an Asim · Murattal", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Speed Pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(SakinaSurfaceContainer)
                                    .clickable {
                                        audioSpeedIdx = (audioSpeedIdx + 1) % audioSpeeds.size
                                    }
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(audioSpeeds[audioSpeedIdx], fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = SakinaPrimary)
                            }

                            // Loop Button
                            IconButton(
                                onClick = { isLooping = !isLooping },
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(if (isLooping) SakinaPrimaryFixed else SakinaSurfaceContainer)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Repeat,
                                    contentDescription = "Loop",
                                    tint = if (isLooping) SakinaPrimary else SakinaSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    // Waveform and Play/Pause Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(SakinaPrimary)
                                .clickable { isAudioPlaying = !isAudioPlaying },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Simulated Interactive Waveform
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .height(28.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            val heights = listOf(8, 14, 20, 12, 18, 24, 16, 22, 12, 18, 14, 10, 20, 12, 8, 16, 10)
                            heights.forEachIndexed { index, h ->
                                val isPlayed = index < 8
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(h.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(
                                            when {
                                                isPlayed && isAudioPlaying -> SakinaPrimary
                                                isPlayed -> SakinaSecondary
                                                else -> SakinaSurfaceContainerHigh
                                            }
                                        )
                                )
                            }
                        }

                        Text(
                            text = if (isAudioPlaying) "00:14 / 00:46" else "00:08 / 00:46",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 5. Sacred Arabic Word Tokens Grid (Interactive Reader)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TOUCH ANY WORD TO INSPECT ROOT",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(SakinaTertiaryFixedDim))
                        Text(
                            text = "Word ${selectedWord.id} of ${SakinaRepository.wordTokensList.size}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SakinaSecondary
                        )
                    }
                }

                // Word Tokens Container
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SakinaSurfaceContainerLowest),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.End),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Display tokens in Right-to-Left Arabic reading order
                        SakinaRepository.wordTokensList.forEach { token ->
                            val isSelected = token.id == selectedWord.id
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) SakinaPrimaryContainer else SakinaSurfaceContainerLow)
                                    .border(
                                        width = if (isSelected) 1.5.dp else 0.dp,
                                        color = if (isSelected) SakinaTertiaryFixedDim else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        selectedWord = token
                                        isMasteredState = token.isMastered
                                    }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                                    .testTag("word_token_${token.id}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = token.wordArabic,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = if (isSelected) Color.White else SakinaPrimary,
                                        lineHeight = 26.sp
                                    )
                                    Text(
                                        text = token.transliteration,
                                        fontSize = 9.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) SakinaPrimaryFixedDim else SakinaSecondary
                                    )
                                }
                            }
                        }

                        // Ayah End Symbol Glyph
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(SakinaSecondaryContainer.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("۝", fontSize = 16.sp, color = SakinaSecondary)
                        }
                    }
                }
            }
        }

        // 6. Active Word Inspection Drawer / Deep Dive Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SakinaSurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Drawer Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SakinaSecondaryContainer.copy(alpha = 0.6f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = selectedWord.wordArabic,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = SakinaPrimary
                                )
                            }

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = selectedWord.transliteration,
                                        fontFamily = FontFamily.Serif,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SakinaPrimary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    IconButton(
                                        onClick = {},
                                        modifier = Modifier.size(22.dp)
                                    ) {
                                        Icon(Icons.Default.VolumeUp, contentDescription = "Pronounce", tint = SakinaSecondary, modifier = Modifier.size(15.dp))
                                    }
                                }
                                Text(
                                    text = selectedWord.attributeType,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Slow Pronunciation Pill
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(SakinaSurfaceContainer)
                                .clickable { }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(Icons.Default.SlowMotionVideo, contentDescription = null, tint = SakinaSecondary, modifier = Modifier.size(13.dp))
                            Text("Slow 0.5x", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    // Root Morphology Segment
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SakinaSurfaceContainerLow)
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ARABIC ROOT (الجذر)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = SakinaSecondary,
                                letterSpacing = 0.8.sp
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(SakinaSurfaceContainerLowest)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = selectedWord.rootArabic,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SakinaPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(selectedWord.rootTranslit, fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Column(modifier = Modifier.weight(1.2f)) {
                            Text(
                                text = "LITERAL MEANING",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = SakinaSecondary,
                                letterSpacing = 0.8.sp
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = selectedWord.literalMeaning,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Contextual Meaning
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "FULL MEANING IN CONTEXT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SakinaSecondary,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = selectedWord.contextualMeaning,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 19.sp
                        )
                    }

                    // Quranic Concordance
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SakinaSurfaceContainer)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, tint = SakinaSecondary, modifier = Modifier.size(14.dp))
                            Text("Appears ${selectedWord.occurrencesCount} times in the Quran", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            selectedWord.verseReferences.take(3).forEach { ref ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(SakinaSurfaceContainerLowest)
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(ref, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SakinaSecondary)
                                }
                            }
                        }
                    }

                    // Action Buttons: Master Root & Dhikr in Tasbih
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Master Root Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isMasteredState) SakinaSecondaryContainer else SakinaPrimary)
                                .clickable {
                                    SakinaRepository.toggleMasteredWord(selectedWord.id)
                                    isMasteredState = !isMasteredState
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = if (isMasteredState) Icons.Default.Check else Icons.Default.BookmarkAdd,
                                    contentDescription = null,
                                    tint = if (isMasteredState) SakinaPrimary else Color.White,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = if (isMasteredState) "Mastered ✓" else "Master Root",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isMasteredState) SakinaPrimary else Color.White
                                )
                            }
                        }

                        // Dhikr in Tasbih Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(SakinaSecondaryContainer)
                                .clickable {
                                    SakinaRepository.sendDuaToTasbih(selectedWord.wordArabic, 33)
                                    onNavigateTab(SakinaTab.TASBIH)
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Adjust, contentDescription = null, tint = SakinaSecondary, modifier = Modifier.size(15.dp))
                                Text("Dhikr in Tasbih", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SakinaPrimary)
                            }
                        }
                    }
                }
            }
        }

        // 7. Authentic Classical Virtue & Tafsir Snippet Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SakinaSurfaceContainerLow),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
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
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(SakinaTertiaryContainer))
                            Text("Authentic Source & Virtue", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SakinaPrimary)
                        }
                        Text("Sahih Bukhari & Muslim", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Text(
                        text = "Whoever recites Ayat al-Kursi after every obligatory prayer, nothing stands between him and entering Paradise except death.",
                        fontSize = 12.sp,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // 8. Contemplation Reflection Note Area
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        journalInput = personalNotes
                        showJournalDialog = true
                    },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SakinaSurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SakinaSurfaceContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.EditNote, contentDescription = null, tint = SakinaSecondary, modifier = Modifier.size(20.dp))
                        }

                        Column {
                            Text("Personal Contemplation Journal", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SakinaPrimary)
                            Text(
                                text = "Tap to view/record your reflection on ${selectedWord.transliteration}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = SakinaSecondary, modifier = Modifier.size(18.dp))
                }
            }
        }
    }

    // Journal Dialog
    if (showJournalDialog) {
        AlertDialog(
            onDismissRequest = { showJournalDialog = false },
            title = {
                Text("Tadabbur Journal", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Reflections on ${selectedWord.transliteration} (${selectedWord.wordArabic}):",
                        fontSize = 12.sp,
                        color = SakinaSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                    OutlinedTextField(
                        value = journalInput,
                        onValueChange = { journalInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        placeholder = { Text("Write your heart's reflection here...") }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        SakinaRepository.updatePersonalNotes(journalInput)
                        showJournalDialog = false
                    }
                ) {
                    Text("Save Reflection", color = SakinaPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showJournalDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
private fun ModeSegmentButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(if (isSelected) SakinaPrimary else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 7.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
