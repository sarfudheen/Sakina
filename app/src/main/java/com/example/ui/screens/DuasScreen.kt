package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.Adjust
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DuaItem
import com.example.data.repository.SakinaRepository
import com.example.ui.components.SakinaTab
import com.example.ui.theme.SakinaError
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

@Composable
fun DuasScreen(
    onNavigateTab: (SakinaTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val duasList by SakinaRepository.duasList.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("all") }
    var currentlyPlayingDuaId by remember { mutableStateOf<String?>(null) }
    var toastInfo by remember { mutableStateOf<Pair<String, Int>?>(null) }

    val categories = listOf(
        "all" to "All (الكل)",
        "morning" to "Morning & Evening (أذكار الصباح والمساء)",
        "anxiety" to "Anxiety & Distress (الهم والحزن)",
        "protection" to "Protection (الحفظ)",
        "forgiveness" to "Forgiveness (الاستغفار)",
        "rizq" to "Rizq & Guidance (الرزق والهداية)",
        "family" to "Family & Parents",
        "sleep" to "Sleep (أذكار النوم)"
    )

    val filteredDuas = duasList.filter { dua ->
        val matchesCategory = selectedCategory == "all" || dua.category.contains(selectedCategory)
        val matchesSearch = searchQuery.isBlank() ||
                dua.title.contains(searchQuery, ignoreCase = true) ||
                dua.translation.contains(searchQuery, ignoreCase = true) ||
                dua.arabic.contains(searchQuery, ignoreCase = true) ||
                dua.transliteration.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    val masterSupplication = duasList.firstOrNull { it.isMasterSupplication }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 48.dp)
        ) {
            // 1. Top Offline Badge & Quick Stats Pill
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(SakinaSecondaryContainer.copy(alpha = 0.7f))
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.OfflinePin, contentDescription = null, tint = SakinaSurfaceTint, modifier = Modifier.size(15.dp))
                        Text("128 Duas cached locally", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = SakinaPrimary)
                    }

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(SakinaSurfaceContainerLow)
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.GraphicEq, contentDescription = null, tint = SakinaTertiaryFixed, modifier = Modifier.size(15.dp))
                        Text("Reciter: Mishary", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = SakinaSecondary)
                    }
                }
            }

            // 2. Search Input Container
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dua-search"),
                    placeholder = {
                        Text(
                            text = "Search by Arabic, English, or feeling ('anxiety', 'rizq')...",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = SakinaSecondary)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Cancel, contentDescription = "Clear", tint = Color.Gray)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SakinaSurfaceContainerLowest,
                        unfocusedContainerColor = SakinaSurfaceContainerLow,
                        focusedBorderColor = SakinaPrimary,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }

            // 3. Horizontally Scrollable Category Pills
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(categories) { (key, label) ->
                        val isSelected = selectedCategory == key
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (isSelected) SakinaPrimary else SakinaSurfaceContainer)
                                .clickable { selectedCategory = key }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else SakinaSecondary
                            )
                        }
                    }
                }
            }

            // 4. Featured Master Dua Bento Card (Sayyid al-Istighfar)
            if (masterSupplication != null && (selectedCategory == "all" || selectedCategory == "forgiveness" || selectedCategory == "morning")) {
                item {
                    val isPlaying = currentlyPlayingDuaId == masterSupplication.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("master_supplication_card"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = SakinaPrimaryContainer),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            SakinaPrimaryContainer,
                                            Color(0xFF1B4332),
                                            SakinaPrimary
                                        )
                                    )
                                )
                                .padding(16.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                // Top row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(SakinaTertiaryContainer)
                                                .padding(horizontal = 7.dp, vertical = 3.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Stars, contentDescription = null, tint = SakinaTertiaryFixed, modifier = Modifier.size(13.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Master Supplication", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SakinaTertiaryFixed)
                                            }
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color.White.copy(alpha = 0.12f))
                                                .padding(horizontal = 7.dp, vertical = 3.dp)
                                        ) {
                                            Text(masterSupplication.reference, fontSize = 10.sp, color = SakinaPrimaryFixed)
                                        }
                                    }

                                    IconButton(
                                        onClick = { SakinaRepository.toggleFavoriteDua(masterSupplication.id) },
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.12f))
                                    ) {
                                        Icon(
                                            imageVector = if (masterSupplication.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                            contentDescription = "Favorite",
                                            tint = if (masterSupplication.isFavorite) SakinaTertiaryFixed else Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                // Title in English & Arabic
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = masterSupplication.title,
                                        fontFamily = FontFamily.Serif,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SakinaPrimaryFixed
                                    )
                                    Text(
                                        text = "سَيِّدُ الاِسْتِغْفَارِ",
                                        fontSize = 18.sp,
                                        color = SakinaTertiaryFixed
                                    )
                                }

                                Text(
                                    text = "The supreme formula of repentance. Recite in morning and evening with firm conviction.",
                                    fontSize = 12.sp,
                                    color = SakinaPrimaryFixedDim
                                )

                                // Excerpt
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.White.copy(alpha = 0.08f))
                                        .padding(12.dp)
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = masterSupplication.arabic.take(120) + "...",
                                            fontSize = 17.sp,
                                            color = Color.White,
                                            textAlign = TextAlign.End,
                                            modifier = Modifier.fillMaxWidth(),
                                            lineHeight = 30.sp
                                        )
                                        Text(
                                            text = "“Allahumma anta Rabbi la ilaha illa Anta, khalaqtani wa ana 'abduka...”",
                                            fontSize = 11.sp,
                                            fontStyle = FontStyle.Italic,
                                            color = SakinaPrimaryFixedDim
                                        )
                                    }
                                }

                                // Action Buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        // Listen button
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(50))
                                                .background(SakinaTertiaryFixed)
                                                .clickable {
                                                    currentlyPlayingDuaId = if (isPlaying) null else masterSupplication.id
                                                }
                                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                    contentDescription = null,
                                                    tint = SakinaTertiaryContainer,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                                Text(
                                                    text = if (isPlaying) "Playing..." else "Listen (0:48)",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = SakinaTertiaryContainer
                                                )
                                            }
                                        }

                                        // Word-by-Word
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(50))
                                                .background(Color.White.copy(alpha = 0.15f))
                                                .clickable { onNavigateTab(SakinaTab.LEARN) }
                                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(Icons.Default.Translate, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                                Text("Word-by-Word", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Medium)
                                            }
                                        }
                                    }

                                    // Quick count 1x pill
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(SakinaPrimaryFixed)
                                            .clickable {
                                                SakinaRepository.sendDuaToTasbih(masterSupplication.title, 1)
                                                toastInfo = masterSupplication.title to 1
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Count", tint = SakinaPrimary, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 5. Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.AutoStories, contentDescription = null, tint = SakinaSecondary, modifier = Modifier.size(18.dp))
                        Text(
                            text = "Supplications & Prophetic Remembrances",
                            fontFamily = FontFamily.Serif,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = SakinaPrimary
                        )
                    }
                    Text("Sorted by Daily Sunnah", fontSize = 10.sp, color = Color.Gray)
                }
            }

            // 6. Dua Cards List
            if (filteredDuas.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(SakinaSurfaceContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoStories, contentDescription = null, tint = SakinaSecondary, modifier = Modifier.size(28.dp))
                        }
                        Text("No Supplications Found", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SakinaPrimary)
                        Text(
                            "Try searching for terms like 'anxiety', 'peace', 'forgiveness', or reset your filters.",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                        Button(
                            onClick = {
                                searchQuery = ""
                                selectedCategory = "all"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SakinaPrimary)
                        ) {
                            Text("View All Duas")
                        }
                    }
                }
            } else {
                items(filteredDuas.filter { !it.isMasterSupplication || searchQuery.isNotBlank() }) { dua ->
                    val isPlaying = currentlyPlayingDuaId == dua.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dua_item_${dua.id}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SakinaSurfaceContainerLowest),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Top Row: Category badges & Favorite
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50))
                                            .background(SakinaSecondaryContainer)
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(dua.categoryDisplay, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SakinaPrimary)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50))
                                            .background(SakinaSurfaceContainerHigh)
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text("Recite: ${dua.recommendedCount}x", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = SakinaSecondary)
                                    }
                                }

                                IconButton(
                                    onClick = { SakinaRepository.toggleFavoriteDua(dua.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = if (dua.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Favorite",
                                        tint = if (dua.isFavorite) SakinaError else Color.Gray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // Title & Reference
                            Column {
                                Text(
                                    text = dua.title,
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(dua.reference, fontSize = 11.sp, color = SakinaSecondary)
                            }

                            // Scripture Box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SakinaSurfaceContainerLow.copy(alpha = 0.6f))
                                    .padding(12.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = dua.arabic,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = SakinaPrimary,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.fillMaxWidth(),
                                        lineHeight = 36.sp
                                    )
                                    Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(SakinaSurfaceContainer))
                                    Text(
                                        text = dua.transliteration,
                                        fontSize = 11.sp,
                                        fontStyle = FontStyle.Italic,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = dua.translation,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 18.sp
                                    )
                                }
                            }

                            // Footprint Actions
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    IconButton(
                                        onClick = {
                                            currentlyPlayingDuaId = if (isPlaying) null else dua.id
                                        },
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(if (isPlaying) SakinaPrimaryContainer else SakinaSurfaceContainer)
                                    ) {
                                        Icon(
                                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.VolumeUp,
                                            contentDescription = "Play",
                                            tint = if (isPlaying) Color.White else SakinaSecondary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            val intent = Intent().apply {
                                                action = Intent.ACTION_SEND
                                                putExtra(Intent.EXTRA_TEXT, "${dua.title}\n\n${dua.arabic}\n\n${dua.translation}\n\n— via Sakina")
                                                type = "text/plain"
                                            }
                                            context.startActivity(Intent.createChooser(intent, "Share Supplication"))
                                        },
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(SakinaSurfaceContainer)
                                    ) {
                                        Icon(Icons.Default.IosShare, contentDescription = "Share", tint = SakinaSecondary, modifier = Modifier.size(15.dp))
                                    }
                                }

                                // Practice in Tasbih button
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(if (dua.recommendedCount > 1) SakinaPrimary else SakinaSecondaryContainer)
                                        .clickable {
                                            SakinaRepository.sendDuaToTasbih(dua.title, dua.recommendedCount)
                                            toastInfo = dua.title to dua.recommendedCount
                                        }
                                        .padding(horizontal = 12.dp, vertical = 7.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Adjust,
                                            contentDescription = null,
                                            tint = if (dua.recommendedCount > 1) Color.White else SakinaPrimary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = if (dua.recommendedCount > 1) "Practice in Tasbih (${dua.recommendedCount}x)" else "Count 1x",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (dua.recommendedCount > 1) Color.White else SakinaPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 7. Interactive Floating Tasbih Handover Toast
        AnimatedVisibility(
            visible = toastInfo != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            if (toastInfo != null) {
                val (title, count) = toastInfo!!
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SakinaPrimary),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
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
                                    .background(SakinaPrimaryFixed),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = SakinaPrimary, modifier = Modifier.size(18.dp))
                            }
                            Column {
                                Text("Target loaded to Tasbih!", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("$title · Goal set to ${count}x", fontSize = 11.sp, color = SakinaPrimaryFixed)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SakinaTertiaryFixed)
                                .clickable {
                                    toastInfo = null
                                    onNavigateTab(SakinaTab.TASBIH)
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Start Now", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SakinaTertiaryContainer)
                        }
                    }
                }
            }
        }
    }
}
