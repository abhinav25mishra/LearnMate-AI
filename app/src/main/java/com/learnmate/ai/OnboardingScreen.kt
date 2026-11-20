package com.learnmate.ai

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RocketLaunch
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

@Composable
fun OnboardingScreen(onComplete: (StudentProfile) -> Unit) {
    val context = LocalContext.current
    val existingAccounts = remember { ProfileStorage.loadAllAccounts(context) }

    // Step 1: Sign In / Sign Up Only -> "Next"
    // Step 2: Interest & Learning Setup -> "Get Started" -> Persists & opens Home
    var currentStep by remember { mutableStateOf(1) }
    var isSignUpMode by remember { mutableStateOf(existingAccounts.isEmpty()) }

    // Step 1 Credentials State
    var signInName by remember { mutableStateOf(existingAccounts.firstOrNull()?.name ?: "") }
    var signInPasscode by remember { mutableStateOf("") }
    var signInError by remember { mutableStateOf<String?>(null) }
    var showPassword by remember { mutableStateOf(false) }

    var signUpName by remember { mutableStateOf("") }
    var signUpPasscode by remember { mutableStateOf("") }
    var matchedExistingAccount by remember { mutableStateOf<StudentProfile?>(null) }

    // Step 2 Interest & Learning Setup State
    var selectedSubject by remember { mutableStateOf("Python") }
    var interestNotes by remember { mutableStateOf("Core Concepts, Functions & Problem Solving") }
    var skillLevel by remember { mutableStateOf("Beginner") }
    var dailyHours by remember { mutableStateOf("1.5") }
    var targetDuration by remember { mutableStateOf("30 Days") }
    var preferredLanguage by remember { mutableStateOf("English") }

    val subjects = listOf(
        "Python",
        "C Programming",
        "Data Structures & Algorithms",
        "SQL & Databases",
        "Machine Learning & AI",
        "Mathematics"
    )
    val levels = listOf("Beginner", "Intermediate", "Advanced")
    val hourOptions = listOf("1.0", "1.5", "2.0", "3.0")
    val durationOptions = listOf("15 Days", "30 Days", "60 Days", "90 Days")
    val languages = listOf("English", "Hindi", "Bengali")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        // Branded VicharaX Header
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
                    VxBrandMark(sizeDp = 50)
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

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = if (currentStep == 1) "Step 1 of 2 • Account Authentication" else "Step 2 of 2 • Interest & Study Setup",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE0F2FE)
                    )
                    LinearProgressIndicator(
                        progress = { if (currentStep == 1) 0.5f else 1.0f },
                        color = Color(0xFF5EEAD4),
                        trackColor = Color.White.copy(alpha = 0.25f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }
            }
        }

        if (currentStep == 1) {
            // =================================================================
            // STEP 1: SIGN IN / SIGN UP ONLY + "Next" BUTTON
            // =================================================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                    label = { Text("Sign Up", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.weight(1f)
                )
            }

            if (!isSignUpMode) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Sign In",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Enter your learner name and passcode, then tap Next to review or confirm your learning interests.",
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (existingAccounts.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
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
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFFEE2E2),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = signInError!!,
                                    color = Color(0xFF991B1B),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }

                        Button(
                            onClick = {
                                val verified = ProfileStorage.verifyCredentials(context, signInName, signInPasscode)
                                if (verified != null) {
                                    matchedExistingAccount = verified
                                    selectedSubject = verified.subject
                                    interestNotes = verified.interests
                                    skillLevel = verified.skillLevel
                                    dailyHours = verified.dailyHours
                                    targetDuration = verified.targetDate
                                    preferredLanguage = verified.preferredLanguage
                                    currentStep = 2
                                } else {
                                    signInError = if (existingAccounts.isEmpty()) {
                                        "No local account found yet. Tap 'Sign Up' above to create your account."
                                    } else {
                                        "Invalid learner name or passcode. Please check your credentials."
                                    }
                                }
                            },
                            enabled = signInName.isNotBlank() && signInPasscode.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Next", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            } else {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Sign Up",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Create your VicharaX account with your name and security passcode, then tap Next to select your Interest.",
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = signUpName,
                            onValueChange = { signUpName = it },
                            label = { Text("Full Name") },
                            placeholder = { Text("e.g., Abhinav Mishra") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        )

                        OutlinedTextField(
                            value = signUpPasscode,
                            onValueChange = { signUpPasscode = it },
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

                        Button(
                            onClick = {
                                matchedExistingAccount = null
                                currentStep = 2
                            },
                            enabled = signUpName.trim().isNotEmpty() && signUpPasscode.trim().length >= 4,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Next", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        } else {
            // =================================================================
            // STEP 2: DEDICATED "Interest" SCREEN WITH "Get Started" BUTTON
            // =================================================================
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Text(
                        text = "Interest",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Select your core subject interest, learning scope, and daily study duration. VicharaX personalizes your Roadmap, 10-MCQ Assessment, and AI Mentor around this selection.",
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Primary Subject Interest", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        subjects.chunked(2).forEach { rowSubjects ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                rowSubjects.forEach { subj ->
                                    FilterChip(
                                        selected = selectedSubject == subj,
                                        onClick = { selectedSubject = subj },
                                        label = {
                                            Text(
                                                text = subj,
                                                fontSize = 13.sp,
                                                fontWeight = if (selectedSubject == subj) FontWeight.Bold else FontWeight.Medium,
                                                maxLines = 1
                                            )
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = interestNotes,
                        onValueChange = { interestNotes = it },
                        label = { Text("Focus Areas & Topics") },
                        placeholder = { Text("e.g., Functions, Arrays, Logic Building") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Learning Scope / Level", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            levels.forEach { lvl ->
                                FilterChip(
                                    selected = skillLevel == lvl,
                                    onClick = { skillLevel = lvl },
                                    label = { Text(lvl, fontSize = 13.sp) }
                                )
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Daily Study Hours", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            hourOptions.forEach { hrs ->
                                FilterChip(
                                    selected = dailyHours == hrs,
                                    onClick = { dailyHours = hrs },
                                    label = { Text("$hrs hrs/day", fontSize = 12.sp) }
                                )
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Target Study Duration", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            durationOptions.forEach { dur ->
                                FilterChip(
                                    selected = targetDuration == dur,
                                    onClick = { targetDuration = dur },
                                    label = { Text(dur, fontSize = 12.sp) }
                                )
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Preferred Mentor Language", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            languages.forEach { lang ->
                                FilterChip(
                                    selected = preferredLanguage == lang,
                                    onClick = { preferredLanguage = lang },
                                    label = { Text(lang, fontSize = 12.sp) }
                                )
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = { currentStep = 1 },
                            modifier = Modifier.weight(1f).height(54.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Back", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val dateStr = matchedExistingAccount?.createdAt
                                    ?: SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
                                val finalName = matchedExistingAccount?.name ?: signUpName.trim()
                                val finalPasscode = matchedExistingAccount?.passcode ?: signUpPasscode.trim()
                                val savedProfile = StudentProfile(
                                    name = finalName,
                                    goal = "Master $selectedSubject ($skillLevel • $targetDuration)",
                                    subject = selectedSubject,
                                    skillLevel = skillLevel,
                                    interests = interestNotes.trim().ifEmpty { selectedSubject },
                                    dailyHours = dailyHours,
                                    targetDate = targetDuration,
                                    preferredLanguage = preferredLanguage,
                                    passcode = finalPasscode,
                                    createdAt = dateStr
                                )
                                ProfileStorage.saveProfile(context, savedProfile)
                                onComplete(savedProfile)
                            },
                            modifier = Modifier.weight(1.6f).height(54.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Get Started", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    }
}
