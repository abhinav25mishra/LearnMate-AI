package com.learnmate.ai

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RoadmapScreen(profile: StudentProfile) {
    val questions = remember(profile.subject) {
        LearningEngine.getDiagnosticQuestions(profile.subject)
    }
    var selectedAnswers by remember { mutableStateOf(emptyMap<Int, Int>()) }
    var gapReport by remember { mutableStateOf<KnowledgeGapReport?>(null) }
    var roadmap by remember { mutableStateOf(emptyList<RoadmapModule>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (gapReport == null) {
            Text(
                text = "Diagnostic Assessment",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Answer these quick questions so LearnMate AI can detect your exact knowledge gaps.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            questions.forEach { q ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("${q.id}. ${q.question}", fontWeight = FontWeight.SemiBold)
                        q.options.forEachIndexed { idx, option ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                RadioButton(
                                    selected = selectedAnswers[q.id] == idx,
                                    onClick = {
                                        selectedAnswers = selectedAnswers + (q.id to idx)
                                    }
                                )
                                Text(
                                    text = option,
                                    modifier = Modifier.padding(top = 12.dp)
                                )
                            }
                        }
                    }
                }
            }

            Button(
                onClick = {
                    val report = LearningEngine.evaluateDiagnostic(questions, selectedAnswers)
                    gapReport = report
                    roadmap = LearningEngine.generateRoadmap(profile, report)
                },
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Analyze Knowledge Gaps & Build Roadmap")
            }
        } else {
            val report = gapReport!!
            Text("Knowledge-Gap Report", fontSize = 22.sp, fontWeight = FontWeight.Bold)

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Assigned Level: ${report.assignedLevel}", fontWeight = FontWeight.Bold)
                    Text("Strong Topics: ${report.strongTopics.ifEmpty { listOf("None yet") }.joinToString(", ")}")
                    Text("Weak Topics: ${report.weakTopics.ifEmpty { listOf("None! Great job") }.joinToString(", ")}")
                    Text("Missing Prerequisites: ${report.missingPrerequisites.ifEmpty { listOf("None") }.joinToString(", ")}")
                }
            }

            Text("Personalised Learning Roadmap", fontSize = 20.sp, fontWeight = FontWeight.Bold)

            roadmap.forEach { module ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = if (module.isRevision) "🔄 ${module.title}" else "📘 ${module.title}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text("Prerequisite: ${module.prerequisite}", fontSize = 12.sp)
                        Text("Task: ${module.dailyTask}", fontSize = 14.sp)
                        Text("Est. Time: ${module.estimatedMinutes} mins", fontSize = 12.sp)

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    roadmap = roadmap.map {
                                        if (it.id == module.id) it.copy(isCompleted = !it.isCompleted) else it
                                    }
                                }
                            ) {
                                Text(if (module.isCompleted) "✅ Completed" else "Mark Complete")
                            }
                            TextButton(
                                onClick = {
                                    roadmap = LearningEngine.adaptRoadmapForStruggle(roadmap, module.title)
                                }
                            ) {
                                Text("Struggled? Adapt Plan")
                            }
                        }
                    }
                }
            }

            OutlinedButton(
                onClick = {
                    gapReport = null
                    selectedAnswers = emptyMap()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Retake Diagnostic")
            }
        }
    }
}