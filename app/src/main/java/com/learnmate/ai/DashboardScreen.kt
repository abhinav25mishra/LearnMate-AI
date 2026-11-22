package com.learnmate.ai

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ImprovementGraphCard(
    assessmentHistory: List<AssessmentRecord>,
    topicMasteryPct: Int
) {
    // Chronological percentage points (oldest -> newest)
    val points: List<Pair<String, Float>> = remember(assessmentHistory, topicMasteryPct) {
        if (assessmentHistory.isEmpty()) {
            listOf(
                "Start" to 10f,
                "Roadmap" to topicMasteryPct.coerceAtLeast(15).toFloat()
            )
        } else {
            val chronological = assessmentHistory.reversed()
            listOf("Baseline" to 20f) + chronological.mapIndexed { idx, rec ->
                val pct = ((rec.score * 100f) / rec.total.coerceAtLeast(1)).coerceIn(0f, 100f)
                "Test ${idx + 1}" to pct
            }
        }
    }

    val lineColor = MaterialTheme.colorScheme.secondary
    val dotColor = MaterialTheme.colorScheme.primary
    val gridColor = MaterialTheme.colorScheme.outlineVariant

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.TrendingUp, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                    Text("Learning Improvement Graph", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                val latestPct = points.lastOrNull()?.second?.toInt() ?: 0
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = "Current: $latestPct%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                val w = size.width
                val h = size.height

                // Horizontal reference grid lines (0%, 50%, 100%)
                listOf(0f, 0.5f, 1f).forEach { ratio ->
                    val y = h * ratio
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                if (points.size >= 2) {
                    val stepX = w / (points.size - 1).coerceAtLeast(1)
                    val path = Path()
                    val coords = points.mapIndexed { idx, (_, pct) ->
                        val x = idx * stepX
                        val y = h - (pct / 100f) * h
                        Offset(x, y)
                    }

                    coords.forEachIndexed { idx, pt ->
                        if (idx == 0) path.moveTo(pt.x, pt.y) else path.lineTo(pt.x, pt.y)
                    }

                    drawPath(
                        path = path,
                        color = lineColor,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    coords.forEach { pt ->
                        drawCircle(color = Color.White, radius = 5.dp.toPx(), center = pt)
                        drawCircle(color = dotColor, radius = 3.5.dp.toPx(), center = pt)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                points.forEach { (label, pct) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${pct.toInt()}%", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardScreen(
    profile: StudentProfile,
    visitCount: Int,
    onSwitchTopic: (String) -> Unit,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val subjectTopics = remember(profile.subject) { LearningEngine.getSubjectTopics(profile.subject) }
    val exploredTopics = remember(profile.name, profile.subject) { ProfileStorage.getExploredTopics(context, profile.name, profile.subject) }
    val assessmentHistory = remember(profile.name) { ProfileStorage.loadAssessmentScores(context, profile.name) }
    val activeDates = remember(profile.name) { ProfileStorage.getActiveDateStrings(context, profile.name) }
    var showPasscode by remember { mutableStateOf(false) }
    var customTopicInput by remember { mutableStateOf("") }

    val masteryPct = if (subjectTopics.isEmpty()) 0 else (exploredTopics.size * 100) / subjectTopics.size
    val latestScoreText = assessmentHistory.firstOrNull()?.let { "${it.score}/${it.total}" } ?: "0/10"

    val availableTopics = listOf(
        "Python",
        "C Programming",
        "SQL & Databases",
        "Data Structures & Algorithms",
        "History",
        "Geography"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Profile & Avatar Header Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF0F172A), Color(0xFF1E3A8A), Color(0xFF0D9488))
                    ),
                    shape = RoundedCornerShape(22.dp)
                )
                .padding(22.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Surface(
                        modifier = Modifier.size(66.dp),
                        shape = CircleShape,
                        color = Color(0xFFDBEAFE),
                        border = BorderStroke(2.dp, Color(0xFF5EEAD4))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = profile.name.take(2).uppercase(),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1E3A8A)
                            )
                        }
                    }
                    VxBrandMark(sizeDp = 24)
                }

                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = profile.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "${profile.standard} • Joined ${profile.createdAt}",
                        fontSize = 12.sp,
                        color = Color(0xFFCCFBF1)
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.16f)
                    ) {
                        Text(
                            text = "Learning: ${profile.subject} • ${profile.preferredLanguage}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // 4-Metric Activity Strip
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DashboardStatCard(title = "Visits", value = "$visitCount", modifier = Modifier.weight(1f))
            DashboardStatCard(title = "Active Days", value = "${activeDates.size.coerceAtLeast(1)}", modifier = Modifier.weight(1f))
            DashboardStatCard(title = "Assessment", value = latestScoreText, modifier = Modifier.weight(1f))
            DashboardStatCard(title = "Progress", value = "$masteryPct%", modifier = Modifier.weight(1f))
        }

        // Visual Improvement Graph
        ImprovementGraphCard(
            assessmentHistory = assessmentHistory,
            topicMasteryPct = masteryPct
        )

        // Change / Search Active Topic (Updates Roadmap, Assessment & AI Mentor Guardrail)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.School, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Active Topic (Roadmap & AI Tutor Scope)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                OutlinedTextField(
                    value = customTopicInput,
                    onValueChange = { customTopicInput = it },
                    placeholder = { Text("Enter any topic (e.g., History, Geography, Python)...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    trailingIcon = {
                        if (customTopicInput.isNotBlank()) {
                            TextButton(onClick = {
                                onSwitchTopic(customTopicInput.trim())
                                customTopicInput = ""
                            }) {
                                Text("Set", fontWeight = FontWeight.Bold)
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    availableTopics.chunked(2).forEach { rowTopics ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            rowTopics.forEach { topic ->
                                FilterChip(
                                    selected = profile.subject.equals(topic, ignoreCase = true),
                                    onClick = { onSwitchTopic(topic) },
                                    label = { Text(topic, fontSize = 12.sp, maxLines = 1) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Saved Account Details & Passcode Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.AccountCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Student Profile & Security", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                DashInfoRow("Learner Name", profile.name)
                DashInfoRow("Username", "@${profile.username}")
                DashInfoRow("Standard", profile.standard)
                DashInfoRow("Courses", profile.courses)
                DashInfoRow("Focus Areas", profile.interests)
                DashInfoRow("Study Plan", "${profile.dailyHours} hrs/day • ${profile.targetDate}")
                DashInfoRow("Language", profile.preferredLanguage)
                DashInfoRow("App Open Count", "$visitCount")
                DashInfoRow("Active Days", "${activeDates.size.coerceAtLeast(1)} day(s)")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                        Text("Saved Passcode / PIN", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (showPasscode) profile.passcode else "•".repeat(profile.passcode.length.coerceAtLeast(4)),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showPasscode = !showPasscode }, modifier = Modifier.size(30.dp)) {
                            Icon(
                                imageVector = if (showPasscode) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle Passcode",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Assessment Score History Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                    Text("Assessment Score History", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                if (assessmentHistory.isEmpty()) {
                    Text(
                        text = "No assessments submitted yet. Open the Assessment tab to take the 10-MCQ quiz.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    assessmentHistory.forEach { rec ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(rec.subject, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("${rec.assignedLevel} • ${rec.timestamp}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = "${rec.score}/${rec.total} (${(rec.score * 100) / rec.total.coerceAtLeast(1)}%)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Sign Out / Switch Account Button
        OutlinedButton(
            onClick = onSignOut,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sign Out / Switch Account", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun DashboardStatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
            Text(title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun DashInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}


