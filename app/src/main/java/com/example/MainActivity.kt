package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.data.repository.SakinaRepository
import com.example.ui.components.SakinaBottomBar
import com.example.ui.components.SakinaTab
import com.example.ui.components.SakinaTopBar
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.SakinaLocaleManager
import com.example.ui.screens.DuasScreen
import com.example.ui.screens.HabitsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LearnScreen
import com.example.ui.screens.TasbihScreen
import com.example.ui.theme.SakinaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SakinaRepository.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            val currentLanguage by SakinaLocaleManager.currentLanguage.collectAsState()
            val layoutDirection = if (currentLanguage.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                SakinaTheme {
                    SakinaApp()
                }
            }
        }
    }
}

@Composable
fun SakinaApp() {
    var currentTab by remember { mutableStateOf(SakinaTab.HOME) }
    val currentLanguage by SakinaLocaleManager.currentLanguage.collectAsState()
    val isArabic = currentLanguage == AppLanguage.ARABIC
    val locationConfig by SakinaRepository.locationConfig.collectAsState()
    val activeCity = if (isArabic) locationConfig.arabicCityName else locationConfig.cityName

    val subtitle = when (currentTab) {
        SakinaTab.HOME -> if (isArabic) "ملاذ روحي وسكينة · $activeCity" else "Spiritual Sanctuary · $activeCity"
        SakinaTab.DUAS -> if (isArabic) "أدعية نبوية مأثورة وحصن المسلم" else "Prophetic Supplications & Protection"
        SakinaTab.TASBIH -> if (isArabic) "تسبيح وذكر واعٍ ومسبحة إلكترونية" else "Mindful Remembrance & Tasbih"
        SakinaTab.LEARN -> if (isArabic) "تدبر قرآني ومعاني الكلمات" else "Tadabbur · Root-by-Root Contemplation"
        SakinaTab.HABITS -> if (isArabic) "عادات روحية وجدول الصلاة اليومي" else "Daily Habits & Prayer Timetable"
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            SakinaTopBar(
                subtitle = subtitle,
                onPrayerClick = { currentTab = SakinaTab.HABITS }
            )
        },
        bottomBar = {
            SakinaBottomBar(
                currentTab = currentTab,
                onTabSelected = { currentTab = it }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Crossfade(
                targetState = currentTab,
                animationSpec = tween(durationMillis = 220),
                label = "tabTransition"
            ) { tab ->
                when (tab) {
                    SakinaTab.HOME -> HomeScreen(onNavigateTab = { currentTab = it })
                    SakinaTab.DUAS -> DuasScreen(onNavigateTab = { currentTab = it })
                    SakinaTab.TASBIH -> TasbihScreen()
                    SakinaTab.LEARN -> LearnScreen(onNavigateTab = { currentTab = it })
                    SakinaTab.HABITS -> HabitsScreen(onNavigateTab = { currentTab = it })
                }
            }
        }
    }
}

