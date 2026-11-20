package com.learnmate.ai

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AssessmentScreen(
    profile: StudentProfile,
    strings: UiStrings = StringsManager.getStrings(profile.preferredLanguage),
    onNavigateToRoadmap: () -> Unit = {}
) {
    val context = LocalContext.current
    val questions = remember(profile.subject) {
        LearningEngine.getDiagnosticQuestions(profile.subject)
    }
    var selectedAnswers by remember { mutableStateOf(emptyMap<Int, Int>()) }
    var isSubmitted by remember { mutableStateOf(false) }
    var evaluationReport by remember { mutableStateOf(AppStorage.loadGapReport(context)) }

    val correctCount = remember(selectedAnswers, isSubmitted) {
        questions.count { q -> selectedAnswers[q.id] == q.correctIndex }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF1E3A8A), Color(0xFF0D9488))
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Quiz, contentDescription = null, tint = Color(0xFF5EEAD4))
                        Text(
                            text = "Skill Assessment (${profile.subject})",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White.copy(alpha = 0.18f)
                    ) {
                        Text(
                            text = if (isSubmitted) "$correctCount/${questions.size} Score" else "${selectedAnswers.size}/${questions.size}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Text(
                    text = if (isSubmitted) {
                        "Assessment evaluated! Review each question below to see your status and the correct answers."
                    } else {
                        "Complete all ${questions.size} MCQs to diagnose skill gaps, unlock instant answer explanations, and calibrate your study roadmap."
                    },
                    fontSize = 13.sp,
                    color = Color(0xFFE0F2FE)
                )

                LinearProgressIndicator(
                    progress = {
                        if (isSubmitted) {
                            correctCount.toFloat() / questions.size.coerceAtLeast(1)
                        } else {
                            selectedAnswers.size.toFloat() / questions.size.coerceAtLeast(1)
                        }
                    },
                    color = Color(0xFF5EEAD4),
                    trackColor = Color.White.copy(alpha = 0.25f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }
        }

        // Post-Submission Summary Card
        if (isSubmitted && evaluationReport != null) {
            val report = evaluationReport!!
            val scorePct = (correctCount * 100) / questions.size.coerceAtLeast(1)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.secondary),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Assessment Result", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = "$scorePct% • ${report.assignedLevel}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Text(
                        text = "Strong Topics: ${report.strongTopics.size} • Weak Topics Mapped: ${report.weakTopics.size}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = onNavigateToRoadmap,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Open Roadmap", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }

                        OutlinedButton(
                            onClick = {
                                isSubmitted = false
                                selectedAnswers = emptyMap()
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Retake", fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // 10 MCQ Cards with Per-Question Evaluation Feedback
        questions.forEach { q ->
            val userChoice = selectedAnswers[q.id]
            val isCorrect = isSubmitted && userChoice == q.correctIndex
            val isWrong = isSubmitted && userChoice != q.correctIndex

            val cardBorderColor = when {
                isCorrect -> Color(0xFF10B981)
                isWrong -> Color(0xFFEF4444)
                else -> MaterialTheme.colorScheme.outlineVariant
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(if (isSubmitted) 1.5.dp else 1.dp, cardBorderColor),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = "Q${q.id} • ${q.topic}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        if (isSubmitted) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isCorrect) Color(0xFFD1FAE5) else Color(0xFFFEE2E2)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                        contentDescription = null,
                                        tint = if (isCorrect) Color(0xFF059669) else Color(0xFFDC2626),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = if (isCorrect) "Correct" else "Incorrect",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isCorrect) Color(0xFF065F46) else Color(0xFF991B1B)
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        text = "${q.id}. ${q.question}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    q.options.forEachIndexed { idx, option ->
                        val isSelected = userChoice == idx
                        val isRightOption = isSubmitted && idx == q.correctIndex
                        val isWrongSelection = isSubmitted && isSelected && idx != q.correctIndex

                        val optionBgColor = when {
                            isRightOption -> Color(0xFFD1FAE5)
                            isWrongSelection -> Color(0xFFFEE2E2)
                            isSelected -> MaterialTheme.colorScheme.primaryContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }

                        val optionTextColor = when {
                            isRightOption -> Color(0xFF065F46)
                            isWrongSelection -> Color(0xFF991B1B)
                            isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                            else -> MaterialTheme.colorScheme.onSurface
                        }

                        val optionBorder = when {
                            isRightOption -> BorderStroke(1.dp, Color(0xFF059669))
                            isWrongSelection -> BorderStroke(1.dp, Color(0xFFDC2626))
                            isSelected -> BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                            else -> null
                        }

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable(enabled = !isSubmitted) {
                                    selectedAnswers = selectedAnswers + (q.id to idx)
                                },
                            color = optionBgColor,
                            border = optionBorder
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = when {
                                        isRightOption -> Icons.Default.CheckCircle
                                        isWrongSelection -> Icons.Default.Cancel
                                        isSelected -> Icons.Default.CheckCircle
                                        else -> Icons.Default.RadioButtonUnchecked
                                    },
                                    contentDescription = null,
                                    tint = when {
                                        isRightOption -> Color(0xFF059669)
                                        isWrongSelection -> Color(0xFFDC2626)
                                        isSelected -> MaterialTheme.colorScheme.primary
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = option,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected || isRightOption) FontWeight.SemiBold else FontWeight.Normal,
                                    color = optionTextColor
                                )
                            }
                        }
                    }

                    // Explicit Correct Answer Feedback Callout when User's Answer is Incorrect
                    if (isWrong) {
                        val correctText = q.options.getOrElse(q.correctIndex) { "" }
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFEF2F2),
                            border = BorderStroke(1.dp, Color(0xFFFCA5A5))
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Correct Answer: $correctText",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF065F46)
                                )
                                Text(
                                    text = "Prerequisite Concept: ${q.prerequisite}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF7F1D1D)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Submit / Retake Action Button
        if (!isSubmitted) {
            Button(
                onClick = {
                    val report = LearningEngine.evaluateDiagnostic(questions, selectedAnswers)
                    val newRoadmap = LearningEngine.generateRoadmap(profile, report)
                    evaluationReport = report
                    isSubmitted = true
                    AppStorage.saveRoadmapData(context, report, newRoadmap)
                },
                enabled = selectedAnswers.size == questions.size,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (selectedAnswers.size == questions.size) {
                        "Submit Assessment & Check Answers"
                    } else {
                        "Answer All Questions (${selectedAnswers.size}/${questions.size})"
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            OutlinedButton(
                onClick = {
                    isSubmitted = false
                    selectedAnswers = emptyMap()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Retake 10-Question Assessment", fontWeight = FontWeight.Bold)
            }
        }
    }
}
