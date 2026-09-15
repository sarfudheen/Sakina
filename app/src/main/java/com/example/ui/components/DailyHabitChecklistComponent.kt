package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SpiritualGoalItem
import com.example.data.repository.SakinaRepository
import com.example.ui.theme.SakinaPrimary
import com.example.ui.theme.SakinaPrimaryContainer
import com.example.ui.theme.SakinaSecondary
import com.example.ui.theme.SakinaSecondaryContainer
import com.example.ui.theme.SakinaSurfaceContainer
import com.example.ui.theme.SakinaSurfaceContainerHigh
import com.example.ui.theme.SakinaSurfaceContainerLowest
import com.example.ui.theme.SakinaTertiaryContainer
import com.example.ui.theme.SakinaTertiaryFixed
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class ChecklistDayInfo(
    val dateKey: String,
    val dayName: String,
    val dayNumber: String,
    val fullDisplay: String,
    val isToday: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyHabitChecklistComponent(
    modifier: Modifier = Modifier
) {
    val goals by SakinaRepository.spiritualGoals.collectAsState()
    val dailyCompletions by SakinaRepository.dailyCompletedGoals.collectAsState()
    val todayKey = remember { SakinaRepository.getTodayDateKey() }

    // Generate last 7 days ending with today
    val daysList = remember {
        val list = mutableListOf<ChecklistDayInfo>()
        val cal = Calendar.getInstance()
        val nameSdf = SimpleDateFormat("EEE", Locale.getDefault())
        val numSdf = SimpleDateFormat("d", Locale.getDefault())
        val fullSdf = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
        val keySdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // 6 days ago up to today
        for (i in 6 downTo 0) {
            val c = Calendar.getInstance()
            c.add(Calendar.DAY_OF_YEAR, -i)
            val key = keySdf.format(c.time)
            list.add(
                ChecklistDayInfo(
                    dateKey = key,
                    dayName = nameSdf.format(c.time),
                    dayNumber = numSdf.format(c.time),
                    fullDisplay = fullSdf.format(c.time),
                    isToday = (i == 0)
                )
            )
        }
        list
    }

    var selectedDateKey by remember { mutableStateOf(todayKey) }
    var selectedCategory by remember { mutableStateOf("All") }
    var showAddGoalDialog by remember { mutableStateOf(false) }

    val completedGoalIds = dailyCompletions[selectedDateKey] ?: emptySet()
    val totalGoalsCount = goals.size
    val completedCount = goals.count { completedGoalIds.contains(it.id) }
    val progressFraction = if (totalGoalsCount > 0) completedCount.toFloat() / totalGoalsCount else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "progress"
    )

    val currentDayInfo = daysList.find { it.dateKey == selectedDateKey }
        ?: ChecklistDayInfo(selectedDateKey, "Day", "", selectedDateKey, selectedDateKey == todayKey)

    val categories = listOf("All", "Salah", "Dhikr", "Quran", "Charity", "Sunnah")
    val filteredGoals = goals.filter { goal ->
        if (selectedCategory == "All") true else goal.category.equals(selectedCategory, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("daily_habit_checklist_component"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // --- 1. Date Selector Strip ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SakinaSurfaceContainerLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Daily Spiritual Habit Checklist",
                            fontFamily = FontFamily.Serif,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = SakinaPrimary
                        )
                        Text(
                            text = "Select any day to track & review your devotion",
                            fontSize = 11.sp,
                            color = SakinaSecondary
                        )
                    }

                    if (selectedDateKey == todayKey) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(SakinaTertiaryFixed)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Today", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SakinaTertiaryContainer)
                        }
                    }
                }

                // Horizontal Day Pills
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(daysList) { day ->
                        val isSelected = day.dateKey == selectedDateKey
                        val dayDoneIds = dailyCompletions[day.dateKey] ?: emptySet()
                        val dayDoneCount = goals.count { dayDoneIds.contains(it.id) }
                        val isAllDone = totalGoalsCount > 0 && dayDoneCount == totalGoalsCount
                        val isPartial = dayDoneCount > 0 && !isAllDone

                        val bgColor by animateColorAsState(
                            targetValue = if (isSelected) SakinaPrimary else SakinaSurfaceContainer,
                            label = "dayBg"
                        )
                        val textColor = if (isSelected) Color.White else SakinaPrimary

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(bgColor)
                                .clickable { selectedDateKey = day.dateKey }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .testTag("day_selector_${day.dateKey}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = day.dayName,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White.copy(alpha = 0.85f) else Color.Gray
                                )
                                Text(
                                    text = day.dayNumber,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )

                                // Completion indicator dot or check
                                Box(
                                    modifier = Modifier
                                        .padding(top = 2.dp)
                                        .size(14.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isAllDone) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Done",
                                            tint = if (isSelected) SakinaTertiaryFixed else SakinaPrimary,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    } else if (isPartial) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) SakinaTertiaryFixed else SakinaPrimary)
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(4.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) Color.White.copy(alpha = 0.4f) else Color.LightGray)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 2. Progress Overview for the Selected Day ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SakinaSecondaryContainer.copy(alpha = 0.45f))
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
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = currentDayInfo.fullDisplay,
                                fontFamily = FontFamily.Serif,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = SakinaPrimary
                            )
                            if (currentDayInfo.isToday) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(SakinaPrimary)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("Today", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                        Text(
                            text = "$completedCount of $totalGoalsCount Goals Completed (${(progressFraction * 100).toInt()}%)",
                            fontSize = 12.sp,
                            color = SakinaSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Quick Actions (Mark All / Reset)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = {
                                SakinaRepository.setAllGoalsForDate(selectedDateKey, completed = true)
                            },
                            modifier = Modifier.size(36.dp).testTag("mark_all_done_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = "Mark All Done",
                                tint = SakinaPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                SakinaRepository.setAllGoalsForDate(selectedDateKey, completed = false)
                            },
                            modifier = Modifier.size(36.dp).testTag("reset_day_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.RestartAlt,
                                contentDescription = "Reset Checklist",
                                tint = SakinaSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Smooth Progress Bar
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(50)),
                    color = SakinaPrimary,
                    trackColor = SakinaPrimary.copy(alpha = 0.15f),
                )

                // Spiritual Reflection Callout
                if (progressFraction >= 1.0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SakinaTertiaryFixed.copy(alpha = 0.7f))
                            .padding(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = SakinaTertiaryContainer, modifier = Modifier.size(16.dp))
                            Text(
                                text = "Masha'Allah! All daily spiritual goals completed. May Allah accept your devotion and bless your time with Barakah.",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = SakinaTertiaryContainer
                            )
                        }
                    }
                } else {
                    Text(
                        text = "“The most beloved of deeds to Allah are those that are most consistent, even if small.” — Sahih al-Bukhari",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
        }

        // --- 3. Category Filter Chips ---
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            items(categories) { cat ->
                val isSelected = cat == selectedCategory
                val count = if (cat == "All") goals.size else goals.count { it.category.equals(cat, ignoreCase = true) }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (isSelected) SakinaPrimary else SakinaSurfaceContainer)
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("filter_chip_$cat"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$cat ($count)",
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else SakinaPrimary
                    )
                }
            }
        }

        // --- 4. Checklist Goals Items ---
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            filteredGoals.forEach { goal ->
                val isDone = completedGoalIds.contains(goal.id)

                ChecklistGoalItemCard(
                    goal = goal,
                    isCompleted = isDone,
                    onToggle = {
                        SakinaRepository.toggleSpiritualGoal(selectedDateKey, goal.id)
                    },
                    onDelete = if (goal.isCustom) {
                        { SakinaRepository.deleteCustomGoal(goal.id) }
                    } else null
                )
            }
        }

        // --- 5. Add Custom Spiritual Goal Button ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, SakinaPrimary.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                .clickable { showAddGoalDialog = true }
                .padding(vertical = 12.dp)
                .testTag("add_spiritual_goal_button"),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = SakinaPrimary, modifier = Modifier.size(18.dp))
                Text(
                    text = "Add Custom Spiritual Goal",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SakinaPrimary
                )
            }
        }
    }

    // Dialog for Adding a Custom Goal
    if (showAddGoalDialog) {
        var goalTitle by remember { mutableStateOf("") }
        var goalArabic by remember { mutableStateOf("") }
        var goalCategory by remember { mutableStateOf("Sunnah") }
        var goalDescription by remember { mutableStateOf("") }
        var expandedCategory by remember { mutableStateOf(false) }
        val categoryOptions = listOf("Salah", "Dhikr", "Quran", "Sunnah", "Charity", "Akhlaq")

        AlertDialog(
            onDismissRequest = { showAddGoalDialog = false },
            title = {
                Text(
                    text = "Add Spiritual Goal",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = SakinaPrimary
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = goalTitle,
                        onValueChange = { goalTitle = it },
                        label = { Text("Goal Title (e.g., Tahajjud 2 Rak'ahs)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = goalArabic,
                        onValueChange = { goalArabic = it },
                        label = { Text("Arabic Title (Optional, e.g., قيام الليل)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Category dropdown
                    ExposedDropdownMenuBox(
                        expanded = expandedCategory,
                        onExpandedChange = { expandedCategory = !expandedCategory }
                    ) {
                        OutlinedTextField(
                            value = goalCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Spiritual Dimension") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCategory,
                            onDismissRequest = { expandedCategory = false }
                        ) {
                            categoryOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt) },
                                    onClick = {
                                        goalCategory = opt
                                        expandedCategory = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = goalDescription,
                        onValueChange = { goalDescription = it },
                        label = { Text("Spiritual Intention / Note") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("E.g., Wake up 15 mins before Suhur/Fajr") }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (goalTitle.isNotBlank()) {
                            SakinaRepository.addCustomGoal(
                                title = goalTitle.trim(),
                                arabicTitle = goalArabic.trim(),
                                category = goalCategory,
                                description = goalDescription.trim()
                            )
                            showAddGoalDialog = false
                        }
                    }
                ) {
                    Text("Add Goal", fontWeight = FontWeight.Bold, color = SakinaPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddGoalDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
private fun ChecklistGoalItemCard(
    goal: SpiritualGoalItem,
    isCompleted: Boolean,
    onToggle: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    val cardBg by animateColorAsState(
        targetValue = if (isCompleted) SakinaSecondaryContainer.copy(alpha = 0.35f) else SakinaSurfaceContainerLowest,
        label = "itemBg"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .testTag("goal_item_${goal.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCompleted) 0.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Category/Type Icon Box
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isCompleted) SakinaPrimary else SakinaSurfaceContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getGoalIcon(goal.iconType, goal.category),
                        contentDescription = null,
                        tint = if (isCompleted) Color.White else SakinaPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = goal.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isCompleted) SakinaPrimary else MaterialTheme.colorScheme.onSurface,
                            textDecoration = if (isCompleted) TextDecoration.None else TextDecoration.None
                        )

                        if (goal.arabicTitle.isNotBlank()) {
                            Text(
                                text = "· ${goal.arabicTitle}",
                                fontSize = 12.sp,
                                color = SakinaSecondary,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }

                    Text(
                        text = goal.description,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 14.sp
                    )

                    // Category Pill
                    Box(
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(SakinaSecondaryContainer.copy(alpha = 0.5f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = goal.category,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            color = SakinaPrimary
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onDelete != null) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Goal",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Big Checkmark Button (Touch target >= 48dp)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable(onClick = onToggle)
                        .testTag("checkbox_${goal.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(SakinaPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completed",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, SakinaSecondary, CircleShape)
                        )
                    }
                }
            }
        }
    }
}

private fun getGoalIcon(iconType: String, category: String): ImageVector {
    return when (iconType.lowercase()) {
        "fajr" -> Icons.Default.WbTwilight
        "morning" -> Icons.Default.WbSunny
        "salah" -> Icons.Default.Spa
        "quran" -> Icons.Default.MenuBook
        "tasbih" -> Icons.Default.Spa
        "evening" -> Icons.Default.WbTwilight
        "charity" -> Icons.Default.VolunteerActivism
        "sleep" -> Icons.Default.Bedtime
        else -> when (category.lowercase()) {
            "salah" -> Icons.Default.Spa
            "dhikr" -> Icons.Default.Spa
            "quran" -> Icons.Default.MenuBook
            "charity" -> Icons.Default.VolunteerActivism
            else -> Icons.Default.AutoAwesome
        }
    }
}
