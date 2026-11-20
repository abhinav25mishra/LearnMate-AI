package com.learnmate.ai

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class StudentProfile(
    val name: String = "",
    val goal: String = "",
    val subject: String = "Python",
    val skillLevel: String = "Beginner",
    val interests: String = "",
    val dailyHours: String = "1.5",
    val targetDate: String = "30 Days",
    val preferredLanguage: String = "English",
    val passcode: String = "1234",
    val createdAt: String = "Nov 2026"
)

data class AssessmentRecord(
    val subject: String,
    val score: Int,
    val total: Int,
    val assignedLevel: String,
    val timestamp: String
)

object ProfileStorage {
    private const val PREFS_NAME = "vicharax_auth_accounts"
    private const val KEY_ACCOUNTS = "registered_accounts"
    private const val KEY_ACTIVE_USER = "active_signed_in_user"
    private const val KEY_VISIT_PREFIX = "visit_count_"
    private const val KEY_SCORES_PREFIX = "assessment_scores_"

    fun saveProfile(context: Context, profile: StudentProfile) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val accounts = loadAllAccounts(context).toMutableList()
        val existingIdx = accounts.indexOfFirst { it.name.equals(profile.name.trim(), ignoreCase = true) }
        val normalized = profile.copy(name = profile.name.trim())
        if (existingIdx >= 0) {
            accounts[existingIdx] = normalized
        } else {
            accounts.add(normalized)
        }

        val arr = JSONArray()
        accounts.forEach { acc ->
            arr.put(JSONObject().apply {
                put("name", acc.name)
                put("goal", acc.goal)
                put("subject", acc.subject)
                put("skillLevel", acc.skillLevel)
                put("interests", acc.interests)
                put("dailyHours", acc.dailyHours)
                put("targetDate", acc.targetDate)
                put("preferredLanguage", acc.preferredLanguage)
                put("passcode", acc.passcode)
                put("createdAt", acc.createdAt)
            })
        }

        prefs.edit()
            .putString(KEY_ACCOUNTS, arr.toString())
            .putString(KEY_ACTIVE_USER, normalized.name)
            .apply()
    }

    fun loadAllAccounts(context: Context): List<StudentProfile> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY_ACCOUNTS, null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            val list = mutableListOf<StudentProfile>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    StudentProfile(
                        name = obj.optString("name", ""),
                        goal = obj.optString("goal", ""),
                        subject = obj.optString("subject", "Python"),
                        skillLevel = obj.optString("skillLevel", "Beginner"),
                        interests = obj.optString("interests", ""),
                        dailyHours = obj.optString("dailyHours", "1.5"),
                        targetDate = obj.optString("targetDate", "30 Days"),
                        preferredLanguage = obj.optString("preferredLanguage", "English"),
                        passcode = obj.optString("passcode", "1234"),
                        createdAt = obj.optString("createdAt", "Nov 2026")
                    )
                )
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun loadProfile(context: Context): StudentProfile? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val activeName = prefs.getString(KEY_ACTIVE_USER, null) ?: return null
        return loadAllAccounts(context).firstOrNull { it.name.equals(activeName, ignoreCase = true) }
    }

    fun signIn(context: Context, name: String, passcode: String): StudentProfile? {
        val account = loadAllAccounts(context).firstOrNull {
            it.name.equals(name.trim(), ignoreCase = true) && it.passcode == passcode.trim()
        } ?: return null
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_ACTIVE_USER, account.name)
            .apply()
        return account
    }

    fun signOut(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_ACTIVE_USER)
            .apply()
    }

    fun clearProfile(context: Context) {
        signOut(context)
    }

    fun incrementAndGetVisitCount(context: Context, userName: String): Int {
        val key = KEY_VISIT_PREFIX + userName.trim().lowercase()
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val next = prefs.getInt(key, 0) + 1
        prefs.edit().putInt(key, next).apply()
        return next
    }

    fun getVisitCount(context: Context, userName: String): Int {
        val key = KEY_VISIT_PREFIX + userName.trim().lowercase()
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getInt(key, 1)
    }

    fun saveAssessmentScore(context: Context, userName: String, record: AssessmentRecord) {
        val key = KEY_SCORES_PREFIX + userName.trim().lowercase()
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val existing = loadAssessmentScores(context, userName).toMutableList()
        existing.add(0, record)
        val arr = JSONArray()
        existing.take(10).forEach { r ->
            arr.put(JSONObject().apply {
                put("subject", r.subject)
                put("score", r.score)
                put("total", r.total)
                put("assignedLevel", r.assignedLevel)
                put("timestamp", r.timestamp)
            })
        }
        prefs.edit().putString(key, arr.toString()).apply()
    }

    fun loadAssessmentScores(context: Context, userName: String): List<AssessmentRecord> {
        val key = KEY_SCORES_PREFIX + userName.trim().lowercase()
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getString(key, null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            val list = mutableListOf<AssessmentRecord>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    AssessmentRecord(
                        subject = obj.optString("subject", "Python"),
                        score = obj.optInt("score", 0),
                        total = obj.optInt("total", 10),
                        assignedLevel = obj.optString("assignedLevel", "Beginner"),
                        timestamp = obj.optString("timestamp", "Recent")
                    )
                )
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }
}
