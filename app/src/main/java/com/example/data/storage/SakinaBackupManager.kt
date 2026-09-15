package com.example.data.storage

import com.example.data.model.SpiritualGoalItem
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class RoutineBackupState(
    val id: String,
    val title: String,
    val completedItems: Int,
    val totalItems: Int,
    val isDone: Boolean
)

data class SakinaBackupData(
    val app: String = "Sakina",
    val version: Int = 1,
    val exportDate: String,
    val exportTimestamp: Long = System.currentTimeMillis(),
    val streakDays: Int,
    val dailyDhikrCount: Int,
    val dailyDhikrGoal: Int,
    val tasbihCount: Int,
    val currentDhikrId: String,
    val tasbihTarget: Int?,
    val tasbihLap: Int,
    val routines: List<RoutineBackupState>,
    val favoriteDuaIds: List<String>,
    val masteredWordIds: List<Int>,
    val personalNotes: String,
    val soundEnabled: Boolean,
    val hapticEnabled: Boolean,
    val dailyCompletedGoals: Map<String, List<String>> = emptyMap(),
    val customGoals: List<SpiritualGoalItem> = emptyList(),
    val locationCity: String? = null,
    val calculationMethod: String? = null,
    val appLanguage: String? = null
)

data class ImportSummary(
    val streakDays: Int,
    val dailyDhikr: Int,
    val routineCount: Int,
    val favoriteCount: Int,
    val masteredWordsCount: Int,
    val notesLength: Int,
    val exportDate: String,
    val completedGoalsDaysCount: Int = 0
)

object SakinaBackupManager {

    fun formatCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }

    fun createBackupJson(data: SakinaBackupData): String {
        val root = JSONObject()
        root.put("app", data.app)
        root.put("version", data.version)
        root.put("exportDate", data.exportDate)
        root.put("exportTimestamp", data.exportTimestamp)
        root.put("streakDays", data.streakDays)
        root.put("dailyDhikrCount", data.dailyDhikrCount)
        root.put("dailyDhikrGoal", data.dailyDhikrGoal)
        root.put("tasbihCount", data.tasbihCount)
        root.put("currentDhikrId", data.currentDhikrId)
        if (data.tasbihTarget != null) {
            root.put("tasbihTarget", data.tasbihTarget)
        } else {
            root.put("tasbihTarget", JSONObject.NULL)
        }
        root.put("tasbihLap", data.tasbihLap)

        // Routines
        val routinesArray = JSONArray()
        data.routines.forEach { r ->
            val rObj = JSONObject()
            rObj.put("id", r.id)
            rObj.put("title", r.title)
            rObj.put("completedItems", r.completedItems)
            rObj.put("totalItems", r.totalItems)
            rObj.put("isDone", r.isDone)
            routinesArray.put(rObj)
        }
        root.put("routines", routinesArray)

        // Favorite Duas
        val favsArray = JSONArray()
        data.favoriteDuaIds.forEach { favsArray.put(it) }
        root.put("favoriteDuaIds", favsArray)

        // Mastered Word IDs
        val wordsArray = JSONArray()
        data.masteredWordIds.forEach { wordsArray.put(it) }
        root.put("masteredWordIds", wordsArray)

        // Notes
        root.put("personalNotes", data.personalNotes)

        // Settings
        val settingsObj = JSONObject()
        settingsObj.put("soundEnabled", data.soundEnabled)
        settingsObj.put("hapticEnabled", data.hapticEnabled)
        if (data.locationCity != null) settingsObj.put("locationCity", data.locationCity)
        if (data.calculationMethod != null) settingsObj.put("calculationMethod", data.calculationMethod)
        if (data.appLanguage != null) settingsObj.put("appLanguage", data.appLanguage)
        root.put("settings", settingsObj)

        // Daily Completed Goals
        val dailyGoalsObj = JSONObject()
        data.dailyCompletedGoals.forEach { (dateKey, goalIds) ->
            val arr = JSONArray()
            goalIds.forEach { arr.put(it) }
            dailyGoalsObj.put(dateKey, arr)
        }
        root.put("dailyCompletedGoals", dailyGoalsObj)

        // Custom Spiritual Goals
        val customGoalsArray = JSONArray()
        data.customGoals.forEach { goal ->
            val gObj = JSONObject()
            gObj.put("id", goal.id)
            gObj.put("title", goal.title)
            gObj.put("arabicTitle", goal.arabicTitle)
            gObj.put("category", goal.category)
            gObj.put("description", goal.description)
            gObj.put("iconType", goal.iconType)
            gObj.put("isCustom", goal.isCustom)
            customGoalsArray.put(gObj)
        }
        root.put("customGoals", customGoalsArray)

        return root.toString(2)
    }

    fun parseBackupJson(jsonString: String): Result<SakinaBackupData> {
        return try {
            val root = JSONObject(jsonString.trim())

            // Validate signature or basic keys
            val app = root.optString("app", "Sakina")
            val version = root.optInt("version", 1)
            val exportDate = root.optString("exportDate", formatCurrentDate())
            val exportTimestamp = root.optLong("exportTimestamp", System.currentTimeMillis())

            val streakDays = root.optInt("streakDays", 14)
            val dailyDhikrCount = root.optInt("dailyDhikrCount", 0)
            val dailyDhikrGoal = root.optInt("dailyDhikrGoal", 500)
            val tasbihCount = root.optInt("tasbihCount", 0)
            val currentDhikrId = root.optString("currentDhikrId", "subhanallah")

            val tasbihTarget = if (root.has("tasbihTarget") && !root.isNull("tasbihTarget")) {
                root.getInt("tasbihTarget")
            } else {
                null
            }
            val tasbihLap = root.optInt("tasbihLap", 1)

            val routines = mutableListOf<RoutineBackupState>()
            val routinesArray = root.optJSONArray("routines")
            if (routinesArray != null) {
                for (i in 0 until routinesArray.length()) {
                    val rObj = routinesArray.optJSONObject(i) ?: continue
                    routines.add(
                        RoutineBackupState(
                            id = rObj.optString("id", ""),
                            title = rObj.optString("title", ""),
                            completedItems = rObj.optInt("completedItems", 0),
                            totalItems = rObj.optInt("totalItems", 24),
                            isDone = rObj.optBoolean("isDone", false)
                        )
                    )
                }
            }

            val favoriteDuaIds = mutableListOf<String>()
            val favsArray = root.optJSONArray("favoriteDuaIds")
            if (favsArray != null) {
                for (i in 0 until favsArray.length()) {
                    val id = favsArray.optString(i)
                    if (id.isNotEmpty()) favoriteDuaIds.add(id)
                }
            }

            val masteredWordIds = mutableListOf<Int>()
            val wordsArray = root.optJSONArray("masteredWordIds")
            if (wordsArray != null) {
                for (i in 0 until wordsArray.length()) {
                    val id = wordsArray.optInt(i, -1)
                    if (id != -1) masteredWordIds.add(id)
                }
            }

            val personalNotes = root.optString("personalNotes", "")

            var soundEnabled = true
            var hapticEnabled = true
            var locationCity: String? = null
            var calculationMethod: String? = null
            var appLanguage: String? = null
            val settingsObj = root.optJSONObject("settings")
            if (settingsObj != null) {
                soundEnabled = settingsObj.optBoolean("soundEnabled", true)
                hapticEnabled = settingsObj.optBoolean("hapticEnabled", true)
                if (settingsObj.has("locationCity")) locationCity = settingsObj.optString("locationCity")
                if (settingsObj.has("calculationMethod")) calculationMethod = settingsObj.optString("calculationMethod")
                if (settingsObj.has("appLanguage")) appLanguage = settingsObj.optString("appLanguage")
            }

            // Daily Completed Goals
            val dailyCompletedGoals = mutableMapOf<String, List<String>>()
            val dailyGoalsObj = root.optJSONObject("dailyCompletedGoals")
            if (dailyGoalsObj != null) {
                val keys = dailyGoalsObj.keys()
                while (keys.hasNext()) {
                    val dateKey = keys.next()
                    val arr = dailyGoalsObj.optJSONArray(dateKey)
                    val idList = mutableListOf<String>()
                    if (arr != null) {
                        for (j in 0 until arr.length()) {
                            val id = arr.optString(j)
                            if (id.isNotEmpty()) idList.add(id)
                        }
                    }
                    dailyCompletedGoals[dateKey] = idList
                }
            }

            // Custom Goals
            val customGoals = mutableListOf<SpiritualGoalItem>()
            val customGoalsArr = root.optJSONArray("customGoals")
            if (customGoalsArr != null) {
                for (k in 0 until customGoalsArr.length()) {
                    val gObj = customGoalsArr.optJSONObject(k) ?: continue
                    customGoals.add(
                        SpiritualGoalItem(
                            id = gObj.optString("id", "custom_${System.currentTimeMillis()}"),
                            title = gObj.optString("title", ""),
                            arabicTitle = gObj.optString("arabicTitle", ""),
                            category = gObj.optString("category", "Sunnah"),
                            description = gObj.optString("description", ""),
                            iconType = gObj.optString("iconType", "spa"),
                            isCustom = true
                        )
                    )
                }
            }

            Result.success(
                SakinaBackupData(
                    app = app,
                    version = version,
                    exportDate = exportDate,
                    exportTimestamp = exportTimestamp,
                    streakDays = streakDays,
                    dailyDhikrCount = dailyDhikrCount,
                    dailyDhikrGoal = dailyDhikrGoal,
                    tasbihCount = tasbihCount,
                    currentDhikrId = currentDhikrId,
                    tasbihTarget = tasbihTarget,
                    tasbihLap = tasbihLap,
                    routines = routines,
                    favoriteDuaIds = favoriteDuaIds,
                    masteredWordIds = masteredWordIds,
                    personalNotes = personalNotes,
                    soundEnabled = soundEnabled,
                    hapticEnabled = hapticEnabled,
                    dailyCompletedGoals = dailyCompletedGoals,
                    customGoals = customGoals,
                    locationCity = locationCity,
                    calculationMethod = calculationMethod,
                    appLanguage = appLanguage
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun summarize(data: SakinaBackupData): ImportSummary {
        return ImportSummary(
            streakDays = data.streakDays,
            dailyDhikr = data.dailyDhikrCount,
            routineCount = data.routines.count { it.isDone },
            favoriteCount = data.favoriteDuaIds.size,
            masteredWordsCount = data.masteredWordIds.size,
            notesLength = data.personalNotes.length,
            exportDate = data.exportDate,
            completedGoalsDaysCount = data.dailyCompletedGoals.size
        )
    }
}
