# Sakina — Antigravity IDE Reference Prompt

Use this prompt directly in **Google AI Studio Build / Antigravity IDE** to generate or iterate on this application:

```markdown
Build "Sakina", an offline-first Islamic mindfulness, authentic Dhikr, and spiritual companion Android app using Kotlin and Jetpack Compose (Material Design 3).

Key Requirements:

1. Visual Identity & Aesthetics:
   - Palette: Deep Emerald Green (#006C4C, #1B4332), Mint Green (#52B788), Gold Sandstone (#E9D8A6), and Charcoal surfaces.
   - Sacred Typography: Serif calligraphy for Arabic texts, high-contrast typography for English, and spacious padding adhering to M3 8.dp grid.
   - Dual-language support: English and Arabic (العربية) with dynamic runtime switching and native RTL support.

2. Interactive Tasbih Counter:
   - Centered tactile bead dial with circular progress arc and multi-tiered haptic feedback (per bead, 33-milestone, and target complete).
   - Full-Screen Immersive Mode (#071410 canvas) allowing user to tap anywhere on the screen with one thumb.
   - Auto-Increment Cadence Learning: The user taps 3 to 5 times at their natural recitation speed; the app measures the intervals, calculates the cadence, and automatically increments the count at that rhythm. Includes tempo fine-tuning (-0.2s, +0.2s), pause/resume, and recalibration buttons.

3. Authentic Dhikr Audio Pronunciation Library:
   - Audio playback engine for authentic Dhikrs (SubhanAllah, Alhamdulillah, Allahu Akbar, Astaghfirullah, La ilaha illallah, SubhanAllahil 'Azim, La Hawla, Salawat).
   - Tajweed and Makharij articulation guide detailing anatomical throat/tongue positions.
   - Recitation speed control: 1.0x (normal) vs 0.75x (slow/educational).
   - Direct Tasbih loading: one tap loads any selected Dhikr into the counter.

4. Daily Spiritual Habit Checklist:
   - Daily checklist UI component for tracking obligatory prayers, Morning/Evening Adhkar, Quran recitation, Tahajjud, and Sadaqah.
   - 7-day past week calendar strip with completion dots and continuous streak counters.
   - Custom goal creator with category selection and frequency options.

5. Offline Prayer Times & Qibla Compass:
   - Offline astronomical solar prayer calculator (Fajr, Sunrise, Dhuhr, Asr, Maghrib, Isha) with standard calculation methods (MWL, ISNA, Umm Al-Qura, Egypt, Karachi).
   - Location picker with preset international cities and custom coordinate input.
   - Live prayer countdown timer and Qibla compass bearing.

6. Quranic Tadabbur (Contemplation Studio):
   - Ayah contemplation cards with Arabic script, phonetic transliteration, translation, and word-by-word grammatical roots.
   - Private personal reflection notes editor.

7. Architecture & Privacy:
   - Offline-first architecture using Kotlin Coroutines, StateFlow, and local JSON backup/restore.
   - Zero mandatory cloud accounts or external tracking.
```
