package com.example.data.repository

import android.content.Context
import android.net.Uri
import com.example.data.location.AsrJuristicMethod
import com.example.data.location.CalculationMethod
import com.example.data.location.CityPreset
import com.example.data.location.LocationConfig
import com.example.data.location.PrayerCalculator
import com.example.data.model.AdhkarRoutine
import com.example.data.model.DhikrItem
import com.example.data.model.DuaItem
import com.example.data.model.PrayerTimeInfo
import com.example.data.model.SinglePrayerTime
import com.example.data.model.SpiritualGoalItem
import com.example.data.model.WordToken
import com.example.data.storage.ImportSummary
import com.example.data.storage.RoutineBackupState
import com.example.data.storage.SakinaBackupData
import com.example.data.storage.SakinaBackupManager
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.SakinaLocaleManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object SakinaRepository {

    // Dynamic Location & Prayer Times State
    private val _locationConfig = MutableStateFlow(LocationConfig())
    val locationConfig: StateFlow<LocationConfig> = _locationConfig.asStateFlow()

    private val _prayerTimesList = MutableStateFlow(
        PrayerCalculator.calculateDailyPrayers(Date(), _locationConfig.value)
    )
    val prayerTimesListState: StateFlow<List<SinglePrayerTime>> = _prayerTimesList.asStateFlow()

    val prayerTimesList: List<SinglePrayerTime>
        get() = _prayerTimesList.value

    private val _prayerInfo = MutableStateFlow(
        PrayerCalculator.calculatePrayerInfo(Date(), _locationConfig.value)
    )
    val prayerInfo: StateFlow<PrayerTimeInfo> = _prayerInfo.asStateFlow()

    val currentPrayerInfo: PrayerTimeInfo
        get() = _prayerInfo.value

    val dhikrList: List<DhikrItem> = listOf(
        DhikrItem(
            id = "subhanallah",
            arabic = "سُبْحَانَ اللَّهِ",
            title = "SubhanAllah",
            verseArabic = "سُبْحَانَ ٱللَّٰهِ وَبِحَمْدِهِ",
            verseTranslit = "SubhanAllahi wa bihamdih",
            verseMeaning = "“Glory be to Allah and praise Him”",
            reference = "Sahih Muslim 2691 · 100x sins forgiven",
            defaultTarget = 100,
            phoneticBreakdown = "Sub-ḥaa-nal-laah",
            makharijTips = "Makhraj of Haa (ح): Emitted from the middle of the throat (وسط الحلق) with a crisp, breathy friction, distinct from the chest 'h' (هـ)."
        ),
        DhikrItem(
            id = "alhamdulillah",
            arabic = "الْحَمْدُ لِلَّهِ",
            title = "Alhamdulillah",
            verseArabic = "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ",
            verseTranslit = "Al-hamdu lillahi Rabbil-'alamin",
            verseMeaning = "“All praise is due to Allah alone, Lord of the worlds”",
            reference = "Surah Al-Fatihah 1:2 · Fills the Scales",
            defaultTarget = 33,
            phoneticBreakdown = "Al-ḥam-du lil-laah",
            makharijTips = "Clear vocalization of the initial 'Al' (إظهار قمري). Ensure the middle-throat 'Haa' (ح) is soft and breathy."
        ),
        DhikrItem(
            id = "allahuakbar",
            arabic = "اللَّهُ أَكْبَرُ",
            title = "Allahu Akbar",
            verseArabic = "اللَّهُ أَكْبَرُ كَبِيرًا وَالْحَمْدُ لِلَّهِ كَثِيرًا",
            verseTranslit = "Allahu Akbaru Kabira, walhamdu lillahi kathira",
            verseMeaning = "“Allah is truly the Greatest, abundant praise be to Him”",
            reference = "Sahih Muslim 601 · Gates of Heaven opened",
            defaultTarget = 33,
            phoneticBreakdown = "Al-laa-hu Ak-bar",
            makharijTips = "Heavy Tafkheem (تفخيم) on the Lam in 'Allah'. Soft Hams (همس) whisper on the Kaf (ك) in 'Akbar'."
        ),
        DhikrItem(
            id = "astaghfirullah",
            arabic = "أَسْتَغْفِرُ اللَّهَ",
            title = "Astaghfirullah",
            verseArabic = "أَسْتَغْفِرُ اللَّهَ وَأَتُوبُ إِلَيْهِ",
            verseTranslit = "Astaghfirullah wa atubu ilayh",
            verseMeaning = "“I seek forgiveness of Allah and turn to Him in repentance”",
            reference = "Sahih Al-Bukhari 6307 · 70+ times daily",
            defaultTarget = 100,
            phoneticBreakdown = "As-tagh-fi-rul-laah",
            makharijTips = "Makhraj of Ghayn (غ): Upper throat (أدنى الحلق) with slight gargle resonance; followed by thin Ra (ر) because of Kasrah."
        ),
        DhikrItem(
            id = "lailahaillallah",
            arabic = "لَا إِلٰهَ إِلَّا اللَّهُ",
            title = "La ilaha illallah",
            verseArabic = "لَا إِلٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ",
            verseTranslit = "La ilaha illallahu wahdahu la sharika lah",
            verseMeaning = "“None has the right to be worshipped except Allah alone”",
            reference = "Muwatta Malik · Best prayer of all prophets",
            defaultTarget = 100,
            phoneticBreakdown = "Laaa i-laa-ha il-lal-laah",
            makharijTips = "Elongation (Madd Munfasil 2-4 counts) on 'Lā'. Firm Tashdeed (شدّة) on 'illa'."
        ),
        DhikrItem(
            id = "subhanallah_azim",
            arabic = "سُبْحَانَ اللَّهِ الْعَظِيمِ",
            title = "SubhanAllahil 'Azim",
            verseArabic = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ سُبْحَانَ اللَّهِ الْعَظِيمِ",
            verseTranslit = "SubhanAllahi wa bihamdihi, SubhanAllahil 'Azim",
            verseMeaning = "“Glory be to Allah and His praise; Glory be to Allah the Magnificent”",
            reference = "Sahih Al-Bukhari 6682 · Light on tongue, heavy on scales",
            defaultTarget = 100,
            phoneticBreakdown = "Sub-ḥaa-nal-laa-hil 'A-ẓeem",
            makharijTips = "Makhraj of Ayn (ع) from middle throat; heavy emphatic Zha (ظ) tip of tongue against upper incisors."
        ),
        DhikrItem(
            id = "lahawla",
            arabic = "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
            title = "La Hawla",
            verseArabic = "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ الْعَلِيِّ الْعَظِيمِ",
            verseTranslit = "La hawla wa la quwwata illa billahil 'Aliyyil 'Azim",
            verseMeaning = "“There is no power and no strength except with Allah”",
            reference = "Sahih Al-Bukhari 6384 · A treasure from Paradise",
            defaultTarget = 100,
            phoneticBreakdown = "Laa ḥaw-la wa laa quw-wa-ta il-laa bil-laah",
            makharijTips = "Deep soft Waw (و) diphthong in 'Hawl'; strong Qaf (ق) from back of tongue with Tashdeed on Waw."
        ),
        DhikrItem(
            id = "salawat",
            arabic = "اللَّهُمَّ صَلِّ عَلَىٰ مُحَمَّدٍ",
            title = "Salawat upon the Prophet",
            verseArabic = "اللَّهُمَّ صَلِّ عَلَىٰ مُحَمَّدٍ وَعَلَىٰ آلِ مُحَمَّدٍ",
            verseTranslit = "Allahumma salli 'ala Muhammadin wa 'ala ali Muhammad",
            verseMeaning = "“O Allah, bestow peace and blessings upon Muhammad and his family”",
            reference = "Sunan an-Nasa'i 1297 · 10 blessings and 10 ranks raised",
            defaultTarget = 100,
            phoneticBreakdown = "Al-laa-hum-ma sal-li 'a-laa Mu-ḥam-mad",
            makharijTips = "Strong Ghunnah (nasalization 2 beats) on the Meem (مّ) in Allahumma; emphatic heavy Sad (ص)."
        )
    )

    private val initialWordTokens: List<WordToken> = listOf(
        WordToken(
            id = 1,
            wordArabic = "اللَّهُ",
            transliteration = "Allāh",
            rootArabic = "أ - ل - ه",
            rootTranslit = "(A-L-H)",
            literalMeaning = "God, The Supreme Creator",
            contextualMeaning = "The uniquely supreme Deity to Whom all devotion, praise, and submission are directed.",
            occurrencesCount = 2699,
            verseReferences = listOf("1:1", "2:255", "112:1"),
            attributeType = "Proper Name of God · اسم الجلالة",
            isMastered = true
        ),
        WordToken(
            id = 2,
            wordArabic = "لَا",
            transliteration = "lā",
            rootArabic = "ل - و",
            rootTranslit = "(L-W)",
            literalMeaning = "No / There is no",
            contextualMeaning = "Particle of absolute categorical negation, invalidating any false deities.",
            occurrencesCount = 1726,
            verseReferences = listOf("2:255", "3:18", "47:19"),
            attributeType = "Negative Particle · حرف نفي للجنس",
            isMastered = true
        ),
        WordToken(
            id = 3,
            wordArabic = "إِلَٰهَ",
            transliteration = "ilāha",
            rootArabic = "أ - ل - ه",
            rootTranslit = "(A-L-H)",
            literalMeaning = "Deity worthy of worship",
            contextualMeaning = "Any being or authority that is worshiped, feared, or taken as ultimate protector.",
            occurrencesCount = 111,
            verseReferences = listOf("2:163", "2:255", "20:98"),
            attributeType = "Noun · اسم جنس",
            isMastered = true
        ),
        WordToken(
            id = 4,
            wordArabic = "إِلَّا",
            transliteration = "illā",
            rootArabic = "أ - ل",
            rootTranslit = "(A-L)",
            literalMeaning = "Except / But",
            contextualMeaning = "Particle of definitive exception that affirms divine exclusivity exclusively to Allah.",
            occurrencesCount = 663,
            verseReferences = listOf("2:255", "3:2", "59:22"),
            attributeType = "Exception Particle · أداة استثناء",
            isMastered = true
        ),
        WordToken(
            id = 5,
            wordArabic = "هُوَ",
            transliteration = "huwa",
            rootArabic = "ه - و",
            rootTranslit = "(H-W)",
            literalMeaning = "Him / He",
            contextualMeaning = "The transcendent Singular One, beyond human perception and comprehension.",
            occurrencesCount = 482,
            verseReferences = listOf("2:255", "59:23", "112:1"),
            attributeType = "Pronoun · ضمير منفصل",
            isMastered = false
        ),
        WordToken(
            id = 6,
            wordArabic = "الْحَيُّ",
            transliteration = "al-ḥayyu",
            rootArabic = "ح - ي - ي",
            rootTranslit = "(H-Y-Y)",
            literalMeaning = "The Ever-Living",
            contextualMeaning = "He who possesses eternal, perfect life that has neither beginning nor end, immune to mortality or slumber.",
            occurrencesCount = 5,
            verseReferences = listOf("2:255", "3:2", "20:111", "25:58", "40:65"),
            attributeType = "Divine Attribute · صفة مشبهة",
            isMastered = false
        ),
        WordToken(
            id = 7,
            wordArabic = "الْقَيُّومُ",
            transliteration = "al-qayyūm",
            rootArabic = "ق - و - م",
            rootTranslit = "(Q-W-M)",
            literalMeaning = "To stand upright, sustain, maintain",
            contextualMeaning = "“The Self-Sustaining, The All-Sustaining Provider upon whom all creation depends for every single moment of existence.”",
            occurrencesCount = 3,
            verseReferences = listOf("2:255", "3:2", "20:111"),
            attributeType = "Divine Attribute · اسم فاعل مبالغة",
            isMastered = true
        )
    )

    private val _wordTokensList = MutableStateFlow(initialWordTokens)
    val wordTokens: StateFlow<List<WordToken>> = _wordTokensList.asStateFlow()
    val wordTokensList: List<WordToken> get() = _wordTokensList.value

    private val _duasList = MutableStateFlow<List<DuaItem>>(
        listOf(
            DuaItem(
                id = "sayyid_istighfar",
                title = "Sayyid al-Istighfar",
                category = "forgiveness morning",
                categoryDisplay = "Master Supplication",
                arabic = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ",
                transliteration = "“Allahumma anta Rabbi la ilaha illa Anta, khalaqtani wa ana 'abduka, wa ana 'ala 'ahdika wa wa'dika mastata'tu, a'udhu bika min sharri ma sana'tu, abu'u laka bini'matika 'alayya, wa abu'u bidhanbi faghfir li fa'innahu la yaghfirudh-dhunuba illa Anta.”",
                translation = "O Allah, You are my Lord, none has the right to be worshiped except You. You created me and I am Your servant, and I abide by Your covenant and promise as best I can. I seek refuge in You from the evil of what I have done. I acknowledge Your favors upon me and I confess my sins, so forgive me, for none can forgive sins except You.",
                reference = "Sahih al-Bukhari 6306 · Sahih",
                recommendedCount = 1,
                isFavorite = true,
                isMasterSupplication = true,
                audioDuration = "0:48"
            ),
            DuaItem(
                id = "anxiety_grief",
                title = "Dua for Anxiety & Grief",
                category = "anxiety",
                categoryDisplay = "Mental Peace",
                arabic = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْهَمِّ وَالْحَزَنِ، وَالْعَجْزِ وَالْكَسَلِ، وَالْبُخْلِ وَالْجُبْنِ، وَضَلَعِ الدَّيْنِ، وَغَلَبَةِ الرِّجَالِ",
                transliteration = "“Allahumma inni a'udhu bika minal-hammi wal-hazan, wal-'ajzi wal-kasal, wal-bukhli wal-jubn, wa dala'id-dayni wa ghalabatir-rijal.”",
                translation = "O Allah, I seek refuge in You from grief and sadness, from weakness and laziness, from miserliness and cowardice, from being overcome by debt and overwhelmed by men.",
                reference = "Sahih al-Bukhari 2893 · Grade: Sahih",
                recommendedCount = 1,
                isFavorite = false,
                audioDuration = "0:25"
            ),
            DuaItem(
                id = "protection_harm",
                title = "Protection from Every Harm",
                category = "protection morning",
                categoryDisplay = "Morning & Evening",
                arabic = "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ",
                transliteration = "“Bismillahil-ladhi la yadurru ma'as-mihi shay'un fil-ardi wa la fis-sama'i wa Huwas-Sami'ul-'Alim.”",
                translation = "In the Name of Allah, with Whose Name nothing can cause harm in the earth nor in the heavens, and He is the All-Hearing, the All-Knowing.",
                reference = "Sunan Abi Dawud 5088 · Grade: Sahih",
                recommendedCount = 3,
                isFavorite = false,
                audioDuration = "0:20"
            ),
            DuaItem(
                id = "goodness_world_hereafter",
                title = "Goodness in This World & Hereafter",
                category = "rizq",
                categoryDisplay = "Surah Al-Baqarah 2:201",
                arabic = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
                transliteration = "“Rabbana atina fid-dunya hasanatan wa fil-akhirati hasanatan wa qina 'adhaban-nar.”",
                translation = "Our Lord, give us in this world that which is good and in the Hereafter that which is good, and save us from the punishment of the Fire.",
                reference = "The Most Frequent Supplication of the Prophet ﷺ",
                recommendedCount = 7,
                isFavorite = false,
                audioDuration = "0:18"
            ),
            DuaItem(
                id = "seeking_pardon",
                title = "Seeking Constant Pardon",
                category = "forgiveness",
                categoryDisplay = "Forgiveness (الاستغفار)",
                arabic = "أَسْتَغْفِرُ اللَّهَ وَأَتُوبُ إِلَيْهِ",
                transliteration = "“Astaghfirullaha wa atubu ilayh.”",
                translation = "I seek the forgiveness of Allah and turn to Him in sincere repentance.",
                reference = "Sahih Muslim 2702 · Grade: Sahih",
                recommendedCount = 33,
                isFavorite = true,
                audioDuration = "0:15"
            ),
            DuaItem(
                id = "parents_dua",
                title = "Supplication for Parents",
                category = "family",
                categoryDisplay = "Surah Al-Isra 17:24",
                arabic = "رَّبِّ ارْحَمْهُمَا كَمَا رَبَّيَانِي صَغِيرًا",
                transliteration = "“Rabbir-hamhuma kama rabbayani saghira.”",
                translation = "My Lord, have mercy upon them as they brought me up when I was small.",
                reference = "Noble Quran 17:24",
                recommendedCount = 3,
                isFavorite = false,
                audioDuration = "0:14"
            ),
            DuaItem(
                id = "sleep_dua",
                title = "Before Sleeping",
                category = "sleep",
                categoryDisplay = "Sleep (أذكار النوم)",
                arabic = "بِاسْمِكَ رَبِّي وَضَعْتُ جَنْبِي وَبِكَ أَرْفَعُهُ، فَإِنْ أَمْسَكْتَ نَفْسِي فَارْحَمْهَا، وَإِنْ أَرْسَلْتَهَا فَاحْفَظْهَا بِمَا تَحْفَظُ بِهِ عِبَادَكَ الصَّالِحِينَ",
                transliteration = "“Bismika Rabbi wada'tu janbi wa bika arfa'uh, fa in amsakta nafsi farhamha, wa in arsaltaha fahfazha bima tahfazu bihi 'ibadakas-salihin.”",
                translation = "In Your Name, my Lord, I lay down my side and with Your Name I raise it. If You take my soul, have mercy upon it, and if You send it back, protect it as You protect Your righteous slaves.",
                reference = "Sahih al-Bukhari 6320",
                recommendedCount = 1,
                isFavorite = false,
                audioDuration = "0:32"
            )
        )
    )
    val duasList: StateFlow<List<DuaItem>> = _duasList.asStateFlow()

    // Daily Adhkar Routines
    private val _routines = MutableStateFlow<List<AdhkarRoutine>>(
        listOf(
            AdhkarRoutine(
                id = "morning",
                title = "Morning Adhkar",
                arabicTitle = "أذكار الصباح",
                totalItems = 24,
                completedItems = 24,
                isDone = true,
                iconType = "morning"
            ),
            AdhkarRoutine(
                id = "evening",
                title = "Evening Adhkar",
                arabicTitle = "أذكار المساء",
                totalItems = 24,
                completedItems = 14,
                isDone = false,
                iconType = "evening"
            ),
            AdhkarRoutine(
                id = "sleep",
                title = "Sleep Adhkar",
                arabicTitle = "أذكار النوم",
                totalItems = 12,
                completedItems = 0,
                isDone = false,
                scheduledTime = "22:30",
                iconType = "sleep"
            )
        )
    )
    val routines: StateFlow<List<AdhkarRoutine>> = _routines.asStateFlow()

    // Daily Dhikr Total Count State
    private val _dailyDhikrCount = MutableStateFlow(412)
    val dailyDhikrCount: StateFlow<Int> = _dailyDhikrCount.asStateFlow()

    // Active Tasbih Session State
    private val _currentDhikr = MutableStateFlow(dhikrList[0])
    val currentDhikr: StateFlow<DhikrItem> = _currentDhikr.asStateFlow()

    private val _tasbihCount = MutableStateFlow(67)
    val tasbihCount: StateFlow<Int> = _tasbihCount.asStateFlow()

    private val _tasbihTarget = MutableStateFlow<Int?>(100)
    val tasbihTarget: StateFlow<Int?> = _tasbihTarget.asStateFlow()

    private val _tasbihLap = MutableStateFlow(2)
    val tasbihLap: StateFlow<Int> = _tasbihLap.asStateFlow()

    private val _totalLaps = MutableStateFlow(3)
    val totalLaps: StateFlow<Int> = _totalLaps.asStateFlow()

    private val _soundEnabled = MutableStateFlow(true)
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _hapticEnabled = MutableStateFlow(true)
    val hapticEnabled: StateFlow<Boolean> = _hapticEnabled.asStateFlow()

    private val _personalNotes = MutableStateFlow(
        "Al-Qayyum: Contemplate how every heartbeat, breath, and atom in the cosmos is held together by His active sustenance. Rely only on Him."
    )
    val personalNotes: StateFlow<String> = _personalNotes.asStateFlow()

    // Habits & Streaks
    private val _streakDays = MutableStateFlow(14)
    val streakDays: StateFlow<Int> = _streakDays.asStateFlow()

    private val _dailyDhikrGoal = MutableStateFlow(500)
    val dailyDhikrGoal: StateFlow<Int> = _dailyDhikrGoal.asStateFlow()

    // Default Spiritual Goals
    val defaultSpiritualGoals: List<SpiritualGoalItem> = listOf(
        SpiritualGoalItem(
            id = "fajr_prayer",
            title = "Fajr Prayer on time",
            arabicTitle = "صلاة الفجر في وقتها",
            category = "Salah",
            description = "Start your day under the divine covenant & light of Allah.",
            iconType = "fajr"
        ),
        SpiritualGoalItem(
            id = "morning_adhkar",
            title = "Morning Adhkar",
            arabicTitle = "أذكار الصباح",
            category = "Dhikr",
            description = "Recite the prophetic fortress of remembrance and gratitude.",
            iconType = "morning"
        ),
        SpiritualGoalItem(
            id = "dhuhr_asr_prayers",
            title = "Dhuhr & Asr in Khushu",
            arabicTitle = "صلاتي الظهر والعصر بخشوع",
            category = "Salah",
            description = "Pause worldly work to realign heart with your Creator.",
            iconType = "salah"
        ),
        SpiritualGoalItem(
            id = "quran_tadabbur",
            title = "Quran Reading & Tadabbur",
            arabicTitle = "ورد تدبر القرآن الكريم",
            category = "Quran",
            description = "Read with reflection — even one page or several ayat.",
            iconType = "quran"
        ),
        SpiritualGoalItem(
            id = "daily_tasbih_100",
            title = "100x Daily Dhikr",
            arabicTitle = "ورد التسبيح والاستغفار",
            category = "Dhikr",
            description = "100x SubhanAllah wa bihamdihi or Astaghfirullah.",
            iconType = "tasbih"
        ),
        SpiritualGoalItem(
            id = "evening_adhkar",
            title = "Evening Adhkar",
            arabicTitle = "أذكار المساء",
            category = "Dhikr",
            description = "Shield the soul as dusk arrives with prophetic adhkar.",
            iconType = "evening"
        ),
        SpiritualGoalItem(
            id = "maghrib_isha_prayers",
            title = "Maghrib & Isha Prayers",
            arabicTitle = "صلاتي المغرب والعشاء",
            category = "Salah",
            description = "Conclude the five daily obligations in congregation or tranquility.",
            iconType = "salah"
        ),
        SpiritualGoalItem(
            id = "daily_sadaqah",
            title = "Daily Sadaqah or Act of Ihsan",
            arabicTitle = "صدقة يومية أو إحسان",
            category = "Charity",
            description = "Charity, a genuine smile, helping family, or removing harm.",
            iconType = "charity"
        ),
        SpiritualGoalItem(
            id = "witr_sleep_adhkar",
            title = "Witr Prayer & Sleep Adhkar",
            arabicTitle = "صلاة الوتر وأذكار النوم",
            category = "Sunnah",
            description = "Ayat al-Kursi, Surah Al-Mulk, and seal night with Witr.",
            iconType = "sleep"
        )
    )

    private val _spiritualGoals = MutableStateFlow<List<SpiritualGoalItem>>(defaultSpiritualGoals)
    val spiritualGoals: StateFlow<List<SpiritualGoalItem>> = _spiritualGoals.asStateFlow()

    fun getTodayDateKey(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun getDateKeyForOffset(daysAgo: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -daysAgo)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(cal.time)
    }

    // Map of dateKey ("yyyy-MM-dd") -> Set of completed goal IDs
    private val _dailyCompletedGoals = MutableStateFlow<Map<String, Set<String>>>(
        mapOf(
            getTodayDateKey() to setOf("fajr_prayer", "morning_adhkar", "dhuhr_asr_prayers", "quran_tadabbur", "daily_tasbih_100"),
            getDateKeyForOffset(1) to setOf("fajr_prayer", "morning_adhkar", "dhuhr_asr_prayers", "quran_tadabbur", "daily_tasbih_100", "evening_adhkar", "maghrib_isha_prayers", "witr_sleep_adhkar"),
            getDateKeyForOffset(2) to setOf("fajr_prayer", "morning_adhkar", "dhuhr_asr_prayers", "quran_tadabbur", "evening_adhkar", "maghrib_isha_prayers", "daily_sadaqah", "witr_sleep_adhkar"),
            getDateKeyForOffset(3) to setOf("fajr_prayer", "morning_adhkar", "dhuhr_asr_prayers", "quran_tadabbur", "daily_tasbih_100", "evening_adhkar", "maghrib_isha_prayers"),
            getDateKeyForOffset(4) to setOf("fajr_prayer", "morning_adhkar", "dhuhr_asr_prayers", "quran_tadabbur", "daily_tasbih_100", "evening_adhkar", "maghrib_isha_prayers", "witr_sleep_adhkar", "daily_sadaqah"),
            getDateKeyForOffset(5) to setOf("fajr_prayer", "morning_adhkar", "quran_tadabbur", "evening_adhkar", "maghrib_isha_prayers", "witr_sleep_adhkar"),
            getDateKeyForOffset(6) to setOf("fajr_prayer", "morning_adhkar", "dhuhr_asr_prayers", "daily_tasbih_100", "evening_adhkar", "maghrib_isha_prayers", "witr_sleep_adhkar")
        )
    )
    val dailyCompletedGoals: StateFlow<Map<String, Set<String>>> = _dailyCompletedGoals.asStateFlow()

    fun toggleSpiritualGoal(dateKey: String, goalId: String) {
        _dailyCompletedGoals.update { currentMap ->
            val mutable = currentMap.toMutableMap()
            val existingSet = (mutable[dateKey] ?: emptySet()).toMutableSet()
            if (existingSet.contains(goalId)) {
                existingSet.remove(goalId)
            } else {
                existingSet.add(goalId)
            }
            mutable[dateKey] = existingSet
            mutable
        }
        saveToStorage()
    }

    fun setAllGoalsForDate(dateKey: String, completed: Boolean) {
        _dailyCompletedGoals.update { currentMap ->
            val mutable = currentMap.toMutableMap()
            if (completed) {
                mutable[dateKey] = _spiritualGoals.value.map { it.id }.toSet()
            } else {
                mutable[dateKey] = emptySet()
            }
            mutable
        }
        saveToStorage()
    }

    fun addCustomGoal(title: String, arabicTitle: String, category: String, description: String) {
        val newGoal = SpiritualGoalItem(
            id = "custom_${System.currentTimeMillis()}",
            title = title.ifBlank { "Spiritual Goal" },
            arabicTitle = arabicTitle.ifBlank { "هدف روحي" },
            category = category.ifBlank { "Sunnah" },
            description = description.ifBlank { "Daily spiritual practice for Allah's pleasure." },
            iconType = "spa",
            isCustom = true
        )
        _spiritualGoals.update { it + newGoal }
        saveToStorage()
    }

    fun deleteCustomGoal(goalId: String) {
        _spiritualGoals.update { list -> list.filterNot { it.id == goalId && it.isCustom } }
        _dailyCompletedGoals.update { map ->
            map.mapValues { (_, set) -> set.filterNot { it == goalId }.toSet() }
        }
        saveToStorage()
    }

    private val _lastBackupDate = MutableStateFlow<String?>(null)
    val lastBackupDate: StateFlow<String?> = _lastBackupDate.asStateFlow()

    private var appContext: Context? = null
    private const val PREFS_NAME = "sakina_tracking_prefs"
    private const val KEY_BACKUP_JSON = "saved_backup_data"

    fun init(context: Context) {
        appContext = context.applicationContext
        SakinaLocaleManager.init(context)
        loadFromStorage()
        refreshPrayerTimes()
    }

    private fun saveToStorage() {
        val ctx = appContext ?: return
        try {
            val prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val json = exportBackupJson()
            prefs.edit().putString(KEY_BACKUP_JSON, json).apply()
        } catch (_: Exception) {
            // Non-blocking fallback
        }
    }

    private fun loadFromStorage() {
        val ctx = appContext ?: return
        try {
            val prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val savedJson = prefs.getString(KEY_BACKUP_JSON, null)
            if (!savedJson.isNullOrEmpty()) {
                val result = SakinaBackupManager.parseBackupJson(savedJson)
                result.getOrNull()?.let { applyBackupData(it) }
            }
        } catch (_: Exception) {
            // Non-blocking fallback
        }
    }

    fun updatePersonalNotes(note: String) {
        _personalNotes.value = note
        saveToStorage()
    }

    fun toggleMasteredWord(wordId: Int) {
        _wordTokensList.update { list ->
            list.map { token ->
                if (token.id == wordId) token.copy(isMastered = !token.isMastered) else token
            }
        }
        saveToStorage()
    }

    fun incrementDailyDhikr(amount: Int) {
        _dailyDhikrCount.update { it + amount }
        saveToStorage()
    }

    fun incrementTasbih() {
        _tasbihCount.update { it + 1 }
        _dailyDhikrCount.update { it + 1 }
        val target = _tasbihTarget.value
        if (target != null && _tasbihCount.value >= target) {
            if (_tasbihLap.value < _totalLaps.value) {
                _tasbihLap.update { it + 1 }
                _tasbihCount.value = 0
            }
        }
        saveToStorage()
    }

    fun decrementTasbih() {
        if (_tasbihCount.value > 0) {
            _tasbihCount.update { it - 1 }
            if (_dailyDhikrCount.value > 0) {
                _dailyDhikrCount.update { it - 1 }
            }
            saveToStorage()
        }
    }

    fun resetTasbih() {
        _tasbihCount.value = 0
        saveToStorage()
    }

    fun setDhikr(dhikr: DhikrItem) {
        _currentDhikr.value = dhikr
        _tasbihTarget.value = dhikr.defaultTarget
        _tasbihCount.value = 0
        _tasbihLap.value = 1
        saveToStorage()
    }

    fun setTarget(target: Int?) {
        _tasbihTarget.value = target
        saveToStorage()
    }

    fun toggleSound() {
        _soundEnabled.update { !it }
        saveToStorage()
    }

    fun toggleHaptic() {
        _hapticEnabled.update { !it }
        saveToStorage()
    }

    fun toggleFavoriteDua(duaId: String) {
        _duasList.update { list ->
            list.map { dua ->
                if (dua.id == duaId) dua.copy(isFavorite = !dua.isFavorite) else dua
            }
        }
        saveToStorage()
    }

    fun sendDuaToTasbih(title: String, targetCount: Int) {
        val matchingDhikr = dhikrList.find { it.title.contains(title, ignoreCase = true) }
        if (matchingDhikr != null) {
            _currentDhikr.value = matchingDhikr
        } else {
            _currentDhikr.value = DhikrItem(
                id = "custom_" + System.currentTimeMillis(),
                arabic = "ذِكْرٌ مُبَارَك",
                title = title,
                verseArabic = title,
                verseTranslit = title,
                verseMeaning = "“Mindful Supplication & Dhikr”",
                reference = "Prophetic Sunnah",
                defaultTarget = targetCount
            )
        }
        _tasbihTarget.value = targetCount
        _tasbihCount.value = 0
        _tasbihLap.value = 1
        saveToStorage()
    }

    fun selectPresetCity(city: CityPreset) {
        _locationConfig.update {
            it.copy(
                cityName = city.name,
                arabicCityName = city.arabicName,
                countryName = city.country,
                latitude = city.latitude,
                longitude = city.longitude,
                timezoneId = city.timezoneId,
                calculationMethod = city.recommendedMethod,
                isAutoGps = false
            )
        }
        refreshPrayerTimes()
        saveToStorage()
    }

    fun setCalculationMethod(method: CalculationMethod) {
        _locationConfig.update { it.copy(calculationMethod = method) }
        refreshPrayerTimes()
        saveToStorage()
    }

    fun setAsrJuristicMethod(method: AsrJuristicMethod) {
        _locationConfig.update { it.copy(asrJuristicMethod = method) }
        refreshPrayerTimes()
        saveToStorage()
    }

    fun updateWithGpsLocation(lat: Double, lng: Double, cityName: String = "GPS Location") {
        _locationConfig.update {
            it.copy(
                cityName = cityName,
                arabicCityName = "موقع GPS",
                latitude = lat,
                longitude = lng,
                timezoneId = TimeZone.getDefault().id,
                isAutoGps = true
            )
        }
        refreshPrayerTimes()
        saveToStorage()
    }

    fun updateLocation(config: LocationConfig) {
        _locationConfig.value = config
        refreshPrayerTimes()
        saveToStorage()
    }

    fun refreshPrayerTimes() {
        val now = Date()
        val config = _locationConfig.value
        _prayerTimesList.value = PrayerCalculator.calculateDailyPrayers(now, config)
        _prayerInfo.value = PrayerCalculator.calculatePrayerInfo(now, config)
    }

    fun toggleRoutineCompleted(routineId: String) {
        toggleRoutineItem(routineId)
    }

    fun toggleRoutineItem(routineId: String) {
        _routines.update { list ->
            list.map { r ->
                if (r.id == routineId) {
                    if (r.isDone) {
                        r.copy(isDone = false, completedItems = 0)
                    } else {
                        r.copy(isDone = true, completedItems = r.totalItems)
                    }
                } else r
            }
        }
        saveToStorage()
    }

    // --- Export & Import Capabilities ---

    fun getBackupData(): SakinaBackupData {
        val routinesBackup = _routines.value.map {
            RoutineBackupState(
                id = it.id,
                title = it.title,
                completedItems = it.completedItems,
                totalItems = it.totalItems,
                isDone = it.isDone
            )
        }
        val favs = _duasList.value.filter { it.isFavorite }.map { it.id }
        val mastered = _wordTokensList.value.filter { it.isMastered }.map { it.id }
        val dailyGoalsMap = _dailyCompletedGoals.value.mapValues { it.value.toList() }
        val customGoalsList = _spiritualGoals.value.filter { it.isCustom }

        return SakinaBackupData(
            app = "Sakina",
            version = 1,
            exportDate = SakinaBackupManager.formatCurrentDate(),
            exportTimestamp = System.currentTimeMillis(),
            streakDays = _streakDays.value,
            dailyDhikrCount = _dailyDhikrCount.value,
            dailyDhikrGoal = _dailyDhikrGoal.value,
            tasbihCount = _tasbihCount.value,
            currentDhikrId = _currentDhikr.value.id,
            tasbihTarget = _tasbihTarget.value,
            tasbihLap = _tasbihLap.value,
            routines = routinesBackup,
            favoriteDuaIds = favs,
            masteredWordIds = mastered,
            personalNotes = _personalNotes.value,
            soundEnabled = _soundEnabled.value,
            hapticEnabled = _hapticEnabled.value,
            dailyCompletedGoals = dailyGoalsMap,
            customGoals = customGoalsList,
            locationCity = _locationConfig.value.cityName,
            calculationMethod = _locationConfig.value.calculationMethod.name,
            appLanguage = SakinaLocaleManager.currentLanguage.value.code
        )
    }

    fun exportBackupJson(): String {
        val data = getBackupData()
        val json = SakinaBackupManager.createBackupJson(data)
        _lastBackupDate.value = data.exportDate
        return json
    }

    private fun applyBackupData(data: SakinaBackupData): ImportSummary {
        _streakDays.value = data.streakDays
        _dailyDhikrCount.value = data.dailyDhikrCount
        _dailyDhikrGoal.value = data.dailyDhikrGoal
        _tasbihCount.value = data.tasbihCount
        _tasbihTarget.value = data.tasbihTarget
        _tasbihLap.value = data.tasbihLap
        _personalNotes.value = data.personalNotes
        _soundEnabled.value = data.soundEnabled
        _hapticEnabled.value = data.hapticEnabled
        _lastBackupDate.value = data.exportDate

        val foundDhikr = dhikrList.find { it.id == data.currentDhikrId }
        if (foundDhikr != null) {
            _currentDhikr.value = foundDhikr
        }

        // Apply routines
        if (data.routines.isNotEmpty()) {
            _routines.update { currentList ->
                currentList.map { cur ->
                    val backupR = data.routines.find { it.id == cur.id }
                    if (backupR != null) {
                        cur.copy(
                            completedItems = backupR.completedItems,
                            isDone = backupR.isDone
                        )
                    } else cur
                }
            }
        }

        // Apply favorites
        val favSet = data.favoriteDuaIds.toSet()
        _duasList.update { list ->
            list.map { dua ->
                dua.copy(isFavorite = favSet.contains(dua.id))
            }
        }

        // Apply mastered words
        val masteredSet = data.masteredWordIds.toSet()
        _wordTokensList.update { list ->
            list.map { token ->
                token.copy(isMastered = masteredSet.contains(token.id))
            }
        }

        // Apply daily completed goals
        if (data.dailyCompletedGoals.isNotEmpty()) {
            _dailyCompletedGoals.value = data.dailyCompletedGoals.mapValues { it.value.toSet() }
        }

        // Apply custom spiritual goals
        if (data.customGoals.isNotEmpty()) {
            val base = defaultSpiritualGoals.toMutableList()
            data.customGoals.forEach { g ->
                if (base.none { it.id == g.id }) {
                    base.add(g)
                }
            }
            _spiritualGoals.value = base
        }

        // Apply location & prayer configuration
        if (data.locationCity != null) {
            val matchingCity = PrayerCalculator.PRESET_CITIES.find {
                it.name.equals(data.locationCity, ignoreCase = true)
            }
            if (matchingCity != null) {
                val calcMethod = data.calculationMethod?.let { m ->
                    try { CalculationMethod.valueOf(m) } catch (_: Exception) { null }
                } ?: matchingCity.recommendedMethod
                _locationConfig.value = LocationConfig(
                    cityName = matchingCity.name,
                    arabicCityName = matchingCity.arabicName,
                    countryName = matchingCity.country,
                    latitude = matchingCity.latitude,
                    longitude = matchingCity.longitude,
                    timezoneId = matchingCity.timezoneId,
                    calculationMethod = calcMethod
                )
            }
        }

        if (data.appLanguage != null) {
            SakinaLocaleManager.setLanguage(AppLanguage.fromCode(data.appLanguage))
        }

        refreshPrayerTimes()
        saveToStorage()
        return SakinaBackupManager.summarize(data)
    }

    fun restoreFromJson(json: String): Result<ImportSummary> {
        val parseResult = SakinaBackupManager.parseBackupJson(json)
        return parseResult.map { data ->
            applyBackupData(data)
        }
    }

    fun writeBackupToUri(context: Context, uri: Uri): Result<Unit> {
        return try {
            val json = exportBackupJson()
            context.contentResolver.openOutputStream(uri)?.use { os ->
                OutputStreamWriter(os).use { writer ->
                    writer.write(json)
                    writer.flush()
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun readBackupFromUri(context: Context, uri: Uri): Result<ImportSummary> {
        return try {
            val stringBuilder = StringBuilder()
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    var line = reader.readLine()
                    while (line != null) {
                        stringBuilder.append(line).append("\n")
                        line = reader.readLine()
                    }
                }
            }
            val json = stringBuilder.toString()
            if (json.isBlank()) {
                Result.failure(IllegalArgumentException("Backup file was empty."))
            } else {
                restoreFromJson(json)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
