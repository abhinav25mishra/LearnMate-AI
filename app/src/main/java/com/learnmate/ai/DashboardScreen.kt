package com.learnmate.ai

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
fun DashboardScreen(
    profile: StudentProfile,
    visitCount: Int,
    onSwitchTopic: (String) -> Unit,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val roadmap = remember { AppStorage.loadRoadmap(context) }
    val gapReport = remember { AppStorage.loadGapReport(context) }
    val assessmentHistory = remember(profile.name) { ProfileStorage.loadAssessmentScores(context, profile.name) }
    var showPasscode by remember { mutableStateOf(false) }

    val completedModules = roadmap.count { it.isCompleted }
    val masteryPct = if (roadmap.isEmpty()) 0 else (completedModules * 100) / roadmap.size
    val latestScoreText = assessmentHistory.firstOrNull()?.let { "${it.score}/${it.total}" } ?: "Not Taken"

    val availableTopics = listOf("Python", "Data Structures & Algorithms", "SQL & Databases", "Machine Learning & AI", "Mathematics")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Profile & Identity Header Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF0F172A), Color(0xFF1E3A8A), Color(0xFF0D9488))
                    ),
                    shape = RoundedCornerShape(22.dp)
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Surface(
                        modifier = Modifier.size(64.dp),
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
                        fontSize = 21.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "VicharaX Learner • Joined ${profile.createdAt}",
                        fontSize = 12.sp,
                        color = Color(0xFFCCFBF1)
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.16f)
                    ) {
                        Text(
                            text = "Active Topic: ${profile.subject} • ${gapReport?.assignedLevel ?: profile.skillLevel}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // 3-Metric Telemetry Strip (App Visits, Latest Assessment Score, Roadmap Mastery)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DashboardStatCard(
                title = "App Visits",
                value = "$visitCount",
                subtitle = "Sessions",
                modifier = Modifier.weight(1f)
            )
            DashboardStatCard(
                title = "Assessment",
                value = latestScoreText,
                subtitle = "${assessmentHistory.size} attempts",
                modifier = Modifier.weight(1f)
            )
            DashboardStatCard(
                title = "Mastery",
                value = "$masteryPct%",
                subtitle = "$completedModules/${roadmap.size} modules",
                modifier = Modifier.weight(1f)
            )
        }

        // Active AI Mentor Topic Selector (Directly controls TopicGuardrail)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.School, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Active Learning Topic (Mentor Scope)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Text(
                    text = "VicharaX enforces a strict topic barrier in AI Mentor based on your selected topic:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
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
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.AccountCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Saved Sign-Up Details & Credentials", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                DashInfoRow("Account Name", profile.name)
                DashInfoRow("Selected Interests", profile.interests)
                DashInfoRow("Learning Goal", profile.goal)
                DashInfoRow("Study Commitment", "${profile.dailyHours} hrs/day • ${profile.targetDate}")
                DashInfoRow("Preferred Language", profile.preferredLanguage)
                DashInfoRow("Total App Opens", "$visitCount times")

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
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                    Text("Assessment Score History", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                if (assessmentHistory.isEmpty()) {
                    Text(
                        text = "No assessments submitted yet. Open the Assessment tab to take your 10-MCQ diagnostic test.",
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
                .height(50.dp),
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
private fun DashboardStatCard(title: String, value: String, subtitle: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
            Text(subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.SemiBold)
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
