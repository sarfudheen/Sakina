package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.Adjust
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Mosque
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.SakinaLocaleManager
import com.example.ui.theme.SakinaPrimary
import com.example.ui.theme.SakinaSecondary
import com.example.ui.theme.SakinaTertiaryFixed

enum class SakinaTab(val title: String, val arabicTitle: String = title) {
    HOME("Home", "الرئيسية"),
    DUAS("Duas", "الأدعية"),
    TASBIH("Tasbih", "المسبحة"),
    LEARN("Learn", "تدبر"),
    HABITS("Habits", "العادات")
}

@Composable
fun SakinaBottomBar(
    currentTab: SakinaTab,
    onTabSelected: (SakinaTab) -> Unit
) {
    val currentLanguage by SakinaLocaleManager.currentLanguage.collectAsState()
    val isArabic = currentLanguage == AppLanguage.ARABIC

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(68.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Home
                BottomNavItem(
                    label = if (isArabic) "الرئيسية" else "Home",
                    iconFilled = Icons.Filled.Mosque,
                    iconOutlined = Icons.Outlined.Mosque,
                    isSelected = currentTab == SakinaTab.HOME,
                    onClick = { onTabSelected(SakinaTab.HOME) },
                    testTag = "nav_home"
                )

                // 2. Duas
                BottomNavItem(
                    label = if (isArabic) "الأدعية" else "Duas",
                    iconFilled = Icons.Filled.MenuBook,
                    iconOutlined = Icons.Outlined.MenuBook,
                    isSelected = currentTab == SakinaTab.DUAS,
                    onClick = { onTabSelected(SakinaTab.DUAS) },
                    testTag = "nav_duas"
                )

                // 3. Center Elevated Floating Tasbih
                Box(
                    modifier = Modifier
                        .offset(y = (-14).dp)
                        .size(62.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onTabSelected(SakinaTab.TASBIH) }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .shadow(8.dp, CircleShape)
                                .clip(CircleShape)
                                .background(SakinaPrimary)
                                .testTag("nav_tasbih_center"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Adjust,
                                contentDescription = "Tasbih",
                                tint = if (currentTab == SakinaTab.TASBIH) SakinaTertiaryFixed else Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Text(
                            text = if (isArabic) "المسبحة" else "Tasbih",
                            fontSize = 10.sp,
                            fontWeight = if (currentTab == SakinaTab.TASBIH) FontWeight.Bold else FontWeight.Medium,
                            color = if (currentTab == SakinaTab.TASBIH) SakinaPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // 4. Learn
                BottomNavItem(
                    label = if (isArabic) "تدبر" else "Learn",
                    iconFilled = Icons.Filled.School,
                    iconOutlined = Icons.Outlined.School,
                    isSelected = currentTab == SakinaTab.LEARN,
                    onClick = { onTabSelected(SakinaTab.LEARN) },
                    testTag = "nav_learn"
                )

                // 5. Habits
                BottomNavItem(
                    label = if (isArabic) "العادات" else "Habits",
                    iconFilled = Icons.Filled.CalendarMonth,
                    iconOutlined = Icons.Outlined.CalendarMonth,
                    isSelected = currentTab == SakinaTab.HABITS,
                    onClick = { onTabSelected(SakinaTab.HABITS) },
                    testTag = "nav_habits"
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    iconFilled: ImageVector,
    iconOutlined: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) SakinaPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        label = "navColor"
    )

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isSelected) iconFilled else iconOutlined,
            contentDescription = label,
            tint = iconColor,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = iconColor,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
