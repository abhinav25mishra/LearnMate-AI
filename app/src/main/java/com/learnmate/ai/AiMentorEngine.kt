package com.learnmate.ai

interface AiMentorEngine {
    suspend fun askMentor(
        question: String,
        profile: StudentProfile,
        difficulty: String,
        hintOnly: Boolean
    ): String

    suspend fun askMentorStream(
        question: String,
        profile: StudentProfile,
        difficulty: String,
        hintOnly: Boolean,
        onToken: (String) -> Unit
    ): String {
        val reply = askMentor(question, profile, difficulty, hintOnly)
        onToken(reply)
        return reply
    }
}

object MockAiMentorEngine : AiMentorEngine {
    override suspend fun askMentor(
        question: String,
        profile: StudentProfile,
        difficulty: String,
        hintOnly: Boolean
    ): String {
        val langNote = if (profile.preferredLanguage != "English") "[${profile.preferredLanguage} Mode] " else ""
        return if (hintOnly) {
            "${langNote}Hint ($difficulty): Break '$question' into smaller steps in ${profile.subject}."
        } else {
            "${langNote}Explanation ($difficulty level for ${profile.subject}):\n1. Core idea behind '$question'.\n2. Worked example.\n3. Practice next."
        }
    }
}
