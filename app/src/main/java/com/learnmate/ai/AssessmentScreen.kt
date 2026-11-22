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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AssessmentScreen(
    profile: StudentProfile,
    strings: UiStrings = StringsManager.getStrings(profile.preferredLanguage),
    onNavigateToRoadmap: () -> Unit = {}
) {
    val context = LocalContext.current
    var activeAssessmentTopic by remember(profile.subject) { mutableStateOf(profile.subject) }
    var topicSearchInput by remember { mutableStateOf("") }

    val questions = remember(activeAssessmentTopic) {
        LearningEngine.getDiagnosticQuestions(activeAssessmentTopic)
    }
    var selectedAnswers by remember(activeAssessmentTopic) { mutableStateOf(emptyMap<Int, Int>()) }
    var isSubmitted by remember(activeAssessmentTopic) { mutableStateOf(false) }
    var evaluationReport by remember(activeAssessmentTopic) { mutableStateOf<KnowledgeGapReport?>(null) }

    val correctCount = remember(selectedAnswers, isSubmitted, questions) {
        questions.count { q -> selectedAnswers[q.id] == q.correctIndex }
    }
    val optionLetters = listOf("A", "B", "C", "D")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Compact Assessment Header Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF0F172A), Color(0xFF1E3A8A), Color(0xFF0D9488))
                    ),
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Quiz, contentDescription = null, tint = Color(0xFF5EEAD4), modifier = Modifier.size(18.dp))
                        Text(
                            text = "Assessment: $activeAssessmentTopic",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White.copy(alpha = 0.18f)
                    ) {
                        Text(
                            text = if (isSubmitted) "$correctCount/${questions.size}" else "${selectedAnswers.size}/${questions.size}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Text(
                    text = if (isSubmitted) {
                        "Evaluated! Review your Correct / Incorrect status and the exact Correct Answer below each question."
                    } else {
                        "Answer all ${questions.size} topic-specific MCQs below or search any subject to generate a new 10-MCQ quiz."
                    },
                    fontSize = 12.sp,
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
                        .height(5.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }
        }

        // Instant Topic Search Bar for Generating 10 MCQs on ANY Subject (Physics, Math, History, etc.)
        OutlinedTextField(
            value = topicSearchInput,
            onValueChange = { topicSearchInput = it },
            placeholder = { Text("Generate 10 MCQs for any topic (e.g., Physics, Math)...", fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
            trailingIcon = {
                if (topicSearchInput.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(
                            onClick = {
                                activeAssessmentTopic = topicSearchInput.trim()
                                ProfileStorage.recordActivityToday(context, profile.name)
                            }
                        ) {
                            Text("Generate", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        IconButton(
                            onClick = {
                                topicSearchInput = ""
                                activeAssessmentTopic = profile.subject
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Reset", modifier = Modifier.size(16.dp))
                        }
                    }
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

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
                        Text("Assessment Score", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = "$correctCount/${questions.size} ($scorePct%) • ${report.assignedLevel}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

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

        // 10 MCQ Cards with Shuffled A/B/C/D Options & Per-Question Feedback
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = "Q${q.id} • ${q.topic}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        if (isSubmitted) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isCorrect) Color(0xFFD1FAE5) else Color(0xFFFEE2E2)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
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
                                        color = if (isCorrect) Color(0xFF065F46) else Color(0xFF991B1B),
                                        maxLines = 1,
                                        softWrap = false,
                                        overflow = TextOverflow.Clip
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
                        val letter = optionLetters.getOrElse(idx) { "${idx + 1}" }

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
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
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
                                    text = "$letter. $option",
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected || isRightOption) FontWeight.SemiBold else FontWeight.Normal,
                                    color = optionTextColor
                                )
                            }
                        }
                    }

                    // Explicit Correct Answer Feedback Callout when User's Answer is Incorrect
                    if (isWrong) {
                        val correctLetter = optionLetters.getOrElse(q.correctIndex) { "" }
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
                                    text = "Correct Answer: $correctLetter. $correctText",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF065F46)
                                )
                                Text(
                                    text = "Core Concept: ${q.prerequisite}",
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
                    val newRoadmap = LearningEngine.generateRoadmap(profile.copy(subject = activeAssessmentTopic), report)
                    evaluationReport = report
                    isSubmitted = true
                    AppStorage.saveRoadmapData(context, report, newRoadmap)
                    val scoreVal = questions.count { q -> selectedAnswers[q.id] == q.correctIndex }
                    val timeStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date())
                    ProfileStorage.saveAssessmentScore(
                        context,
                        profile.name,
                        AssessmentRecord(activeAssessmentTopic, scoreVal, questions.size, report.assignedLevel, timeStr)
                    )
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

