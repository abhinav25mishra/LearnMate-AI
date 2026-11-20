package com.learnmate.ai

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun VxBrandMark(modifier: Modifier = Modifier, sizeDp: Int = 44) {
    Surface(
        modifier = modifier.size(sizeDp.dp),
        shape = RoundedCornerShape((sizeDp / 3).dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
        shadowElevation = 2.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding((sizeDp * 0.14f).dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.vx_logo_raw),
                contentDescription = "VicharaX Logo",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(onComplete: (StudentProfile) -> Unit) {
    val context = LocalContext.current
    val existingAccounts = remember { ProfileStorage.loadAllAccounts(context) }
    var isSignUpMode by remember { mutableStateOf(existingAccounts.isEmpty()) }

    // Sign-In State
    var signInName by remember { mutableStateOf(existingAccounts.firstOrNull()?.name ?: "") }
    var signInPasscode by remember { mutableStateOf("") }
    var signInError by remember { mutableStateOf<String?>(null) }
    var showPassword by remember { mutableStateOf(false) }

    // Sign-Up State
    var name by remember { mutableStateOf("") }
    var passcode by remember { mutableStateOf("") }
    var goal by remember { mutableStateOf("Master Python & Core Engineering") }
    var selectedSubject by remember { mutableStateOf("Python") }
    var skillLevel by remember { mutableStateOf("Beginner") }
    var interests by remember { mutableStateOf("Coding, AI & Problem Solving") }
    var dailyHours by remember { mutableStateOf("1.5") }
    var targetDate by remember { mutableStateOf("30 Days") }
    var preferredLanguage by remember { mutableStateOf("English") }

    val subjects = listOf("Python", "Data Structures & Algorithms", "SQL & Databases", "Machine Learning & AI", "Mathematics")
    val levels = listOf("Beginner", "Intermediate", "Advanced")
    val languages = listOf("English", "Hindi", "Bengali", "Tamil", "Telugu", "Marathi")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Branded VicharaX Hero Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF0F172A), Color(0xFF1E3A8A), Color(0xFF0D9488))
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(22.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    VxBrandMark(sizeDp = 52)
                    Column {
                        Text(
                            text = "VicharaX",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "Offline Arm Edge AI Learning Mentor",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF99F6E4)
                        )
                    }
                }
                Text(
                    text = "100% On-Device Intelligence • Zero Cloud Latency • Persistent Skill Mastery",
                    fontSize = 12.sp,
                    color = Color(0xFFE2E8F0)
                )
            }
        }

        // Sign In / Sign Up Mode Switcher
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FilterChip(
                selected = !isSignUpMode,
                onClick = {
                    isSignUpMode = false
                    signInError = null
                },
                label = { Text("Sign In", fontWeight = FontWeight.Bold) },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = isSignUpMode,
                onClick = {
                    isSignUpMode = true
                    signInError = null
                },
                label = { Text("Create Account (Sign Up)", fontWeight = FontWeight.Bold) },
                modifier = Modifier.weight(1f)
            )
        }

        if (!isSignUpMode) {
            // SIGN IN CARD
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Welcome Back to VicharaX",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Sign in with your learner name and passcode. Your session stays active across app restarts.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (existingAccounts.isNotEmpty()) {
                        Text("Saved Local Accounts:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            existingAccounts.forEach { acc ->
                                SuggestionChip(
                                    onClick = {
                                        signInName = acc.name
                                        signInError = null
                                    },
                                    label = { Text(acc.name, fontWeight = FontWeight.SemiBold) }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = signInName,
                        onValueChange = {
                            signInName = it
                            signInError = null
                        },
                        label = { Text("Learner Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    OutlinedTextField(
                        value = signInPasscode,
                        onValueChange = {
                            signInPasscode = it
                            signInError = null
                        },
                        label = { Text("Passcode / PIN") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle Passcode"
                                )
                            }
                        },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    if (signInError != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFEE2E2),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = signInError!!,
                                color = Color(0xFF991B1B),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    Button(
                        onClick = {
                            val matched = ProfileStorage.signIn(context, signInName, signInPasscode)
                            if (matched != null) {
                                onComplete(matched)
                            } else {
                                signInError = if (existingAccounts.isEmpty()) {
                                    "No local account found yet. Switch to 'Create Account (Sign Up)' first."
                                } else {
                                    "Invalid name or passcode. Check your saved credentials or create a new account."
                                }
                            }
                        },
                        enabled = signInName.isNotBlank() && signInPasscode.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Sign In & Continue", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        } else {
            // SIGN UP CARD
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Create Your VicharaX Profile",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        placeholder = { Text("e.g., Abhinav Mishra") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    OutlinedTextField(
                        value = passcode,
                        onValueChange = { passcode = it },
                        label = { Text("Create Passcode / PIN (4+ digits)") },
                        placeholder = { Text("e.g., 2580") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle Passcode"
                                )
                            }
                        },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Text("Primary Focus Topic (AI Mentor Guardrail)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        subjects.chunked(2).forEach { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowItems.forEach { subj ->
                                    FilterChip(
                                        selected = selectedSubject == subj,
                                        onClick = { selectedSubject = subj },
                                        label = { Text(subj, fontSize = 12.sp, maxLines = 1) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = interests,
                        onValueChange = { interests = it },
                        label = { Text("Learning Interests & Topics") },
                        placeholder = { Text("e.g., Python, Data Science, Edge AI") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    OutlinedTextField(
                        value = goal,
                        onValueChange = { goal = it },
                        label = { Text("Target Learning Goal") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Text("Current Skill Level", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        levels.forEach { lvl ->
                            FilterChip(
                                selected = skillLevel == lvl,
                                onClick = { skillLevel = lvl },
                                label = { Text(lvl, fontSize = 12.sp) }
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = dailyHours,
                            onValueChange = { dailyHours = it },
                            label = { Text("Daily Hours") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp)
                        )
                        OutlinedTextField(
                            value = targetDate,
                            onValueChange = { targetDate = it },
                            label = { Text("Timeline") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp)
                        )
                    }

                    Text("Preferred Mentor Language", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        languages.take(3).forEach { lang ->
                            FilterChip(
                                selected = preferredLanguage == lang,
                                onClick = { preferredLanguage = lang },
                                label = { Text(lang, fontSize = 12.sp) }
                            )
                        }
                    }

                    Button(
                        onClick = {
                            val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
                            val newProfile = StudentProfile(
                                name = name.trim(),
                                goal = goal.trim().ifEmpty { "Master $selectedSubject" },
                                subject = selectedSubject,
                                skillLevel = skillLevel,
                                interests = interests.trim().ifEmpty { selectedSubject },
                                dailyHours = dailyHours.trim().ifEmpty { "1.5" },
                                targetDate = targetDate.trim().ifEmpty { "30 Days" },
                                preferredLanguage = preferredLanguage,
                                passcode = passcode.trim(),
                                createdAt = dateStr
                            )
                            ProfileStorage.saveProfile(context, newProfile)
                            onComplete(newProfile)
                        },
                        enabled = name.isNotBlank() && passcode.trim().length >= 4,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Create Account & Launch VicharaX", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}
