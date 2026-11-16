package com.learnmate.ai

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class DomainOption(val title: String, val icon: ImageVector)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(onComplete: (StudentProfile) -> Unit) {
    var name by remember { mutableStateOf("") }
    var selectedDomain by remember { mutableStateOf("Technical Roadmaps") }
    var subject by remember { mutableStateOf("Python Programming") }
    var goal by remember { mutableStateOf("Master Python & Edge AI") }
    var skillLevel by remember { mutableStateOf("Beginner") }
    var dailyHours by remember { mutableStateOf("2") }
    var targetDate by remember { mutableStateOf("30 Days") }
    var preferredLanguage by remember { mutableStateOf("English") }
    var interests by remember { mutableStateOf("Problem Solving, AI Apps") }

    val domains = listOf(
        DomainOption("Technical Roadmaps", Icons.Default.Code),
        DomainOption("Exam Preparation", Icons.Default.School),
        DomainOption("Career Guidance", Icons.Default.WorkOutline),
        DomainOption("Skill Development", Icons.Default.Psychology),
        DomainOption("Certification Paths", Icons.Default.Verified),
        DomainOption("Lifelong Learning", Icons.Default.Explore)
    )
    val subjects = listOf("Python Programming", "Data Structures", "Engineering Mathematics", "Digital Electronics")
    val levels = listOf("Beginner", "Intermediate")
    val languages = listOf("English", "Hindi", "Tamil", "Telugu", "Bengali", "Marathi")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Branded Hero Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF1E3A8A), Color(0xFF1D4ED8), Color(0xFF0D9488))
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(22.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.16f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF7DD3FC), modifier = Modifier.size(14.dp))
                        Text(
                            text = "BHARAT AI-SOC • 100% ON-DEVICE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                Text(
                    text = "Build Your AI Learning Path",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "Powered locally by Llama 3.2 & ExecuTorch on Arm mobile SoC. Zero cloud, complete privacy.",
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = Color(0xFFE0F2FE)
                )
            }
        }

        // Step 1: Learner Identity & Domain
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                StepHeader(stepNumber = "1", title = "Learner Identity & Domain", icon = Icons.Default.Person)

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Your Name") },
                    placeholder = { Text("e.g., Abhinav") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )

                Text("Select Focus Domain", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    domains.forEach { domain ->
                        val selected = selectedDomain == domain.title
                        FilterChip(
                            selected = selected,
                            onClick = { selectedDomain = domain.title },
                            leadingIcon = {
                                Icon(
                                    imageVector = domain.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            label = { Text(domain.title, fontSize = 12.sp) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }

        // Step 2: Subject & Proficiency
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                StepHeader(stepNumber = "2", title = "Subject & Target Ambition", icon = Icons.Default.School)

                Text("Core Subject", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    subjects.forEach { item ->
                        FilterChip(
                            selected = subject == item,
                            onClick = { subject = item },
                            label = { Text(item, fontSize = 12.sp) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = goal,
                    onValueChange = { goal = it },
                    label = { Text("Career / Exam Goal") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )

                Text("Current Proficiency Level", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    levels.forEach { lvl ->
                        FilterChip(
                            selected = skillLevel == lvl,
                            onClick = { skillLevel = lvl },
                            label = { Text(lvl, fontWeight = FontWeight.SemiBold) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }

        // Step 3: Schedule & Regional Language
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                StepHeader(stepNumber = "3", title = "Study Pace & Regional Language", icon = Icons.Default.Schedule)

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = dailyHours,
                        onValueChange = { dailyHours = it },
                        label = { Text("Hours / Day") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp)
                    )
                    OutlinedTextField(
                        value = targetDate,
                        onValueChange = { targetDate = it },
                        label = { Text("Duration") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp)
                    )
                }

                OutlinedTextField(
                    value = interests,
                    onValueChange = { interests = it },
                    label = { Text("Personal Interests (for relatable analogies)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.secondary)
                    Text("Preferred Teaching Language", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    languages.forEach { lang ->
                        FilterChip(
                            selected = preferredLanguage == lang,
                            onClick = { preferredLanguage = lang },
                            label = { Text(lang, fontSize = 12.sp) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }

        // Live Plan Summary Preview Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "READY TO INITIALIZE ON-DEVICE PROFILE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${name.ifBlank { "Student" }} • $subject ($skillLevel)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = "${dailyHours.ifBlank { "2" }} hrs/day for ${targetDate.ifBlank { "30 Days" }} • Teaching in $preferredLanguage",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Button(
            onClick = {
                onComplete(
                    StudentProfile(
                        name = name.ifBlank { "Student" },
                        subject = subject,
                        goal = "[$selectedDomain] ${goal.ifBlank { "Master $subject" }}",
                        skillLevel = skillLevel,
                        dailyHours = dailyHours.ifBlank { "2" },
                        targetDate = targetDate.ifBlank { "30 Days" },
                        preferredLanguage = preferredLanguage,
                        interests = interests.ifBlank { "Problem Solving" }
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Launch My Learning Companion", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
    }
}

@Composable
private fun StepHeader(stepNumber: String, title: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stepNumber,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.weight(1f))
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
    }
}