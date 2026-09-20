# Sakina (سكينة) — Islamic Mindfulness & Spiritual Companion

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Offline First](https://img.shields.io/badge/Architecture-Offline--First%20%7C%20Room-006C4C)](#)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

**Sakina** (*سكينة* — meaning *tranquility and peace of heart*) is an offline-first, modern Android application crafted with Jetpack Compose and Material Design 3. It combines digital mindfulness, sacred scripture contemplation (*Tadabbur*), an adaptive cadence-learning Tasbih, authentic audio Dhikr pronunciation guides, global prayer schedules, and an interactive daily spiritual habit checklist.

---

## ✨ Features

### 📿 1. Immersive Tasbih with Cadence Learning & Full-Screen Mode
- **Whole-Screen Tap Canvas**: Tap anywhere on an OLED-friendly emerald canvas (`#071410`) to count beads without looking at the screen.
- **Smart Rhythm Auto-Increment**:
  - Activates **"Auto Mode (Learn My Cadence)"** where the user taps 4 times at their natural recitation speed.
  - Automatically calculates the intervals and sustains that cadence automatically.
  - Fine-tune speed with `[-0.2s]` and `[+0.2s]` tempo adjusters.
- **Milestone Haptics**: Distinct waveform vibrations for standard beads, 33-bead milestones, and target completions.
- **Lap Tracking & Presets**: Built-in 33, 99, 100, and infinite target modes.

### 🎙️ 2. Authentic Audio Dhikr & Makharij Pronunciation Library
- **Recitation Playback**: Audio playback for sacred phrases (*SubhanAllah, Alhamdulillah, Allahu Akbar, Astaghfirullah, La ilaha illallah, SubhanAllahil 'Azim, La Hawla, and Salawat*).
- **Tajweed & Makharij Articulation**: Anatomical pronunciation notes detailing throat letters (وسط الحلق), nasalization (*Ghunnah*), and elongation (*Madd*).
- **Pace Selector**: Toggle between **1.0x (Normal)** and **0.75x (Slow/Educational)** for focused Tajweed training.
- **Instant Tasbih Loading**: One-tap selection loads the selected Dhikr directly into the counter.

### ✅ 3. Daily Spiritual Habit Checklist & Routine Tracker
- **Spiritual Daily Goals**: Track obligatory prayers, morning/evening Adhkar, Quran recitation, Tahajjud, and Sadaqah.
- **7-Day Streak Visualizer**: Interactive day selector (Mon–Sun) with completion indicators and streak counts.
- **Custom Goal Creation**: Add custom spiritual goals with designated categories and frequencies.

### 🧭 4. Prayer Times & Real-Time Qibla Compass
- **Astronomical Prayer Engine**: Calculates Fajr, Sunrise, Dhuhr, Asr, Maghrib, and Isha using recognized calculation methods (Muslim World League, ISNA, Umm Al-Qura, Egypt, Karachi).
- **Global Location Selector**: Instant switching between global cities (Mecca, Medina, Cairo, Istanbul, London, Jakarta, etc.) or custom coordinates.
- **Live Countdown**: Displays remaining time until the next prayer with color-coded alerts.

### 📖 5. Quranic Tadabbur (Word-by-Word Reflection Studio)
- **Deep Contemplation Cards**: Ayah calligraphy, phonetic transliteration, grammatical roots, and thematic tags.
- **Personal Reflection Notes**: Private journaling space stored locally on-device.

### 🌐 6. Bi-Directional Internationalization (English & Arabic)
- Full Arabic (العربية) and English support with native Right-to-Left (RTL) and Left-to-Right (LTR) Compose layout switching.

### 🛡️ 7. Offline-First Privacy & Backup/Restore
- Zero cloud tracking or mandatory accounts.
- Complete JSON export and restore for streaks, habit logs, and reflection notes.

---

## 🛠 Tech Stack & Architecture

- **Language**: Kotlin 2.0+
- **UI Framework**: Jetpack Compose with Material Design 3 (M3)
- **State Management**: Android `ViewModel`, Kotlin Coroutines, and `MutableStateFlow` / `collectAsStateWithLifecycle`
- **Persistence**: Room Database + Jetpack DataStore
- **Audio Engine**: `MediaPlayer` for authentic audio files with fallback to Android `TextToSpeech` configured with Arabic locale
- **Localization**: Centralized `SakinaLocaleManager` supporting runtime dynamic language switching
- **Astronomical Math**: Custom offline trigonometric prayer time calculator based on solar declination and equation of time
- **Design System**: Emerald Palette (`#006C4C`, `#1B4332`, `#52B788`, `#E9D8A6`) honoring classical Islamic aesthetics

---

## 📂 Project Structure

```
app/src/main/java/com/example/
├── data/
│   ├── audio/
│   │   └── SakinaAudioPlayer.kt          # MediaPlayer & TTS audio engine
│   ├── location/
│   │   └── PrayerCalculator.kt           # Solar prayer time & Qibla math
│   ├── model/
│   │   └── SakinaModels.kt               # DhikrItem, RoutineItem, PrayerTimeInfo
│   └── repository/
│       └── SakinaRepository.kt          # Central reactive StateFlow repository
├── ui/
│   ├── components/
│   │   ├── BottomNavBar.kt               # M3 Navigation bar
│   │   ├── DailyHabitChecklistComponent.kt # Daily spiritual goals checklist
│   │   ├── DhikrAudioLibraryDialog.kt   # Tajweed & authentic audio library
│   │   ├── FullScreenTasbihView.kt       # Fullscreen cadence-learning Tasbih
│   │   ├── LocationSelectorDialog.kt     # City & coordinate selector
│   │   └── BackupRestoreDialog.kt        # JSON export & restore
│   ├── i18n/
│   │   └── SakinaLocaleManager.kt        # Runtime Arabic/English manager
│   ├── screens/
│   │   ├── HomeScreen.kt                 # Daily dashboard & prayer tracker
│   │   ├── TasbihScreen.kt               # Primary interactive counter
│   │   ├── TadabburScreen.kt             # Quranic contemplation studio
│   │   └── HabitsScreen.kt               # Habits & prayer schedule
│   └── theme/
│       ├── Color.kt                      # Sakina Emerald & Sandstone M3 palette
│       └── Theme.kt                      # Dynamic & static MaterialTheme
```

---

## 📋 Antigravity IDE Master Reference Prompt

Copy and paste the prompt below into Google AI Studio Build / Antigravity IDE to recreate or extend this application:

```markdown
Build "Sakina", an offline-first Islamic mindfulness, authentic Dhikr, and spiritual companion Android app using Kotlin and Jetpack Compose (Material 3).

Key Requirements:
1. Visual Design:
   - Palette: Deep Emerald Green (#006C4C, #1B4332), Mint Green (#52B788), Gold Sandstone (#E9D8A6), and Charcoal surfaces.
   - Sacred Typography: Serif calligraphy for Arabic texts, high-contrast clean typography for English, and spacious padding adhering to M3 8.dp grid.
   - Dual-language support: English and Arabic (العربية) with RTL support.

2. Interactive Tasbih Counter:
   - Centered tactile bead dial with circular progress ring and bead milestone haptic vibrations (every 33 counts and on target completion).
   - Full-Screen Immersive Mode (#071410 canvas) allowing user to tap anywhere on the screen to count beads.
   - Auto-Increment Cadence Learning: User taps 3-5 times at their natural pace; the app learns the average interval and automatically increments the count at that rhythm with tempo fine-tuning (-0.2s, +0.2s) and pause/resume controls.

3. Authentic Dhikr Audio Pronunciation Library:
   - Audio playback for core Dhikrs (SubhanAllah, Alhamdulillah, Allahu Akbar, Astaghfirullah, La ilaha illallah, SubhanAllahil 'Azim, La Hawla, Salawat).
   - Tajweed and Makharij articulation notes detailing throat/tongue positions.
   - Playback speed controls (1.0x normal, 0.75x slow pronunciation training).
   - Ability to load any Dhikr directly into the Tasbih counter.

4. Daily Spiritual Habit Checklist:
   - Daily checklist UI for tracking Salah, Morning/Evening Adhkar, Quran recitation, Tahajjud, and Sadaqah.
   - 7-day past week calendar strip showing completion dots and streak counters.
   - Dialog to add custom spiritual goals with categories and frequencies.

5. Offline Prayer Times & Qibla Compass:
   - Astronomical prayer calculator (Fajr, Sunrise, Dhuhr, Asr, Maghrib, Isha) with customizable calculation conventions (MWL, ISNA, Umm Al-Qura, Egypt, Karachi).
   - Location picker dialog with preset world cities (Mecca, Medina, Cairo, Istanbul, London, etc.) and coordinate overrides.
   - Qibla compass angle calculation with visual indicator.

6. Quranic Tadabbur Studio:
   - Ayah reflection cards with Arabic text, transliteration, English meaning, and word-by-word grammatical roots.
   - Personal private reflection notes editor.

7. Architecture & Privacy:
   - 100% offline-first using Kotlin Coroutines, StateFlow, and local JSON backup/restore.
   - Strictly no required account creation or cloud telemetry.
```

---

## 🚀 Building and Running

### Prerequisites
- Android SDK 34+
- JDK 17 or higher
- Gradle (Kotlin DSL)

### Compilation
To compile and verify the build locally:
```bash
gradle :app:compileDebugKotlin
```

### Assemble Debug APK
```bash
gradle :app:assembleDebug
```
The resulting APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 📄 License
Licensed under the [Apache License, Version 2.0](LICENSE).
