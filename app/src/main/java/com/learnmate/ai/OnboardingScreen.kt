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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.text.style.TextOverflow
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
fun ModelExecutionFlowCard(activeSubject: String) {
    val steps = listOf(
        "1. Intake & Context" to "Maps your Standard, Courses & '$activeSubject' goal",
        "2. Topic Guardrail" to "Validates query relevance in <1 ms on-device",
        "3. Llama 3.2 1B INT4" to "Runs 24x7 offline via PyTorch ExecuTorch on Arm64"
    )
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Default.Memory, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                Text(
                    text = "On-Device AI Tutor Pipeline (Step-by-Step)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            steps.forEach { (title, desc) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Column {
                        Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text(desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingScreen(onComplete: (StudentProfile) -> Unit) {
    val context = LocalContext.current
    val existingAccounts = remember { ProfileStorage.loadAllAccounts(context) }

    var currentStep by remember { mutableStateOf(1) }
    var isSignUpMode by remember { mutableStateOf(existingAccounts.isEmpty()) }

    // Step 1: Sign In / Sign Up Credentials
    var signInName by remember { mutableStateOf(existingAccounts.firstOrNull()?.name ?: "") }
    var signInPasscode by remember { mutableStateOf("") }
    var signInError by remember { mutableStateOf<String?>(null) }
    var showPassword by remember { mutableStateOf(false) }

    var signUpName by remember { mutableStateOf("") }
    var signUpPasscode by remember { mutableStateOf("") }
    var matchedExistingAccount by remember { mutableStateOf<StudentProfile?>(null) }

    // Step 2: Student Profile Intake
    var selectedStandard by remember { mutableStateOf("Undergraduate / College") }
    var coursesInput by remember { mutableStateOf("Computer Science & Engineering") }
    var customTopicSearch by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf("Python") }
    var interestNotes by remember { mutableStateOf("Core Fundamentals, Problem Solving & Projects") }
    var dailyHours by remember { mutableStateOf("1.5") }
    var targetDuration by remember { mutableStateOf("30 Days") }
    var preferredLanguage by remember { mutableStateOf("English") }

    val standards = listOf("School (Class 9–12)", "Undergraduate / College", "Postgraduate", "Self-Paced")
    val presetSubjects = listOf(
        "Python",
        "Physics",
        "Mathematics",
        "SQL & Databases",
        "C Programming",
        "Data Structures & Algorithms",
        "History",
        "Geography"
    )
    val hourOptions = listOf("1.0", "1.5", "2.0", "3.0")
    val durationOptions = listOf("15 Days", "30 Days", "60 Days", "90 Days")
    val languages = StringsManager.supportedLanguages

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Branded VicharaX Header (Strict single-line title)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF0F172A), Color(0xFF1E3A8A), Color(0xFF0D9488))
                    ),
                    shape = RoundedCornerShape(22.dp)
                )
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    VxBrandMark(sizeDp = 46)
                    Column {
                        Text(
                            text = "VicharaX",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Clip
                        )
                        Text(
                            text = "Official 24×7 Offline AI Tutor • Arm Edge",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF99F6E4),
                            maxLines = 1
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(
                        text = if (currentStep == 1) "Step 1 of 2 • Account Authentication" else "Step 2 of 2 • Student Profile Intake",
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
                            .height(5.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }
            }
        }

        if (currentStep == 1) {
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
                    label = { Text("Sign In", fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false) },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = isSignUpMode,
                    onClick = {
                        isSignUpMode = true
                        signInError = null
                    },
                    label = { Text("Sign Up", fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false) },
                    modifier = Modifier.weight(1f)
                )
            }

            if (!isSignUpMode) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Sign In",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Sign in to your local VicharaX account, then tap Next to review your Student Profile Intake.",
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
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
                                            label = { Text(acc.name, fontWeight = FontWeight.SemiBold, maxLines = 1, softWrap = false) }
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
                                    selectedStandard = verified.standard
                                    coursesInput = verified.courses
                                    selectedSubject = verified.subject
                                    interestNotes = verified.interests
                                    dailyHours = verified.dailyHours
                                    targetDuration = verified.targetDate
                                    preferredLanguage = verified.preferredLanguage
                                    currentStep = 2
                                } else {
                                    signInError = if (existingAccounts.isEmpty()) {
                                        "No account saved yet. Tap 'Sign Up' above to create your account."
                                    } else {
                                        "Invalid learner name or passcode. Please check your credentials."
                                    }
                                }
                            },
                            enabled = signInName.isNotBlank() && signInPasscode.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Next", fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, softWrap = false)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            } else {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Sign Up",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Create your VicharaX student account, then tap Next to configure your Student Profile Intake.",
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
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
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Next", fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, softWrap = false)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        } else {
            // =================================================================
            // STEP 2: STUDENT PROFILE INTAKE (Compact spacing, zero dead gaps, single-line chips)
            // =================================================================
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Student Profile Intake",
                        fontSize = 21.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Configure your standard, courses, and what you want to learn so VicharaX tailors your roadmap, MCQs, and AI Tutor.",
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // 1. Academic Standard (2x2 clean grid)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Academic Standard / Level", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        standards.chunked(2).forEach { rowStd ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowStd.forEach { std ->
                                    FilterChip(
                                        selected = selectedStandard == std,
                                        onClick = { selectedStandard = std },
                                        label = {
                                            Text(
                                                text = std,
                                                fontSize = 11.sp,
                                                maxLines = 1,
                                                softWrap = false,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    // 2. Enrolled Courses
                    OutlinedTextField(
                        value = coursesInput,
                        onValueChange = { coursesInput = it },
                        label = { Text("Courses / Stream") },
                        placeholder = { Text("e.g., B.Tech CSE, Science, Humanities") },
                        leadingIcon = { Icon(Icons.Default.School, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    // 3. Learn What / Primary Subject Interest + Search Bar
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Learn What / Primary Subject Interest", fontSize = 13.sp, fontWeight = FontWeight.Bold)

                        OutlinedTextField(
                            value = customTopicSearch,
                            onValueChange = {
                                customTopicSearch = it
                                if (it.trim().isNotEmpty()) {
                                    selectedSubject = it.trim()
                                }
                            },
                            label = { Text("Search or Enter Any Topic") },
                            placeholder = { Text("e.g., Physics, Mathematics, History, Python, SQL...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        )

                        presetSubjects.chunked(2).forEach { rowSubjects ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowSubjects.forEach { subj ->
                                    FilterChip(
                                        selected = selectedSubject.equals(subj, ignoreCase = true),
                                        onClick = {
                                            selectedSubject = subj
                                            customTopicSearch = ""
                                        },
                                        label = {
                                            Text(
                                                text = subj,
                                                fontSize = 12.sp,
                                                fontWeight = if (selectedSubject.equals(subj, ignoreCase = true)) FontWeight.Bold else FontWeight.Medium,
                                                maxLines = 1,
                                                softWrap = false,
                                                overflow = TextOverflow.Ellipsis
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
                        label = { Text("Specific Goals & Focus Areas") },
                        placeholder = { Text("e.g., Exam prep, problem solving, concepts") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    // 4. Daily Study Hours (2x2 clean grid, zero trailing gap)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Daily Study Hours", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        hourOptions.chunked(2).forEach { rowHrs ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowHrs.forEach { hrs ->
                                    FilterChip(
                                        selected = dailyHours == hrs,
                                        onClick = { dailyHours = hrs },
                                        label = {
                                            Text(
                                                text = "$hrs hrs/day",
                                                fontSize = 12.sp,
                                                maxLines = 1,
                                                softWrap = false
                                            )
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    // 5. Study Plan Duration (2x2 grid so "90 Days" never wraps onto two lines)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Study Plan Duration", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        durationOptions.chunked(2).forEach { rowDur ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowDur.forEach { dur ->
                                    FilterChip(
                                        selected = targetDuration == dur,
                                        onClick = { targetDuration = dur },
                                        label = {
                                            Text(
                                                text = dur,
                                                fontSize = 12.sp,
                                                maxLines = 1,
                                                softWrap = false
                                            )
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    // 6. Multilingual Support (8 Languages in clean 2-column grid)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Preferred Tutor Language", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        languages.chunked(2).forEach { rowLangs ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowLangs.forEach { lang ->
                                    FilterChip(
                                        selected = preferredLanguage == lang,
                                        onClick = { preferredLanguage = lang },
                                        label = {
                                            Text(
                                                text = lang,
                                                fontSize = 12.sp,
                                                maxLines = 1,
                                                softWrap = false
                                            )
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    // 7. Step-by-Step Running Model Flow Indicator
                    ModelExecutionFlowCard(activeSubject = selectedSubject)

                    // Navigation Buttons: Back & Get Started
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = { currentStep = 1 },
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Back", fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                        }

                        Button(
                            onClick = {
                                val dateStr = matchedExistingAccount?.createdAt
                                    ?: SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
                                val finalName = matchedExistingAccount?.name ?: signUpName.trim()
                                val finalPasscode = matchedExistingAccount?.passcode ?: signUpPasscode.trim()
                                val activeTopic = selectedSubject.ifBlank { "Python" }
                                val savedProfile = StudentProfile(
                                    name = finalName,
                                    standard = selectedStandard,
                                    courses = coursesInput.trim().ifEmpty { "General Studies" },
                                    goal = "Master $activeTopic ($targetDuration Study Plan)",
                                    subject = activeTopic,
                                    skillLevel = selectedStandard,
                                    interests = interestNotes.trim().ifEmpty { activeTopic },
                                    dailyHours = dailyHours,
                                    targetDate = targetDuration,
                                    preferredLanguage = preferredLanguage,
                                    passcode = finalPasscode,
                                    createdAt = dateStr
                                )
                                ProfileStorage.saveProfile(context, savedProfile)
                                onComplete(savedProfile)
                            },
                            enabled = selectedSubject.isNotBlank(),
                            modifier = Modifier.weight(1.6f).height(52.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Get Started", fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1, softWrap = false)
                        }
                    }
                }
            }
        }
    }
}
