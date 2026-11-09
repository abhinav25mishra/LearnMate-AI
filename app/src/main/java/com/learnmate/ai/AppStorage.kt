package com.learnmate.ai

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object AppStorage {
    private const val PREFS_NAME = "learnmate_progress"
    private const val KEY_GAP_REPORT = "gap_report"
    private const val KEY_ROADMAP = "roadmap"
    private const val KEY_CHAT = "chat_history"

    fun saveRoadmapData(
        context: Context,
        report: KnowledgeGapReport?,
        roadmap: List<RoadmapModule>
    ) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        if (report == null) {
            editor.remove(KEY_GAP_REPORT)
            editor.remove(KEY_ROADMAP)
        } else {
            val reportJson = JSONObject().apply {
                put("assignedLevel", report.assignedLevel)
                put("strongTopics", JSONArray(report.strongTopics))
                put("weakTopics", JSONArray(report.weakTopics))
                put("missingPrerequisites", JSONArray(report.missingPrerequisites))
            }
            editor.putString(KEY_GAP_REPORT, reportJson.toString())

            val roadmapArray = JSONArray()
            roadmap.forEach { m ->
                val obj = JSONObject().apply {
                    put("id", m.id)
                    put("title", m.title)
                    put("prerequisite", m.prerequisite)
                    put("dailyTask", m.dailyTask)
                    put("estimatedMinutes", m.estimatedMinutes)
                    put("isRevision", m.isRevision)
                    put("isCompleted", m.isCompleted)
                }
                roadmapArray.put(obj)
            }
            editor.putString(KEY_ROADMAP, roadmapArray.toString())
        }
        editor.apply()
    }

    fun loadGapReport(context: Context): KnowledgeGapReport? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY_GAP_REPORT, null) ?: return null
        return try {
            val obj = JSONObject(raw)
            KnowledgeGapReport(
                assignedLevel = obj.getString("assignedLevel"),
                strongTopics = jsonArrayToList(obj.getJSONArray("strongTopics")),
                weakTopics = jsonArrayToList(obj.getJSONArray("weakTopics")),
                missingPrerequisites = jsonArrayToList(obj.getJSONArray("missingPrerequisites"))
            )
        } catch (e: Exception) {
            null
        }
    }

    fun loadRoadmap(context: Context): List<RoadmapModule> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY_ROADMAP, null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            val list = mutableListOf<RoadmapModule>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    RoadmapModule(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        prerequisite = obj.getString("prerequisite"),
                        dailyTask = obj.getString("dailyTask"),
                        estimatedMinutes = obj.getInt("estimatedMinutes"),
                        isRevision = obj.getBoolean("isRevision"),
                        isCompleted = obj.getBoolean("isCompleted")
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveChatHistory(context: Context, messages: List<ChatMessage>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val arr = JSONArray()
        messages.forEach { msg ->
            val obj = JSONObject().apply {
                put("sender", msg.sender)
                put("text", msg.text)
            }
            arr.put(obj)
        }
        prefs.edit().putString(KEY_CHAT, arr.toString()).apply()
    }

    fun loadChatHistory(context: Context): List<ChatMessage> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY_CHAT, null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            val list = mutableListOf<ChatMessage>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    ChatMessage(
                        sender = obj.getString("sender"),
                        text = obj.getString("text")
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun jsonArrayToList(arr: JSONArray): List<String> {
        val list = mutableListOf<String>()
        for (i in 0 until arr.length()) {
            list.add(arr.getString(i))
        }
        return list
    }
}