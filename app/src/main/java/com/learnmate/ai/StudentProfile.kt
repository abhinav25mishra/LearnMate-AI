package com.learnmate.ai

import android.content.Context

data class StudentProfile(
    val name: String = "",
    val goal: String = "",
    val subject: String = "",
    val skillLevel: String = "Beginner",
    val interests: String = "",
    val dailyHours: String = "1.5",
    val targetDate: String = "30 Days",
    val preferredLanguage: String = "English"
)

object ProfileStorage {
    private const val PREFS_NAME = "learnmate_profile"

    fun saveProfile(context: Context, profile: StudentProfile) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("name", profile.name)
            putString("goal", profile.goal)
            putString("subject", profile.subject)
            putString("skillLevel", profile.skillLevel)
            putString("interests", profile.interests)
            putString("dailyHours", profile.dailyHours)
            putString("targetDate", profile.targetDate)
            putString("preferredLanguage", profile.preferredLanguage)
            putBoolean("isOnboarded", true)
            apply()
        }
    }

    fun loadProfile(context: Context): StudentProfile? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (!prefs.getBoolean("isOnboarded", false)) return null
        return StudentProfile(
            name = prefs.getString("name", "") ?: "",
            goal = prefs.getString("goal", "") ?: "",
            subject = prefs.getString("subject", "") ?: "",
            skillLevel = prefs.getString("skillLevel", "Beginner") ?: "Beginner",
            interests = prefs.getString("interests", "") ?: "",
            dailyHours = prefs.getString("dailyHours", "1.5") ?: "1.5",
            targetDate = prefs.getString("targetDate", "30 Days") ?: "30 Days",
            preferredLanguage = prefs.getString("preferredLanguage", "English") ?: "English"
        )
    }

    fun clearProfile(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().clear().apply()
    }
}