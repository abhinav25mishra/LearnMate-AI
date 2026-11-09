package com.learnmate.ai

data class DiagnosticQuestion(
    val id: Int,
    val topic: String,
    val prerequisite: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int
)

data class KnowledgeGapReport(
    val assignedLevel: String,
    val strongTopics: List<String>,
    val weakTopics: List<String>,
    val missingPrerequisites: List<String>
)

data class RoadmapModule(
    val id: String,
    val title: String,
    val prerequisite: String,
    val dailyTask: String,
    val estimatedMinutes: Int,
    val isRevision: Boolean = false,
    val isCompleted: Boolean = false
)

object LearningEngine {

    fun getDiagnosticQuestions(subject: String): List<DiagnosticQuestion> {
        return listOf(
            DiagnosticQuestion(
                id = 1,
                topic = "Variables & Data Types",
                prerequisite = "Basic Memory & Types",
                question = "Which data type best stores a true/false value in $subject?",
                options = listOf("Integer", "Boolean", "String", "Float"),
                correctIndex = 1
            ),
            DiagnosticQuestion(
                id = 2,
                topic = "Control Flow (Loops)",
                prerequisite = "Boolean Logic & Conditions",
                question = "When should you use a 'while' loop instead of a 'for' loop?",
                options = listOf(
                    "When the exact number of iterations is unknown",
                    "Only when printing text",
                    "When declaring variables",
                    "Never, they are identical"
                ),
                correctIndex = 0
            ),
            DiagnosticQuestion(
                id = 3,
                topic = "Functions & Scope",
                prerequisite = "Variables & Control Flow",
                question = "What is the main purpose of a function?",
                options = listOf(
                    "To slow down execution",
                    "To reuse modular blocks of logic",
                    "To replace all variables",
                    "To delete memory"
                ),
                correctIndex = 1
            ),
            DiagnosticQuestion(
                id = 4,
                topic = "Data Structures (Lists/Arrays)",
                prerequisite = "Indexing & Loops",
                question = "What is the index of the first element in a standard 0-indexed list?",
                options = listOf("1", "-1", "0", "Depends on size"),
                correctIndex = 2
            )
        )
    }

    fun evaluateDiagnostic(
        questions: List<DiagnosticQuestion>,
        selectedAnswers: Map<Int, Int>
    ): KnowledgeGapReport {
        val strong = mutableListOf<String>()
        val weak = mutableListOf<String>()
        val missingPrereqs = mutableListOf<String>()

        questions.forEach { q ->
            val chosen = selectedAnswers[q.id]
            if (chosen == q.correctIndex) {
                strong.add(q.topic)
            } else {
                weak.add(q.topic)
                missingPrereqs.add(q.prerequisite)
            }
        }

        val level = when {
            strong.size >= 4 -> "Intermediate"
            strong.size >= 2 -> "Developing Beginner"
            else -> "Foundational Beginner"
        }

        return KnowledgeGapReport(
            assignedLevel = level,
            strongTopics = strong,
            weakTopics = weak,
            missingPrerequisites = missingPrereqs.distinct()
        )
    }

    fun generateRoadmap(
        profile: StudentProfile,
        report: KnowledgeGapReport
    ): List<RoadmapModule> {
        val modules = mutableListOf<RoadmapModule>()
        val dailyMins = ((profile.dailyHours.toFloatOrNull() ?: 1.5f) * 60).toInt().coerceIn(30, 180)

        // Prioritize weak topics & their prerequisites first
        report.weakTopics.forEachIndexed { index, topic ->
            val prereq = report.missingPrerequisites.getOrNull(index) ?: "Core Foundations"
            modules.add(
                RoadmapModule(
                    id = "weak_$index",
                    title = "Master $topic",
                    prerequisite = prereq,
                    dailyTask = "Review $prereq, then solve 3 guided exercises on $topic (${profile.preferredLanguage} notes).",
                    estimatedMinutes = dailyMins
                )
            )
        }

        // Add next-level progression modules
        modules.add(
            RoadmapModule(
                id = "next_1",
                title = "${profile.subject}: Applied Problem Solving",
                prerequisite = report.weakTopics.firstOrNull() ?: "Core Syntax",
                dailyTask = "Build a mini-project combining ${profile.interests} with ${profile.subject}.",
                estimatedMinutes = dailyMins
            )
        )

        modules.add(
            RoadmapModule(
                id = "rev_1",
                title = "Weekly Spaced Revision",
                prerequisite = "Completed Modules",
                dailyTask = "15-minute active recall quiz on weak concepts + mistake review.",
                estimatedMinutes = 30,
                isRevision = true
            )
        )

        return modules
    }

    fun adaptRoadmapForStruggle(
        currentRoadmap: List<RoadmapModule>,
        struggledTopic: String
    ): List<RoadmapModule> {
        val remediation = RoadmapModule(
            id = "adapt_${System.currentTimeMillis()}",
            title = "Adaptive Boost: $struggledTopic",
            prerequisite = "Detected Quiz Struggle",
            dailyTask = "Extra step-by-step breakdown + 5 foundational practice problems on $struggledTopic.",
            estimatedMinutes = 45,
            isRevision = true
        )
        return listOf(remediation) + currentRoadmap
    }
}