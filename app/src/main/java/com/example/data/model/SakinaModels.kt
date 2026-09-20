package com.example.data.model

data class PrayerTimeInfo(
    val nextPrayerName: String = "Asr",
    val nextPrayerArabic: String = "صَلَاة العَصْر",
    val nextPrayerTime: String = "15:42",
    val countdownMinutes: Int = 48,
    val qiblaDirection: String = "Qibla 242° SW",
    val hijriDate: String = "14 Sha'ban 1446 AH · Riyadh",
    val prevPrayerName: String = "Dhuhr",
    val prevPrayerTime: String = "12:18",
    val upcomingPrayerName: String = "Maghrib",
    val upcomingPrayerTime: String = "18:04",
    val progressPercent: Float = 0.72f
)

data class DhikrItem(
    val id: String,
    val arabic: String,
    val title: String,
    val verseArabic: String,
    val verseTranslit: String,
    val verseMeaning: String,
    val reference: String,
    val defaultTarget: Int = 100,
    val audioUrl: String? = null,
    val makharijTips: String? = null,
    val phoneticBreakdown: String? = null
)

data class WordToken(
    val id: Int,
    val wordArabic: String,
    val transliteration: String,
    val rootArabic: String,
    val rootTranslit: String,
    val literalMeaning: String,
    val contextualMeaning: String,
    val occurrencesCount: Int,
    val verseReferences: List<String>,
    val attributeType: String = "Divine Attribute · اسم فاعل مبالغة",
    val isMastered: Boolean = false
)

data class DuaItem(
    val id: String,
    val title: String,
    val category: String, // "morning", "anxiety", "protection", "forgiveness", "rizq", "family", "sleep"
    val categoryDisplay: String,
    val arabic: String,
    val transliteration: String,
    val translation: String,
    val reference: String,
    val recommendedCount: Int = 1,
    val isFavorite: Boolean = false,
    val isMasterSupplication: Boolean = false,
    val audioDuration: String = "0:48"
)

data class AdhkarRoutine(
    val id: String,
    val title: String,
    val arabicTitle: String,
    val totalItems: Int,
    val completedItems: Int,
    val isDone: Boolean,
    val scheduledTime: String? = null,
    val iconType: String // "morning", "evening", "sleep"
)

data class SinglePrayerTime(
    val name: String,
    val arabicName: String,
    val time: String,
    val isNext: Boolean = false
)

data class SpiritualGoalItem(
    val id: String,
    val title: String,
    val arabicTitle: String,
    val category: String, // "Salah", "Dhikr", "Quran", "Sunnah", "Charity"
    val description: String,
    val iconType: String = "spa",
    val isCustom: Boolean = false
)

