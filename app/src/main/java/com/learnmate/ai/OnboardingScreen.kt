package com.learnmate.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
        "Technical Roadmaps",
        "Exam Preparation",
        "Career Guidance",
        "Skill Development",
        "Certification Paths",
        "Lifelong Learning"
    )
    val subjects = listOf("Python Programming", "Data Structures", "Engineering Mathematics", "Digital Electronics")
    val levels = listOf("Beginner", "Intermediate")
    val languages = listOf("English", "Hindi", "Tamil", "Telugu", "Bengali", "Marathi")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header Banner
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
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF7DD3FC))
                    Text(
                        text = "BHARAT AI-SOC • ON-DEVICE MENTOR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFBAE6FD)
                    )
                }
                Text(
                    text = "Welcome to LearnMate AI",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "100% offline personal tutor powered by Llama & ExecuTorch on Arm mobile SoC.",
                    fontSize = 13.sp,
                    color = Color(0xFFE0F2FE)
                )
            }
        }

        // Card 1: Learner Identity & Domain
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("1. Learner Profile & Domain", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Your Name") },
                    placeholder = { Text("e.g., Abhinav") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Text("Learning Domain", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    domains.forEach { domain ->
                        FilterChip(
                            selected = selectedDomain == domain,
                            onClick = { selectedDomain = domain },
                            label = { Text(domain, fontSize = 12.sp) }
                        )
                    }
                }
            }
        }

        // Card 2: Subject & Current Level
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.School, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                    Text("2. Subject & Target Goal", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Text("Core Subject", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    subjects.forEach { item ->
                        FilterChip(
                            selected = subject == item,
                            onClick = { subject = item },
                            label = { Text(item, fontSize = 12.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = goal,
                    onValueChange = { goal = it },
                    label = { Text("Target Ambition / Exam Goal") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Text("Current Proficiency", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    levels.forEach { lvl ->
                        FilterChip(
                            selected = skillLevel == lvl,
                            onClick = { skillLevel = lvl },
                            label = { Text(lvl) }
                        )
                    }
                }
            }
        }

        // Card 3: Schedule & Language
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("3. Pace & Regional Language", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = dailyHours,
                        onValueChange = { dailyHours = it },
                        label = { Text("Daily Hours") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = targetDate,
                        onValueChange = { targetDate = it },
                        label = { Text("Target Timeline") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                OutlinedTextField(
                    value = interests,
                    onValueChange = { interests = it },
                    label = { Text("Personal Interests (for analogies)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.secondary)
                    Text("Preferred Language", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    languages.forEach { lang ->
                        FilterChip(
                            selected = preferredLanguage == lang,
                            onClick = { preferredLanguage = lang },
                            label = { Text(lang, fontSize = 12.sp) }
                        )
                    }
                }
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
                .height(54.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Generate My Personalised Roadmap", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}