package com.learnmate.ai

interface AiMentorEngine {
    suspend fun askMentor(
        question: String,
        profile: StudentProfile,
        difficulty: String,
        hintOnly: Boolean
    ): String
}

/**
 * Temporary offline fallback until Member 3 plugs in LocalLlamaEngine (ExecuTorch).
 */
object MockAiMentorEngine : AiMentorEngine {
    override suspend fun askMentor(
        question: String,
        profile: StudentProfile,
        difficulty: String,
        hintOnly: Boolean
    ): String {
        val langNote = if (profile.preferredLanguage != "English") {
            "[${profile.preferredLanguage} Mode] "
        } else {
            ""
        }

        return if (hintOnly) {
            "${langNote}Hint ($difficulty): Break '$question' into smaller steps. " +
                "First, check what inputs you have in ${profile.subject}, and what output you expect. " +
                "Try writing the first 2 lines before checking the full answer!"
        } else {
            "${langNote}Explanation ($difficulty level for ${profile.subject}):\n" +
                "You asked: \"$question\".\n" +
                "1. Core idea: Focus on the foundational rule behind this concept.\n" +
                "2. Example: Think of it like organizing items in a labelled box.\n" +
                "3. Next step: Practice 1 small exercise in your Roadmap to lock this in."
        }
    }
}