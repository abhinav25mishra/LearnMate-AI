package com.learnmate.ai

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import org.json.JSONArray
import org.json.JSONObject

data class StudentProfile(
    val name: String = "",
    val username: String = "",
    val standard: String = "Undergraduate / College",
    val courses: String = "Computer Science & Engineering",
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
    private const val KEY_ACTIVE_DATES_PREFIX = "active_dates_"
    private const val KEY_EXPLORED_TOPICS_PREFIX = "explored_topics_"

    fun saveProfile(context: Context, profile: StudentProfile, persistSession: Boolean = true) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val accounts = loadAllAccounts(context).toMutableList()
        val normalizedName = profile.name.trim().ifEmpty {
            profile.username.trim().replaceFirstChar { it.uppercase() }
        }
        val normalizedUsername = profile.username.trim().ifEmpty {
            normalizedName.lowercase().replace(Regex("[^a-z0-9._]"), "")
        }
        val normalized = profile.copy(name = normalizedName, username = normalizedUsername)

        val existingIdx = accounts.indexOfFirst {
            it.username.equals(normalized.username, ignoreCase = true) ||
                it.name.equals(normalized.name, ignoreCase = true)
        }
        if (existingIdx >= 0) {
            accounts[existingIdx] = normalized
        } else {
            accounts.add(normalized)
        }

        val arr = JSONArray()
        accounts.forEach { acc ->
            arr.put(JSONObject().apply {
                put("name", acc.name)
                put("username", acc.username)
                put("standard", acc.standard)
                put("courses", acc.courses)
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

        val editor = prefs.edit().putString(KEY_ACCOUNTS, arr.toString())
        if (persistSession) {
            editor.putString(KEY_ACTIVE_USER, normalized.username)
        }
        editor.apply()

        recordActivityToday(context, normalized.name)
    }

    fun loadAllAccounts(context: Context): List<StudentProfile> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY_ACCOUNTS, null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            val list = mutableListOf<StudentProfile>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val name = obj.optString("name", "")
                val username = obj.optString("username", "").ifEmpty {
                    obj.optString("email", "").substringBefore("@").ifEmpty {
                        name.lowercase().replace(Regex("[^a-z0-9._]"), "")
                    }
                }
                list.add(
                    StudentProfile(
                        name = name,
                        username = username,
                        standard = obj.optString("standard", "Undergraduate / College"),
                        courses = obj.optString("courses", "Core Curriculum"),
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
        val activeKey = prefs.getString(KEY_ACTIVE_USER, null) ?: return null
        return loadAllAccounts(context).firstOrNull {
            it.username.equals(activeKey, ignoreCase = true) || it.name.equals(activeKey, ignoreCase = true)
        }
    }

    fun verifyCredentials(context: Context, usernameOrName: String, pin: String): StudentProfile? {
        val cleanId = usernameOrName.trim()
        val cleanPin = pin.trim()
        return loadAllAccounts(context).firstOrNull {
            (it.username.equals(cleanId, ignoreCase = true) || it.name.equals(cleanId, ignoreCase = true)) &&
                it.passcode == cleanPin
        }
    }

    fun findAccountByIdentifier(context: Context, usernameOrName: String): StudentProfile? {
        val cleanId = usernameOrName.trim()
        return loadAllAccounts(context).firstOrNull {
            it.username.equals(cleanId, ignoreCase = true) || it.name.equals(cleanId, ignoreCase = true)
        }
    }

    fun signIn(context: Context, usernameOrName: String, pin: String, rememberMe: Boolean = true): StudentProfile? {
        val account = verifyCredentials(context, usernameOrName, pin) ?: return null
        if (rememberMe) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_ACTIVE_USER, account.username)
                .apply()
        }
        recordActivityToday(context, account.name)
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
        recordActivityToday(context, userName)
        return next
    }

    fun recordActivityToday(context: Context, userName: String) {
        if (userName.isBlank()) return
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val userKey = userName.trim().lowercase()
        val todayIso = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val existingDates = prefs.getStringSet(KEY_ACTIVE_DATES_PREFIX + userKey, emptySet())?.toMutableSet() ?: mutableSetOf()
        if (existingDates.add(todayIso)) {
            prefs.edit().putStringSet(KEY_ACTIVE_DATES_PREFIX + userKey, existingDates).apply()
        }
    }

    fun getActiveDateStrings(context: Context, userName: String): Set<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(KEY_ACTIVE_DATES_PREFIX + userName.trim().lowercase(), emptySet()) ?: emptySet()
    }

    fun getActiveWeekDayIndices(context: Context, userName: String): Set<Int> {
        val dates = getActiveDateStrings(context, userName)
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val dayFmt = SimpleDateFormat("u", Locale.US)
        val indices = mutableSetOf<Int>()
        dates.forEach { dateStr ->
            try {
                val parsed = parser.parse(dateStr)
                if (parsed != null) {
                    val isoDay = dayFmt.format(parsed).toIntOrNull() ?: 1
                    indices.add((isoDay - 1).coerceIn(0, 6))
                }
            } catch (_: Exception) {
            }
        }
        return indices
    }

    fun markTopicExplored(context: Context, userName: String, subject: String, topicId: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val key = "${KEY_EXPLORED_TOPICS_PREFIX}${userName.trim().lowercase()}_${subject.trim().lowercase()}"
        val current = prefs.getStringSet(key, emptySet())?.toMutableSet() ?: mutableSetOf()
        current.add(topicId)
        prefs.edit().putStringSet(key, current).apply()
        recordActivityToday(context, userName)
    }

    fun getExploredTopics(context: Context, userName: String, subject: String): Set<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val key = "${KEY_EXPLORED_TOPICS_PREFIX}${userName.trim().lowercase()}_${subject.trim().lowercase()}"
        return prefs.getStringSet(key, emptySet()) ?: emptySet()
    }

    fun saveAssessmentScore(context: Context, userName: String, record: AssessmentRecord) {
        val key = KEY_SCORES_PREFIX + userName.trim().lowercase()
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val existing = loadAssessmentScores(context, userName).toMutableList()
        existing.add(0, record)
        val arr = JSONArray()
        existing.take(12).forEach { r ->
            arr.put(JSONObject().apply {
                put("subject", r.subject)
                put("score", r.score)
                put("total", r.total)
                put("assignedLevel", r.assignedLevel)
                put("timestamp", r.timestamp)
            })
        }
        prefs.edit().putString(key, arr.toString()).apply()
        recordActivityToday(context, userName)
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
