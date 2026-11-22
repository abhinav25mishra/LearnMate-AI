package com.learnmate.ai

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun VxBrandMark(modifier: Modifier = Modifier, sizeDp: Int = 44, blendSurface: Boolean = false) {
    if (blendSurface) {
        Box(
            modifier = modifier.size(sizeDp.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.vx_logo_raw),
                contentDescription = "VicharaX Logo",
                modifier = Modifier.fillMaxSize()
            )
        }
    } else {
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
}

@Composable
fun ModelExecutionFlowCard(activeSubject: String) {
    val steps = listOf(
        "1. Intake & Context" to "Maps your Standard, Courses & '$activeSubject' goal",
        "2. Topic Guardrail" to "Validates query relevance in <1 ms with 0 wasted tokens",
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
    var isSignUpMode by remember { mutableStateOf(false) }

    // Step 1: Clean Sign In / Sign Up State (Full Name, Username, PIN)
    var fullNameInput by remember { mutableStateOf("") }
    var usernameInput by remember { mutableStateOf(existingAccounts.firstOrNull()?.username ?: "") }
    var pinInput by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(true) }
    var showPin by remember { mutableStateOf(false) }
    var authError by remember { mutableStateOf<String?>(null) }
    var showForgotDialog by remember { mutableStateOf(false) }
    var matchedExistingAccount by remember { mutableStateOf<StudentProfile?>(null) }

    // Step 2: Student Profile Intake State
    var selectedStandard by remember { mutableStateOf("Undergraduate / College") }
    var coursesInput by remember { mutableStateOf("Computer Science & Engineering") }
    var customTopicSearch by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf("Python") }
    var interestNotes by remember { mutableStateOf("Core Fundamentals, Problem Solving & Projects") }
    var dailyHours by remember { mutableStateOf("1.5") }
    var targetDuration by remember { mutableStateOf("30 Days") }
    var preferredLanguage by remember { mutableStateOf("English") }
    var showMoreLanguages by remember { mutableStateOf(false) }

    val standards = listOf("School (Class 9–12)", "Undergraduate / College", "Postgraduate", "Self-Paced")

    val suggestedSubjects = remember(selectedStandard) {
        when (selectedStandard) {
            "School (Class 9–12)" -> listOf(
                "Physics",
                "Mathematics",
                "Chemistry",
                "Biology",
                "History",
                "Geography"
            )
            "Postgraduate" -> listOf(
                "Machine Learning & AI",
                "Cybersecurity",
                "Data Science",
                "SQL & Databases",
                "Distributed Systems",
                "Algorithms"
            )
            "Self-Paced" -> listOf(
                "Python",
                "Cybersecurity",
                "Data Science",
                "Networking",
                "SQL & Databases",
                "Web Development"
            )
            else -> listOf(
                "Python",
                "SQL & Databases",
                "Data Structures & Algorithms",
                "C Programming",
                "Networking",
                "Cybersecurity"
            )
        }
    }

    val hourOptions = listOf("1.0", "1.5", "2.0", "3.0")
    val durationOptions = listOf("15 Days", "30 Days", "60 Days", "90 Days")
    val extraLanguages = remember {
        StringsManager.supportedLanguages.filter { it != "English" && it != "Hindi" }
    }

    if (showForgotDialog) {
        val found = ProfileStorage.findAccountByIdentifier(context, usernameInput)
        AlertDialog(
            onDismissRequest = { showForgotDialog = false },
            title = { Text("PIN Recovery", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = if (found != null) {
                        "Local account found for ${found.name} (@${found.username}).\nYour saved PIN is: ${found.passcode}"
                    } else {
                        "Enter your registered username in 'Your Username' first, or tap 'Sign up' below to create a new local account."
                    },
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showForgotDialog = false }) {
                    Text("OK", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (currentStep == 1) {
        // =====================================================================
        // STEP 1: CLEAN SIGN IN / SIGN UP SCREEN (NO SOCIAL ICONS, NO EMAIL)
        // =====================================================================
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val step = 34.dp.toPx()
                val lineColor = Color(0xFFE2E8F0).copy(alpha = 0.65f)
                var x = 0f
                while (x <= size.width) {
                    drawLine(lineColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
                    x += step
                }
                var y = 0f
                while (y <= size.height) {
                    drawLine(lineColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
                    y += step
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // VX Brand Mark + Single-Line VicharaX Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    VxBrandMark(sizeDp = 42, blendSurface = true)
                    Text(
                        text = "VicharaX",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F172A),
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Clip
                    )
                }

                // Clean Elevated Auth Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (isSignUpMode) "Create account" else "Welcome back",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = if (isSignUpMode) {
                                    "Set up your offline 24×7 AI Tutor profile."
                                } else {
                                    "Please enter your details to sign in."
                                },
                                fontSize = 13.sp,
                                color = Color(0xFF64748B),
                                textAlign = TextAlign.Center
                            )
                        }

                        // Sign Up: "Your Full Name" at the very top of form fields
                        if (isSignUpMode) {
                            OutlinedTextField(
                                value = fullNameInput,
                                onValueChange = {
                                    fullNameInput = it
                                    authError = null
                                },
                                label = { Text("Your Full Name") },
                                placeholder = { Text("e.g., Abhinav Mishra") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF64748B)) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color(0xFF0F172A),
                                    unfocusedTextColor = Color(0xFF0F172A),
                                    focusedBorderColor = Color(0xFF0F172A),
                                    unfocusedBorderColor = Color(0xFFCBD5E1)
                                )
                            )
                        }

                        // "Your Username" (First on Sign In, directly below Full Name on Sign Up)
                        OutlinedTextField(
                            value = usernameInput,
                            onValueChange = {
                                usernameInput = it
                                authError = null
                            },
                            label = { Text("Your Username") },
                            placeholder = { Text("e.g., abhinav_m") },
                            leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = Color(0xFF64748B)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color(0xFF0F172A),
                                unfocusedTextColor = Color(0xFF0F172A),
                                focusedBorderColor = Color(0xFF0F172A),
                                unfocusedBorderColor = Color(0xFFCBD5E1)
                            )
                        )

                        // "PIN" directly below Username
                        OutlinedTextField(
                            value = pinInput,
                            onValueChange = {
                                pinInput = it
                                authError = null
                            },
                            label = { Text("PIN") },
                            placeholder = { Text("4+ digit PIN") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF64748B)) },
                            trailingIcon = {
                                IconButton(onClick = { showPin = !showPin }) {
                                    Icon(
                                        imageVector = if (showPin) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle PIN",
                                        tint = Color(0xFF64748B)
                                    )
                                }
                            },
                            visualTransformation = if (showPin) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color(0xFF0F172A),
                                unfocusedTextColor = Color(0xFF0F172A),
                                focusedBorderColor = Color(0xFF0F172A),
                                unfocusedBorderColor = Color(0xFFCBD5E1)
                            )
                        )

                        // Remember me + Forgot PIN? row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = rememberMe,
                                    onCheckedChange = { rememberMe = it },
                                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0F172A))
                                )
                                Text(
                                    text = "Remember me",
                                    fontSize = 12.sp,
                                    color = Color(0xFF475569)
                                )
                            }

                            if (!isSignUpMode) {
                                TextButton(onClick = { showForgotDialog = true }) {
                                    Text(
                                        text = "Forgot PIN?",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF1D4ED8)
                                    )
                                }
                            }
                        }

                        if (authError != null) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFFEE2E2),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = authError!!,
                                    color = Color(0xFF991B1B),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        // Bold Dark Primary CTA Button
                        Button(
                            onClick = {
                                if (!isSignUpMode) {
                                    val verified = ProfileStorage.verifyCredentials(context, usernameInput, pinInput)
                                    if (verified != null) {
                                        matchedExistingAccount = verified
                                        selectedStandard = verified.standard
                                        coursesInput = verified.courses
                                        selectedSubject = verified.subject
                                        interestNotes = verified.interests
                                        dailyHours = verified.dailyHours
                                        targetDuration = verified.targetDate
                                        preferredLanguage = verified.preferredLanguage
                                        showMoreLanguages = preferredLanguage != "English" && preferredLanguage != "Hindi"
                                        currentStep = 2
                                    } else {
                                        authError = if (existingAccounts.isEmpty()) {
                                            "No local account found yet. Tap 'Sign up' below to create your account."
                                        } else {
                                            "Invalid username or PIN. Please check your credentials."
                                        }
                                    }
                                } else {
                                    matchedExistingAccount = null
                                    currentStep = 2
                                }
                            },
                            enabled = usernameInput.isNotBlank() && pinInput.trim().length >= 4 &&
                                (!isSignUpMode || fullNameInput.isNotBlank()),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF111827),
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = if (isSignUpMode) "Next" else "Sign in",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                maxLines = 1,
                                softWrap = false
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                        }

                        // Bottom Toggle: Don't have an account? Sign up
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (isSignUpMode) "Already have an account?" else "Don't have an account?",
                                fontSize = 13.sp,
                                color = Color(0xFF64748B)
                            )
                            TextButton(
                                onClick = {
                                    isSignUpMode = !isSignUpMode
                                    authError = null
                                }
                            ) {
                                Text(
                                    text = if (isSignUpMode) "Sign in" else "Sign up",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                            }
                        }
                    }
                }
            }
        }
    } else {
        // =====================================================================
        // STEP 2: STUDENT PROFILE INTAKE
        // =====================================================================
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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
                            text = "Step 2 of 2 • Student Profile Intake",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE0F2FE)
                        )
                        LinearProgressIndicator(
                            progress = { 1.0f },
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
                        text = "Student Profile Intake",
                        fontSize = 21.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Configure your standard, courses, and primary interest so VicharaX tailors your roadmap, MCQs, and AI Tutor.",
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

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
                                        onClick = {
                                            selectedStandard = std
                                            val defaultForLevel = when (std) {
                                                "School (Class 9–12)" -> "Physics"
                                                "Postgraduate" -> "Machine Learning & AI"
                                                else -> "Python"
                                            }
                                            if (customTopicSearch.isBlank()) {
                                                selectedSubject = defaultForLevel
                                            }
                                        },
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

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Learning Your Primary Interest",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Selected Interest:",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = selectedSubject,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = customTopicSearch,
                                onValueChange = {
                                    customTopicSearch = it
                                    if (it.trim().isNotEmpty()) {
                                        selectedSubject = it.trim()
                                    }
                                },
                                label = { Text("Search or Enter Custom Topic") },
                                placeholder = { Text("e.g., Cybersecurity, Data Science, Networking...") },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                trailingIcon = {
                                    if (customTopicSearch.isNotEmpty()) {
                                        IconButton(onClick = { customTopicSearch = "" }) {
                                            Icon(Icons.Default.Close, contentDescription = "Clear search", modifier = Modifier.size(16.dp))
                                        }
                                    }
                                },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Text(
                                text = "Suggested for $selectedStandard:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            suggestedSubjects.chunked(2).forEach { rowSubjects ->
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

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Preferred Tutor Language", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Not comfortable in English or Hindi? Tap + to switch to another comfortable language.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf("English", "Hindi").forEach { lang ->
                                FilterChip(
                                    selected = preferredLanguage == lang,
                                    onClick = { preferredLanguage = lang },
                                    label = {
                                        Text(
                                            text = lang,
                                            fontSize = 12.sp,
                                            fontWeight = if (preferredLanguage == lang) FontWeight.Bold else FontWeight.Medium,
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            FilledTonalIconButton(
                                onClick = { showMoreLanguages = !showMoreLanguages },
                                modifier = Modifier.size(40.dp),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = if (showMoreLanguages) Icons.Default.Close else Icons.Default.Add,
                                    contentDescription = "More Languages"
                                )
                            }
                        }

                        if (showMoreLanguages) {
                            extraLanguages.chunked(2).forEach { rowLangs ->
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
                    }

                    ModelExecutionFlowCard(activeSubject = selectedSubject)

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
                                val resolvedUsername = matchedExistingAccount?.username ?: usernameInput.trim()
                                val resolvedName = matchedExistingAccount?.name
                                    ?: fullNameInput.trim().ifEmpty {
                                        resolvedUsername.replaceFirstChar { it.uppercase() }
                                    }
                                val resolvedPin = matchedExistingAccount?.passcode ?: pinInput.trim()
                                val activeTopic = selectedSubject.ifBlank { "Python" }
                                val savedProfile = StudentProfile(
                                    name = resolvedName,
                                    username = resolvedUsername,
                                    standard = selectedStandard,
                                    courses = coursesInput.trim().ifEmpty { "General Studies" },
                                    goal = "Master $activeTopic ($targetDuration Study Plan)",
                                    subject = activeTopic,
                                    skillLevel = selectedStandard,
                                    interests = interestNotes.trim().ifEmpty { activeTopic },
                                    dailyHours = dailyHours,
                                    targetDate = targetDuration,
                                    preferredLanguage = preferredLanguage,
                                    passcode = resolvedPin,
                                    createdAt = dateStr
                                )
                                ProfileStorage.saveProfile(context, savedProfile, persistSession = rememberMe)
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
