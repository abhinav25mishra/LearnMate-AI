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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(onComplete: (StudentProfile) -> Unit) {
    var name by remember { mutableStateOf("") }
    var goal by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("Python Programming") }
    var skillLevel by remember { mutableStateOf("Beginner") }
    var interests by remember { mutableStateOf("") }
    var dailyHours by remember { mutableStateOf("2") }
    var targetDate by remember { mutableStateOf("30 Days") }
    var preferredLanguage by remember { mutableStateOf("English") }

    val skillLevels = listOf("Beginner", "Intermediate", "Advanced")
    val languages = listOf("English", "Hindi", "Tamil", "Telugu", "Bengali", "Marathi")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Welcome to LearnMate AI",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Tell us about yourself so we can build your personalised offline study plan.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Your Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = goal,
            onValueChange = { goal = it },
            label = { Text("What is your learning goal? (e.g., Crack coding interviews)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = subject,
            onValueChange = { subject = it },
            label = { Text("Subject or Technology (e.g., Python, Data Structures)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Text("Current Knowledge Level", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            skillLevels.forEach { level ->
                FilterChip(
                    selected = skillLevel == level,
                    onClick = { skillLevel = level },
                    label = { Text(level) }
                )
            }
        }

        OutlinedTextField(
            value = interests,
            onValueChange = { interests = it },
            label = { Text("Your Interests (e.g., AI, Web Apps, Games)") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = dailyHours,
                onValueChange = { dailyHours = it },
                label = { Text("Daily Hours") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            OutlinedTextField(
                value = targetDate,
                onValueChange = { targetDate = it },
                label = { Text("Target (e.g., 30 Days)") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }

        Text("Preferred Language", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            languages.chunked(3).forEach { rowLangs ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowLangs.forEach { lang ->
                        FilterChip(
                            selected = preferredLanguage == lang,
                            onClick = { preferredLanguage = lang },
                            label = { Text(lang) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                onComplete(
                    StudentProfile(
                        name = name.ifBlank { "Student" },
                        goal = goal.ifBlank { "Master $subject" },
                        subject = subject.ifBlank { "Python Programming" },
                        skillLevel = skillLevel,
                        interests = interests.ifBlank { "Problem Solving" },
                        dailyHours = dailyHours.ifBlank { "2" },
                        targetDate = targetDate.ifBlank { "30 Days" },
                        preferredLanguage = preferredLanguage
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Create My Learning Profile", fontSize = 16.sp)
        }
    }
}