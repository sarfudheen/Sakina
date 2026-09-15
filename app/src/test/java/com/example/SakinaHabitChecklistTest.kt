package com.example

import com.example.data.repository.SakinaRepository
import com.example.data.storage.SakinaBackupManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SakinaHabitChecklistTest {

    @Test
    fun testDefaultSpiritualGoalsExist() {
        val goals = SakinaRepository.spiritualGoals.value
        assertTrue("Should have default spiritual goals", goals.isNotEmpty())
        assertTrue("Should have Fajr prayer goal", goals.any { it.id == "fajr_prayer" })
        assertTrue("Should have Quran goal", goals.any { it.id == "quran_tadabbur" })
    }

    @Test
    fun testToggleGoalForToday() {
        val todayKey = SakinaRepository.getTodayDateKey()
        val goalId = "fajr_prayer"
        val wasDone = SakinaRepository.dailyCompletedGoals.value[todayKey]?.contains(goalId) == true

        SakinaRepository.toggleSpiritualGoal(todayKey, goalId)
        val isDoneAfterFirstToggle = SakinaRepository.dailyCompletedGoals.value[todayKey]?.contains(goalId) == true
        assertEquals(!wasDone, isDoneAfterFirstToggle)

        // Toggle back
        SakinaRepository.toggleSpiritualGoal(todayKey, goalId)
        val isDoneAfterSecondToggle = SakinaRepository.dailyCompletedGoals.value[todayKey]?.contains(goalId) == true
        assertEquals(wasDone, isDoneAfterSecondToggle)
    }

    @Test
    fun testSetAllGoalsForDate() {
        val testDateKey = "2026-09-20"
        val totalGoals = SakinaRepository.spiritualGoals.value.size

        // Mark all as done
        SakinaRepository.setAllGoalsForDate(testDateKey, completed = true)
        val doneCount = SakinaRepository.dailyCompletedGoals.value[testDateKey]?.size ?: 0
        assertEquals(totalGoals, doneCount)

        // Reset
        SakinaRepository.setAllGoalsForDate(testDateKey, completed = false)
        val resetCount = SakinaRepository.dailyCompletedGoals.value[testDateKey]?.size ?: 0
        assertEquals(0, resetCount)
    }

    @Test
    fun testCustomGoalLifecycle() {
        val initialCount = SakinaRepository.spiritualGoals.value.size
        SakinaRepository.addCustomGoal(
            title = "Night Tahajjud",
            arabicTitle = "قيام الليل",
            category = "Sunnah",
            description = "Pray 2 rak'ahs before Fajr"
        )

        val updatedGoals = SakinaRepository.spiritualGoals.value
        assertEquals(initialCount + 1, updatedGoals.size)
        val addedGoal = updatedGoals.find { it.title == "Night Tahajjud" }
        assertTrue("Added goal should exist", addedGoal != null)
        assertTrue("Should be custom", addedGoal!!.isCustom)

        // Delete custom goal
        SakinaRepository.deleteCustomGoal(addedGoal.id)
        val finalGoals = SakinaRepository.spiritualGoals.value
        assertEquals(initialCount, finalGoals.size)
        assertFalse("Should be removed", finalGoals.any { it.id == addedGoal.id })
    }

    @Test
    fun testBackupAndRestoreSpiritualGoals() {
        val backupData = SakinaRepository.getBackupData()
        val json = SakinaBackupManager.createBackupJson(backupData)
        val parseResult = SakinaBackupManager.parseBackupJson(json)

        assertTrue(parseResult.isSuccess)
        val parsed = parseResult.getOrThrow()
        assertTrue(parsed.dailyCompletedGoals.isNotEmpty())
    }
}
