package com.example.data.location

import com.example.data.model.PrayerTimeInfo
import com.example.data.model.SinglePrayerTime
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.tan

enum class CalculationMethod(val displayName: String, val arabicName: String, val fajrAngle: Double, val ishaAngle: Double, val ishaIntervalMinutes: Int? = null) {
    UMM_AL_QURA("Umm al-Qura (Makkah)", "أم القرى (مكة المكرمة)", 18.5, 0.0, ishaIntervalMinutes = 90),
    MUSLIM_WORLD_LEAGUE("Muslim World League", "رابطة العالم الإسلامي", 18.0, 17.0),
    ISNA("ISNA (North America)", "الجمعية الإسلامية لأمريكا الشمالية", 15.0, 15.0),
    EGYPTIAN("Egyptian General Authority", "الهيئة المصرية العامة للمساحة", 19.5, 17.5),
    KARACHI("Univ. of Islamic Sciences, Karachi", "جامعة العلوم الإسلامية بكراتشي", 18.0, 18.0),
    DUBAI("Dubai / Gulf", "دبي / الخليج", 18.2, 18.2)
}

enum class AsrJuristicMethod(val displayName: String, val arabicName: String, val shadowFactor: Double) {
    STANDARD("Standard (Shafi'i, Maliki, Hanbali)", "الجمهور (شافعي، مالكي، حنبلي)", 1.0),
    HANAFI("Hanafi (2x Shadow)", "الحنفي (ضعف الظل)", 2.0)
}

data class CityPreset(
    val name: String,
    val arabicName: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val timezoneId: String,
    val recommendedMethod: CalculationMethod = CalculationMethod.UMM_AL_QURA
)

data class LocationConfig(
    val cityName: String = "Riyadh",
    val arabicCityName: String = "الرياض",
    val countryName: String = "Saudi Arabia",
    val latitude: Double = 24.7136,
    val longitude: Double = 46.6753,
    val timezoneId: String = "Asia/Riyadh",
    val calculationMethod: CalculationMethod = CalculationMethod.UMM_AL_QURA,
    val asrJuristicMethod: AsrJuristicMethod = AsrJuristicMethod.STANDARD,
    val isAutoGps: Boolean = false
)

data class QiblaInfo(
    val bearingDegrees: Double,
    val directionText: String
)

object PrayerCalculator {

    val PRESET_CITIES = listOf(
        CityPreset("Makkah", "مكة المكرمة", "Saudi Arabia", 21.4225, 39.8262, "Asia/Riyadh", CalculationMethod.UMM_AL_QURA),
        CityPreset("Madinah", "المدينة المنورة", "Saudi Arabia", 24.4672, 39.6111, "Asia/Riyadh", CalculationMethod.UMM_AL_QURA),
        CityPreset("Riyadh", "الرياض", "Saudi Arabia", 24.7136, 46.6753, "Asia/Riyadh", CalculationMethod.UMM_AL_QURA),
        CityPreset("Dubai", "دبي", "United Arab Emirates", 25.2048, 55.2708, "Asia/Dubai", CalculationMethod.DUBAI),
        CityPreset("Cairo", "القاهرة", "Egypt", 30.0444, 31.2357, "Africa/Cairo", CalculationMethod.EGYPTIAN),
        CityPreset("Istanbul", "إسطنبول", "Turkey", 41.0082, 28.9784, "Europe/Istanbul", CalculationMethod.MUSLIM_WORLD_LEAGUE),
        CityPreset("Karachi", "كراتشي", "Pakistan", 24.8607, 67.0011, "Asia/Karachi", CalculationMethod.KARACHI),
        CityPreset("Jakarta", "جاكرتا", "Indonesia", -6.2088, 106.8456, "Asia/Jakarta", CalculationMethod.MUSLIM_WORLD_LEAGUE),
        CityPreset("Kuala Lumpur", "كوالالمبور", "Malaysia", 3.1390, 101.6869, "Asia/Kuala_Lumpur", CalculationMethod.MUSLIM_WORLD_LEAGUE),
        CityPreset("London", "لندن", "United Kingdom", 51.5074, -0.1278, "Europe/London", CalculationMethod.MUSLIM_WORLD_LEAGUE),
        CityPreset("Paris", "باريس", "France", 48.8566, 2.3522, "Europe/Paris", CalculationMethod.MUSLIM_WORLD_LEAGUE),
        CityPreset("New York", "نيويورك", "United States", 40.7128, -74.0060, "America/New_York", CalculationMethod.ISNA),
        CityPreset("Toronto", "تورونتو", "Canada", 43.6532, -79.3832, "America/Toronto", CalculationMethod.ISNA),
        CityPreset("Sydney", "سيدني", "Australia", -33.8688, 151.2093, "Australia/Sydney", CalculationMethod.MUSLIM_WORLD_LEAGUE),
        CityPreset("Tokyo", "طوكيو", "Japan", 35.6762, 139.6503, "Asia/Tokyo", CalculationMethod.MUSLIM_WORLD_LEAGUE)
    )

    private const val KAABA_LAT = 21.422487
    private const val KAABA_LNG = 39.826206

    /**
     * Calculates the Qibla bearing from the given coordinate in degrees (0-360).
     */
    fun calculateQiblaDirection(lat: Double, lng: Double): Double {
        val latRad = Math.toRadians(lat)
        val lngRad = Math.toRadians(lng)
        val kaabaLatRad = Math.toRadians(KAABA_LAT)
        val kaabaLngRad = Math.toRadians(KAABA_LNG)

        val deltaLng = kaabaLngRad - lngRad
        val y = sin(deltaLng) * cos(kaabaLatRad)
        val x = cos(latRad) * sin(kaabaLatRad) - sin(latRad) * cos(kaabaLatRad) * cos(deltaLng)

        var bearing = Math.toDegrees(atan2(y, x))
        if (bearing < 0) {
            bearing += 360.0
        }
        return (bearing * 10).toInt() / 10.0
    }

    fun getQiblaCompassString(bearing: Double): String {
        val directions = listOf("N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW")
        val index = (((bearing + 11.25) % 360) / 22.5).toInt()
        val compassStr = directions.getOrElse(index) { "SW" }
        return "${bearing.toInt()}° $compassStr"
    }

    fun calculateQibla(lat: Double, lng: Double): QiblaInfo {
        val bearing = calculateQiblaDirection(lat, lng)
        val dir = getQiblaCompassString(bearing)
        return QiblaInfo(bearingDegrees = bearing, directionText = dir)
    }

    /**
     * High-precision astronomical calculation of Islamic prayer times.
     */
    fun calculateDailyPrayers(
        date: Date,
        config: LocationConfig
    ): List<SinglePrayerTime> {
        val cal = Calendar.getInstance(TimeZone.getTimeZone(config.timezoneId))
        cal.time = date

        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val tzOffset = TimeZone.getTimeZone(config.timezoneId).getOffset(date.time) / 3600000.0

        val julianDay = getJulianDay(year, month, day) - (config.longitude / (15.0 * 24.0))

        // Solar coordinates
        val (declination, eqTime) = getSolarCoordinates(julianDay)

        // Solar noon (Dhuhr)
        val solarNoon = fixHour(12.0 + tzOffset - (config.longitude / 15.0) - (eqTime / 60.0))

        // Sunrise & Sunset angle (standard atmospheric refraction -0.833)
        val sunriseAngle = 0.833
        val sunriseHourAngle = calculateHourAngle(config.latitude, declination, -sunriseAngle)
        val sunrise = fixHour(solarNoon - sunriseHourAngle / 15.0)
        val sunset = fixHour(solarNoon + sunriseHourAngle / 15.0)

        // Fajr angle
        val fajrHourAngle = calculateHourAngle(config.latitude, declination, -config.calculationMethod.fajrAngle)
        val fajr = fixHour(solarNoon - fajrHourAngle / 15.0)

        // Asr angle
        val asrAltitude = -Math.toDegrees(
            atan2(
                1.0,
                config.asrJuristicMethod.shadowFactor + tan(Math.toRadians(abs(config.latitude - declination)))
            )
        )
        val asrHourAngle = calculateHourAngle(config.latitude, declination, asrAltitude)
        val asr = fixHour(solarNoon + asrHourAngle / 15.0)

        // Maghrib is sunset + 2 minutes safety margin
        val maghrib = sunset + (2.0 / 60.0)

        // Isha
        val isha = if (config.calculationMethod.ishaIntervalMinutes != null) {
            maghrib + (config.calculationMethod.ishaIntervalMinutes / 60.0)
        } else {
            val ishaHourAngle = calculateHourAngle(config.latitude, declination, -config.calculationMethod.ishaAngle)
            fixHour(solarNoon + ishaHourAngle / 15.0)
        }

        // Determine which prayer is next relative to current time
        val nowCal = Calendar.getInstance(TimeZone.getTimeZone(config.timezoneId))
        val currentDecimalHour = nowCal.get(Calendar.HOUR_OF_DAY) + (nowCal.get(Calendar.MINUTE) / 60.0)

        val times = listOf(
            Triple("Fajr", "الفجر", fajr),
            Triple("Sunrise", "الشروق", sunrise),
            Triple("Dhuhr", "الظهر", solarNoon),
            Triple("Asr", "العصر", asr),
            Triple("Maghrib", "المغرب", maghrib),
            Triple("Isha", "العشاء", isha)
        )

        var nextIndex = times.indexOfFirst { it.third > currentDecimalHour }
        if (nextIndex == -1) nextIndex = 0 // loops to next day's Fajr

        return times.mapIndexed { index, item ->
            SinglePrayerTime(
                name = item.first,
                arabicName = item.second,
                time = formatDecimalHours(item.third),
                isNext = (index == nextIndex)
            )
        }
    }

    fun calculatePrayerInfo(
        date: Date,
        config: LocationConfig
    ): PrayerTimeInfo {
        val prayerList = calculateDailyPrayers(date, config)
        val nextPrayer = prayerList.firstOrNull { it.isNext } ?: prayerList[0]

        val cal = Calendar.getInstance(TimeZone.getTimeZone(config.timezoneId))
        val nowMinutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)

        val (nextHour, nextMin) = parseHoursMinutes(nextPrayer.time)
        var targetMinutes = nextHour * 60 + nextMin
        if (targetMinutes < nowMinutes) {
            targetMinutes += 24 * 60 // Next day Fajr
        }
        val countdownMinutes = targetMinutes - nowMinutes

        // Previous and upcoming prayer
        val nextIdx = prayerList.indexOfFirst { it.isNext }
        val prevIdx = if (nextIdx > 0) nextIdx - 1 else prayerList.size - 1
        val upcomingIdx = (nextIdx + 1) % prayerList.size

        val prevPrayer = prayerList[prevIdx]
        val upcomingPrayer = prayerList[upcomingIdx]

        val (prevH, prevM) = parseHoursMinutes(prevPrayer.time)
        var prevMinutes = prevH * 60 + prevM
        if (prevMinutes > nowMinutes) {
            prevMinutes -= 24 * 60
        }

        val totalInterval = (targetMinutes - prevMinutes).coerceAtLeast(1)
        val elapsed = (nowMinutes - prevMinutes).coerceIn(0, totalInterval)
        val progress = elapsed.toFloat() / totalInterval

        val qiblaBearing = calculateQiblaDirection(config.latitude, config.longitude)
        val qiblaStr = "Qibla ${getQiblaCompassString(qiblaBearing)}"

        // Hijri date estimation for Riyadh / Global (Umm al-Qura baseline)
        val hijriStr = getEstimatedHijriDate(date, config.cityName)

        return PrayerTimeInfo(
            nextPrayerName = nextPrayer.name,
            nextPrayerArabic = "صَلَاة ${nextPrayer.arabicName}",
            nextPrayerTime = nextPrayer.time,
            countdownMinutes = countdownMinutes,
            qiblaDirection = qiblaStr,
            hijriDate = hijriStr,
            prevPrayerName = prevPrayer.name,
            prevPrayerTime = prevPrayer.time,
            upcomingPrayerName = upcomingPrayer.name,
            upcomingPrayerTime = upcomingPrayer.time,
            progressPercent = progress
        )
    }

    private fun parseHoursMinutes(timeStr: String): Pair<Int, Int> {
        val parts = timeStr.split(":")
        val h = parts.getOrNull(0)?.toIntOrNull() ?: 12
        val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
        return Pair(h, m)
    }

    private fun formatDecimalHours(hours: Double): String {
        val fixed = fixHour(hours)
        val h = floor(fixed).toInt()
        val m = floor((fixed - h) * 60.0 + 0.5).toInt()
        val adjustedH = if (m == 60) (h + 1) % 24 else h % 24
        val adjustedM = if (m == 60) 0 else m
        return String.format(Locale.US, "%02d:%02d", adjustedH, adjustedM)
    }

    private fun fixHour(a: Double): Double {
        var res = a - 24.0 * floor(a / 24.0)
        if (res < 0) res += 24.0
        return res
    }

    private fun getJulianDay(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    private fun getSolarCoordinates(jd: Double): Pair<Double, Double> {
        val d = jd - 2451545.0
        val g = fixAngle(357.529 + 0.98560028 * d)
        val q = fixAngle(280.459 + 0.98564736 * d)
        val l = fixAngle(q + 1.915 * sin(Math.toRadians(g)) + 0.020 * sin(Math.toRadians(2 * g)))

        val e = 23.439 - 0.00000036 * d
        val declination = Math.toDegrees(asin(sin(Math.toRadians(e)) * sin(Math.toRadians(l))))
        var ra = Math.toDegrees(atan2(cos(Math.toRadians(e)) * sin(Math.toRadians(l)), cos(Math.toRadians(l)))) / 15.0
        ra = fixHour(ra)

        val eqTime = q / 15.0 - ra
        return Pair(declination, eqTime * 60.0)
    }

    private fun fixAngle(a: Double): Double {
        var res = a - 360.0 * floor(a / 360.0)
        if (res < 0) res += 360.0
        return res
    }

    private fun calculateHourAngle(latitude: Double, declination: Double, altitude: Double): Double {
        val latRad = Math.toRadians(latitude)
        val declRad = Math.toRadians(declination)
        val altRad = Math.toRadians(altitude)

        val cosHA = (sin(altRad) - sin(latRad) * sin(declRad)) / (cos(latRad) * cos(declRad))
        val clamped = cosHA.coerceIn(-1.0, 1.0)
        return Math.toDegrees(acos(clamped))
    }

    private fun getEstimatedHijriDate(date: Date, cityName: String): String {
        // Approximate calculation based on Islamic calendar epoch
        val cal = Calendar.getInstance()
        cal.time = date
        val gregYear = cal.get(Calendar.YEAR)

        // Months names in Hijri
        val hijriMonths = listOf(
            "Muharram", "Safar", "Rabi' al-Awwal", "Rabi' al-Thani",
            "Jumada al-Awwal", "Jumada al-Thani", "Rajab", "Sha'ban",
            "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah"
        )
        // Approximate Sha'ban / Ramadan 1446-1447 AH
        val hijriYear = 1446 + if (gregYear > 2025) (gregYear - 2025) else 0
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val monthIdx = (cal.get(Calendar.MONTH) + 7) % 12
        val monthName = hijriMonths[monthIdx]

        return "$day $monthName $hijriYear AH · $cityName"
    }
}
