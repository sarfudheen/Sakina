package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.repository.SakinaRepository
import com.example.data.storage.ImportSummary
import com.example.data.storage.SakinaBackupManager
import com.example.ui.theme.SakinaPrimary
import com.example.ui.theme.SakinaPrimaryContainer
import com.example.ui.theme.SakinaSecondary
import com.example.ui.theme.SakinaSecondaryContainer
import com.example.ui.theme.SakinaSurfaceContainer
import com.example.ui.theme.SakinaSurfaceContainerLow
import com.example.ui.theme.SakinaSurfaceContainerLowest
import com.example.ui.theme.SakinaTertiaryFixed
import com.example.ui.theme.SakinaTertiaryFixedDim
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BackupRestoreDialog(
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val streakDays by SakinaRepository.streakDays.collectAsState()
    val dailyDhikrCount by SakinaRepository.dailyDhikrCount.collectAsState()
    val routines by SakinaRepository.routines.collectAsState()
    val personalNotes by SakinaRepository.personalNotes.collectAsState()
    val wordTokens by SakinaRepository.wordTokens.collectAsState()
    val lastBackupDate by SakinaRepository.lastBackupDate.collectAsState()

    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }
    var restoredSummary by remember { mutableStateOf<ImportSummary?>(null) }
    var pastedJsonText by remember { mutableStateOf("") }

    // Launcher for saving export backup to user-selected file
    val saveFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            val result = SakinaRepository.writeBackupToUri(context, uri)
            if (result.isSuccess) {
                isError = false
                statusMessage = "Backup safely saved to chosen file!"
                Toast.makeText(context, "Backup saved successfully", Toast.LENGTH_SHORT).show()
            } else {
                isError = true
                statusMessage = "Could not save file: ${result.exceptionOrNull()?.localizedMessage}"
            }
        }
    }

    // Launcher for importing backup from user-selected file
    val openFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val result = SakinaRepository.readBackupFromUri(context, uri)
            if (result.isSuccess) {
                isError = false
                restoredSummary = result.getOrNull()
                statusMessage = "Backup restored successfully!"
                Toast.makeText(context, "Data restored! Alhamdulillah.", Toast.LENGTH_LONG).show()
            } else {
                isError = true
                statusMessage = "Import failed: ${result.exceptionOrNull()?.localizedMessage ?: "Invalid file"}"
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("backup_restore_dialog"),
        shape = RoundedCornerShape(24.dp),
        containerColor = SakinaSurfaceContainerLowest,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SakinaSecondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = SakinaPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = "Data Backup & Portability",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = SakinaPrimary
                    )
                    Text(
                        text = "Secure your spiritual progress & reflections",
                        fontSize = 11.sp,
                        color = SakinaSecondary
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Tab Selection
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = SakinaSurfaceContainerLow,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = SakinaPrimary
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = {
                            selectedTabIndex = 0
                            statusMessage = null
                            isError = false
                        },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text("Export", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }
                        },
                        selectedContentColor = SakinaPrimary,
                        unselectedContentColor = SakinaSecondary
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = {
                            selectedTabIndex = 1
                            statusMessage = null
                            isError = false
                        },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text("Import", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }
                        },
                        selectedContentColor = SakinaPrimary,
                        unselectedContentColor = SakinaSecondary
                    )
                }

                // Status Message Feedback Banner
                AnimatedVisibility(visible = statusMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isError) Color(0xFFFFEBEE) else SakinaSecondaryContainer)
                            .padding(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (isError) Icons.Default.Info else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (isError) Color(0xFFC62828) else SakinaPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = statusMessage ?: "",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isError) Color(0xFFC62828) else SakinaPrimary
                            )
                        }
                    }
                }

                // TAB 0: EXPORT
                if (selectedTabIndex == 0) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SakinaSurfaceContainerLow),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "CURRENT PROGRESS OVERVIEW",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = SakinaSecondary,
                                letterSpacing = 0.8.sp
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("🌿 Presence Streak:", fontSize = 12.sp, color = SakinaPrimary)
                                Text("$streakDays Days", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SakinaPrimary)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("📿 Daily Dhikr Praises:", fontSize = 12.sp, color = SakinaPrimary)
                                Text("$dailyDhikrCount", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SakinaPrimary)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("📖 Mastered Quranic Roots:", fontSize = 12.sp, color = SakinaPrimary)
                                Text("${wordTokens.count { it.isMastered }} of ${wordTokens.size}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SakinaPrimary)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("🤲 Completed Adhkar Habits:", fontSize = 12.sp, color = SakinaPrimary)
                                Text("${routines.count { it.isDone }} of ${routines.size}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SakinaPrimary)
                            }
                            if (personalNotes.isNotBlank()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("📝 Reflection Journal:", fontSize = 12.sp, color = SakinaPrimary)
                                    Text("${personalNotes.length} characters", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SakinaPrimary)
                                }
                            }
                            if (lastBackupDate != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Last exported: $lastBackupDate",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    // Export Option 1: Save to File
                    Button(
                        onClick = {
                            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())
                            saveFileLauncher.launch("sakina_backup_$timeStamp.json")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("export_save_file_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SakinaPrimary)
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Backup File (.json)", fontWeight = FontWeight.SemiBold)
                    }

                    // Export Option 2: Share via Android Share Sheet
                    OutlinedButton(
                        onClick = {
                            val json = SakinaRepository.exportBackupJson()
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Sakina Spiritual Progress Backup")
                                putExtra(Intent.EXTRA_TEXT, json)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Sakina Backup"))
                            isError = false
                            statusMessage = "Backup ready to share or save."
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("export_share_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp), tint = SakinaPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Share via App (Drive, Email, Notes)", color = SakinaPrimary, fontWeight = FontWeight.SemiBold)
                    }

                    // Export Option 3: Copy to Clipboard
                    OutlinedButton(
                        onClick = {
                            val json = SakinaRepository.exportBackupJson()
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Sakina Backup", json)
                            clipboard.setPrimaryClip(clip)
                            isError = false
                            statusMessage = "Backup copied to clipboard!"
                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("export_copy_clipboard_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp), tint = SakinaSecondary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Copy Raw JSON Backup", color = SakinaSecondary, fontWeight = FontWeight.Medium)
                    }
                }

                // TAB 1: IMPORT
                if (selectedTabIndex == 1) {
                    Text(
                        text = "Restore all your spiritual tracking data, streaks, and personal Tadabbur notes from a previous backup.",
                        fontSize = 12.sp,
                        color = SakinaSecondary,
                        lineHeight = 16.sp
                    )

                    // Import Option 1: Pick File
                    Button(
                        onClick = {
                            openFileLauncher.launch("*/*")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("import_select_file_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SakinaPrimary)
                    ) {
                        Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Choose Backup File (.json)", fontWeight = FontWeight.SemiBold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(modifier = Modifier.weight(1f).height(1.dp).background(SakinaSurfaceContainer))
                        Text("  or paste JSON  ", fontSize = 11.sp, color = Color.Gray)
                        Box(modifier = Modifier.weight(1f).height(1.dp).background(SakinaSurfaceContainer))
                    }

                    // Import Option 2: Paste JSON Text
                    OutlinedTextField(
                        value = pastedJsonText,
                        onValueChange = { pastedJsonText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("import_json_textfield"),
                        placeholder = { Text("Paste backup JSON here...", fontSize = 12.sp) },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SakinaPrimary,
                            unfocusedBorderColor = SakinaSurfaceContainer
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Paste from clipboard button
                        OutlinedButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val item = clipboard.primaryClip?.getItemAt(0)
                                val text = item?.text?.toString() ?: ""
                                if (text.isNotBlank()) {
                                    pastedJsonText = text
                                } else {
                                    Toast.makeText(context, "Clipboard is empty", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(16.dp), tint = SakinaSecondary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Paste", fontSize = 12.sp, color = SakinaSecondary)
                        }

                        // Restore button
                        Button(
                            onClick = {
                                if (pastedJsonText.isBlank()) {
                                    isError = true
                                    statusMessage = "Please paste valid backup JSON first."
                                } else {
                                    val result = SakinaRepository.restoreFromJson(pastedJsonText)
                                    if (result.isSuccess) {
                                        isError = false
                                        restoredSummary = result.getOrNull()
                                        statusMessage = "Restored successfully from pasted backup!"
                                        Toast.makeText(context, "Data restored! Alhamdulillah.", Toast.LENGTH_LONG).show()
                                    } else {
                                        isError = true
                                        statusMessage = "Invalid JSON: ${result.exceptionOrNull()?.localizedMessage}"
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SakinaPrimary)
                        ) {
                            Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Restore", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Summary of Restored Data
                    AnimatedVisibility(visible = restoredSummary != null) {
                        restoredSummary?.let { summary ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = SakinaSecondaryContainer.copy(alpha = 0.7f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SakinaPrimary, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Restored Progress Summary", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SakinaPrimary)
                                    }
                                    Text("• Presence Streak: ${summary.streakDays} Days", fontSize = 12.sp, color = SakinaPrimary)
                                    Text("• Daily Dhikr Count: ${summary.dailyDhikr} Praises", fontSize = 12.sp, color = SakinaPrimary)
                                    Text("• Mastered Roots: ${summary.masteredWordsCount} Words", fontSize = 12.sp, color = SakinaPrimary)
                                    Text("• Favorited Duas: ${summary.favoriteCount} Saved", fontSize = 12.sp, color = SakinaPrimary)
                                    Text("• Source Backup Date: ${summary.exportDate}", fontSize = 11.sp, color = SakinaSecondary)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismissRequest,
                modifier = Modifier.testTag("backup_dialog_close_button")
            ) {
                Text(
                    text = "Close",
                    fontWeight = FontWeight.Bold,
                    color = SakinaPrimary,
                    fontSize = 14.sp
                )
            }
        }
    )
}
