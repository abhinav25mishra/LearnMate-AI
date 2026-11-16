package com.learnmate.ai

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RoadmapScreen(profile: StudentProfile, strings: UiStrings = StringsManager.getStrings(profile.preferredLanguage)) {
    val context = LocalContext.current
    val questions = remember(profile.subject) {
        LearningEngine.getDiagnosticQuestions(profile.subject)
    }
    var selectedAnswers by remember { mutableStateOf(emptyMap<Int, Int>()) }
    var gapReport by remember { mutableStateOf(AppStorage.loadGapReport(context)) }
    var roadmap by remember { mutableStateOf(AppStorage.loadRoadmap(context)) }
    var flashcardIndex by remember { mutableStateOf(0) }
    var isCardFlipped by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (gapReport == null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Quiz, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(
                            text = "${strings.diagnosticTitle} (${profile.subject})",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Text(
                        text = "Answer ${questions.size} quick questions so the on-device engine can map missing prerequisites and build your weekly study plan.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    LinearProgressIndicator(
                        progress = { selectedAnswers.size.toFloat() / questions.size.coerceAtLeast(1) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }
            }

            questions.forEach { q ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = "Topic: ${q.topic}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Text("${q.id}. ${q.question}", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                        q.options.forEachIndexed { idx, option ->
                            val isSelected = selectedAnswers[q.id] == idx
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { selectedAnswers = selectedAnswers + (q.id to idx) },
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(option, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
            }

            Button(
                onClick = {
                    val report = LearningEngine.evaluateDiagnostic(questions, selectedAnswers)
                    val newRoadmap = LearningEngine.generateRoadmap(profile, report)
                    gapReport = report
                    roadmap = newRoadmap
                    AppStorage.saveRoadmapData(context, report, newRoadmap)
                },
                enabled = selectedAnswers.size == questions.size,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = if (selectedAnswers.size == questions.size) {
                        strings.analyzeBtn
                    } else {
                        "Answer All Questions (${selectedAnswers.size}/${questions.size})"
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            val report = gapReport!!
            val completedCount = roadmap.count { it.isCompleted }
            val progressFraction = if (roadmap.isEmpty()) 0f else completedCount.toFloat() / roadmap.size

            // Overall Roadmap Mastery Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Roadmap Mastery", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("${(progressFraction * 100).toInt()}%", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF5EEAD4))
                    }
                    LinearProgressIndicator(
                        progress = { progressFraction },
                        color = Color(0xFF14B8A6),
                        trackColor = Color(0xFF1E40AF),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                    Text(
                        text = "$completedCount of ${roadmap.size} modules completed • Level: ${report.assignedLevel}",
                        fontSize = 12.sp,
                        color = Color(0xFFDBEAFE)
                    )
                }
            }

            // Visual Skill-Gap Bar Chart Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.BarChart, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(strings.gapAnalysisTitle, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    HorizontalDivider()

                    report.strongTopics.forEach { topic ->
                        SkillBarRow(topic = topic, scorePercent = 95, isGap = false)
                    }
                    report.weakTopics.forEach { topic ->
                        SkillBarRow(topic = topic, scorePercent = 30, isGap = true)
                    }
                    if (report.strongTopics.isEmpty() && report.weakTopics.isEmpty()) {
                        SkillBarRow(topic = profile.subject, scorePercent = 80, isGap = false)
                    }

                    if (report.missingPrerequisites.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFEF3C7),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "🔑 Prioritized Prerequisites: ${report.missingPrerequisites.joinToString(", ")}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF92400E),
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }

            // Interactive Concept Flashcard Arena
            if (questions.isNotEmpty()) {
                val activeQ = questions[flashcardIndex % questions.size]
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = if (isCardFlipped) Color(0xFFF0FDFA) else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, if (isCardFlipped) Color(0xFF5EEAD4) else MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.Flip, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
                                Text(strings.flashcardsTitle, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                            Text(
                                text = "Card ${(flashcardIndex % questions.size) + 1}/${questions.size}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            onClick = { isCardFlipped = !isCardFlipped },
                            shape = RoundedCornerShape(14.dp),
                            color = if (isCardFlipped) Color(0xFFCCFBF1) else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = if (isCardFlipped) "ANSWER & PREREQUISITE" else "CONCEPT CHECK (TAP TO FLIP)",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCardFlipped) Color(0xFF0F766E) else MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = if (isCardFlipped) {
                                        "✅ ${activeQ.options[activeQ.correctIndex]}\n\nFoundation: ${activeQ.prerequisite}"
                                    } else {
                                        activeQ.question
                                    },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF0F172A)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(onClick = { isCardFlipped = !isCardFlipped }) {
                                Text(if (isCardFlipped) "Show Question" else "Reveal Answer")
                            }
                            TextButton(
                                onClick = {
                                    isCardFlipped = false
                                    flashcardIndex = (flashcardIndex + 1) % questions.size
                                }
                            ) {
                                Text("Next Card")
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // Weekly Study Modules
            Text(strings.weeklyModulesTitle, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            roadmap.forEachIndexed { index, module ->
                val weekLabel = "W${index + 1}"
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = if (module.isCompleted) Color(0xFFF0FDFA) else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, if (module.isCompleted) Color(0xFF99F6E4) else MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (module.isRevision) Color(0xFFFEF3C7) else MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = if (module.isRevision) "$weekLabel • REVISION" else "$weekLabel • ${module.estimatedMinutes} MIN",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (module.isRevision) Color(0xFF92400E) else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            if (module.isCompleted) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Completed", tint = Color(0xFF0D9488))
                            }
                        }

                        Text(module.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Prerequisite: ${module.prerequisite}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(module.dailyTask, fontSize = 14.sp)

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    val updated = roadmap.map {
                                        if (it.id == module.id) it.copy(isCompleted = !it.isCompleted) else it
                                    }
                                    roadmap = updated
                                    AppStorage.saveRoadmapData(context, report, updated)
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (module.isCompleted) Color(0xFF0D9488) else MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(if (module.isCompleted) strings.completedLabel else strings.markComplete, fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    val adapted = LearningEngine.adaptRoadmapForStruggle(roadmap, module.title)
                                    roadmap = adapted
                                    AppStorage.saveRoadmapData(context, report, adapted)
                                },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(strings.adaptPlan, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            OutlinedButton(
                onClick = {
                    gapReport = null
                    roadmap = emptyList()
                    selectedAnswers = emptyMap()
                    AppStorage.saveRoadmapData(context, null, emptyList())
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Retake Diagnostic Assessment")
            }
        }
    }
}

@Composable
private fun SkillBarRow(topic: String, scorePercent: Int, isGap: Boolean) {
    val animatedProgress by animateFloatAsState(
        targetValue = scorePercent / 100f,
        animationSpec = tween(durationMillis = 600),
        label = "skillBar"
    )
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(topic, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(
                text = if (isGap) "$scorePercent% (Gap Detected)" else "$scorePercent% (Mastered)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isGap) Color(0xFFD97706) else Color(0xFF0D9488)
            )
        }
        LinearProgressIndicator(
            progress = { animatedProgress },
            color = if (isGap) Color(0xFFF59E0B) else Color(0xFF0D9488),
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
        )
    }
}