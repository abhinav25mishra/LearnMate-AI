package com.learnmate.ai

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Quiz
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
fun RoadmapScreen(
    profile: StudentProfile,
    strings: UiStrings = StringsManager.getStrings(profile.preferredLanguage),
    onNavigateToAssessment: () -> Unit = {}
) {
    val context = LocalContext.current
    val questions = remember(profile.subject) {
        LearningEngine.getDiagnosticQuestions(profile.subject)
    }
    var gapReport by remember { mutableStateOf(AppStorage.loadGapReport(context)) }
    var roadmap by remember { mutableStateOf(AppStorage.loadRoadmap(context)) }
    var flashcardIndex by remember { mutableStateOf(0) }
    var isCardFlipped by remember { mutableStateOf(false) }
    var masteredCards by remember { mutableStateOf(emptySet<Int>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (gapReport == null || roadmap.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF1E3A8A), Color(0xFF0D9488))
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Map, contentDescription = null, tint = Color(0xFF5EEAD4))
                        Text(
                            text = "Personalized Study Roadmap",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                    Text(
                        text = "Take the 10-question diagnostic assessment in the Assessment tab first so VicharaX can map your weak prerequisites and generate your weekly timeline.",
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = Color(0xFFE0F2FE)
                    )
                    Button(
                        onClick = onNavigateToAssessment,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF1E3A8A)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Start 10-MCQ Assessment", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            val report = gapReport!!
            val completedCount = roadmap.count { it.isCompleted }
            val progressFraction = if (roadmap.isEmpty()) 0f else completedCount.toFloat() / roadmap.size

            // Overall Roadmap Mastery Banner
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
                        Text("Roadmap Mastery", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("${(progressFraction * 100).toInt()}%", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF5EEAD4))
                    }
                    LinearProgressIndicator(
                        progress = { progressFraction },
                        color = Color(0xFF5EEAD4),
                        trackColor = Color.White.copy(alpha = 0.25f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                    Text(
                        text = "$completedCount of ${roadmap.size} modules completed • Assigned Level: ${report.assignedLevel}",
                        fontSize = 12.sp,
                        color = Color(0xFFE0F2FE)
                    )
                }
            }

            // Visual Skill-Gap Bar Chart Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.BarChart, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(strings.gapAnalysisTitle, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    report.strongTopics.forEach { topic ->
                        SkillBarRow(topic = topic, scorePercent = 95, isGap = false)
                    }
                    report.weakTopics.forEach { topic ->
                        SkillBarRow(topic = topic, scorePercent = 30, isGap = true)
                    }
                    if (report.strongTopics.isEmpty() && report.weakTopics.isEmpty()) {
                        SkillBarRow(topic = profile.subject, scorePercent = 85, isGap = false)
                    }

                    if (report.missingPrerequisites.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Prioritized Prerequisites: ${report.missingPrerequisites.joinToString(", ")}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }

            // Interactive Concept Flashcard Arena
            if (questions.isNotEmpty()) {
                val activeIndex = flashcardIndex % questions.size
                val activeQ = questions[activeIndex]
                val cardSurfaceColor by animateColorAsState(
                    targetValue = if (isCardFlipped) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    animationSpec = tween(300),
                    label = "cardFlipColor"
                )
                val cardTextColor = if (isCardFlipped) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.Flip, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
                                Text(strings.flashcardsTitle, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "Mastered ${masteredCards.size}/${questions.size}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Surface(
                            onClick = { isCardFlipped = !isCardFlipped },
                            shape = RoundedCornerShape(16.dp),
                            color = cardSurfaceColor,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (isCardFlipped) "ANSWER & PREREQUISITE" else "CONCEPT CHECK • TAP TO FLIP",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isCardFlipped) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "${activeIndex + 1}/${questions.size}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = if (isCardFlipped) {
                                        "Answer: ${activeQ.options[activeQ.correctIndex]}\n\nFoundation: ${activeQ.prerequisite}"
                                    } else {
                                        activeQ.question
                                    },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    lineHeight = 20.sp,
                                    color = cardTextColor
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = {
                                    masteredCards = if (masteredCards.contains(activeQ.id)) {
                                        masteredCards - activeQ.id
                                    } else {
                                        masteredCards + activeQ.id
                                    }
                                },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (masteredCards.contains(activeQ.id)) "Mastered" else "Got It", fontSize = 12.sp)
                            }

                            TextButton(
                                onClick = {
                                    isCardFlipped = false
                                    flashcardIndex = (flashcardIndex + 1) % questions.size
                                }
                            ) {
                                Text("Next Card", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // Personalised Weekly Timeline
            Text(
                text = strings.weeklyModulesTitle,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            roadmap.forEachIndexed { index, module ->
                val weekLabel = "W${index + 1}"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    if (module.isCompleted) MaterialTheme.colorScheme.secondary
                                    else MaterialTheme.colorScheme.primaryContainer
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = weekLabel,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (module.isCompleted) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        color = if (module.isCompleted) {
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                        border = BorderStroke(
                            1.dp,
                            if (module.isCompleted) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outlineVariant
                        ),
                        shadowElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (module.isRevision) {
                                        MaterialTheme.colorScheme.tertiaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    }
                                ) {
                                    Text(
                                        text = if (module.isRevision) "ADAPTIVE REVISION" else "${module.estimatedMinutes} MIN SESSION",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (module.isRevision) {
                                            MaterialTheme.colorScheme.onTertiaryContainer
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        },
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                if (module.isCompleted) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Completed",
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Text(
                                text = module.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Prerequisite: ${module.prerequisite}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = module.dailyTask,
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                            )

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
                                        containerColor = if (module.isCompleted) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
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
                                    Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(strings.adaptPlan, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            OutlinedButton(
                onClick = onNavigateToAssessment,
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Go to Assessment Tab")
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
            Text(topic, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Text(
                text = if (isGap) "$scorePercent% (Gap Detected)" else "$scorePercent% (Mastered)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isGap) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary
            )
        }
        LinearProgressIndicator(
            progress = { animatedProgress },
            color = if (isGap) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
        )
    }
}
