package com.learnmate.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.OfflineBolt
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val LightLearnMateScheme = lightColorScheme(
    primary = Color(0xFF1D4ED8),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E3A8A),
    secondary = Color(0xFF0D9488),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCCFBF1),
    onSecondaryContainer = Color(0xFF115E59),
    background = Color(0xFFF8FAFC),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    outlineVariant = Color(0xFFE2E8F0)
)

private val DarkLearnMateScheme = darkColorScheme(
    primary = Color(0xFF60A5FA),
    onPrimary = Color(0xFF0F172A),
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = Color(0xFF2DD4BF),
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFF115E59),
    onSecondaryContainer = Color(0xFFCCFBF1),
    background = Color(0xFF0B1120),
    surface = Color(0xFF111827),
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFF94A3B8),
    outlineVariant = Color(0xFF334155)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            LaunchedEffect(Unit) { LocalLlamaEngine.warmUp() }
            var isDarkMode by remember { mutableStateOf(false) }
            MaterialTheme(colorScheme = if (isDarkMode) DarkLearnMateScheme else LightLearnMateScheme) {
                LearnMateApp(isDarkMode = isDarkMode, onToggleDarkMode = { isDarkMode = !isDarkMode })
            }
        }
    }
}

private data class NavTab(val label: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnMateApp(isDarkMode: Boolean, onToggleDarkMode: () -> Unit) {
    val context = LocalContext.current
    var profile by remember { mutableStateOf(ProfileStorage.loadProfile(context)) }
    var selectedTab by remember { mutableStateOf(0) }
    var showPerfScreen by remember { mutableStateOf(false) }

    if (profile == null) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            OnboardingScreen(
                onComplete = { newProfile ->
                    ProfileStorage.saveProfile(context, newProfile)
                    profile = newProfile
                }
            )
        }
    } else {
        val currentProfile = profile!!
        val strings = remember(currentProfile.preferredLanguage) {
            StringsManager.getStrings(currentProfile.preferredLanguage)
        }
        val tabs = listOf(
            NavTab(strings.tabHome, Icons.Default.Home),
            NavTab("Assessment", Icons.Default.Quiz),
            NavTab(strings.tabRoadmap, Icons.Default.Map),
            NavTab(strings.tabMentor, Icons.Default.AutoAwesome)
        )

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                Surface(shadowElevation = 2.dp, color = MaterialTheme.colorScheme.surface) {
                    TopAppBar(
                        navigationIcon = {
                            if (showPerfScreen) {
                                IconButton(onClick = { showPerfScreen = false }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        },
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            Brush.linearGradient(
                                                listOf(Color(0xFF1D4ED8), Color(0xFF0D9488))
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "LearnMate AI",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 17.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (showPerfScreen) "Arm & ExecuTorch Metrics" else "Arm Edge Mentor",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                        actions = {
                            IconButton(onClick = onToggleDarkMode) {
                                Icon(
                                    imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = "Toggle Theme",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { showPerfScreen = !showPerfScreen }) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = "Arm & ExecuTorch Performance",
                                    tint = if (showPerfScreen) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                                )
                            }
                            Surface(
                             onClick = { showPerfScreen = true },
                             shape = RoundedCornerShape(20.dp),
                             color = MaterialTheme.colorScheme.secondaryContainer,
                             border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)),
                             modifier = Modifier.padding(end = 10.dp)
                         ) {
                             Row(
                                 modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                 verticalAlignment = Alignment.CenterVertically,
                                 horizontalArrangement = Arrangement.spacedBy(3.dp)
                             ) {
                                 Icon(
                                     imageVector = Icons.Default.OfflineBolt,
                                     contentDescription = null,
                                     tint = MaterialTheme.colorScheme.secondary,
                                     modifier = Modifier.size(13.dp)
                                 )
                                 Text(
                                     text = strings.offlineBadge,
                                     fontSize = 10.sp,
                                     fontWeight = FontWeight.Bold,
                                     color = MaterialTheme.colorScheme.onSecondaryContainer
                                 )
                             }
                         }
                        }
                    )
                }
            },
            bottomBar = {
                Surface(shadowElevation = 10.dp, color = MaterialTheme.colorScheme.surface) {
                    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                        tabs.forEachIndexed { index, tab ->
                            val isSelected = !showPerfScreen && selectedTab == index
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = {
                                    showPerfScreen = false
                                    selectedTab = index
                                },
                                icon = { Icon(imageVector = tab.icon, contentDescription = tab.label) },
                                label = {
                                    Text(
                                        text = tab.label,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        maxLines = 1
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                if (showPerfScreen) {
                    PerformanceScreen()
                } else {
                    Crossfade(
                        targetState = selectedTab,
                        animationSpec = tween(durationMillis = 200),
                        label = "tabTransition"
                    ) { tabIndex ->
                        when (tabIndex) {
                            0 -> HomeDashboardTab(
                                profile = currentProfile,
                                strings = strings,
                                onNavigate = { target -> selectedTab = target },
                                onResetProfile = {
                                    ProfileStorage.clearProfile(context)
                                    profile = null
                                }
                            )
                            1 -> AssessmentScreen(
                                profile = currentProfile,
                                strings = strings,
                                onNavigateToRoadmap = { selectedTab = 2 }
                            )
                            2 -> RoadmapScreen(
                                profile = currentProfile,
                                strings = strings,
                                onNavigateToAssessment = { selectedTab = 1 }
                            )
                            3 -> MentorChatScreen(profile = currentProfile, strings = strings)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeDashboardTab(
    profile: StudentProfile,
    strings: UiStrings,
    onNavigate: (Int) -> Unit,
    onResetProfile: () -> Unit
) {
    val context = LocalContext.current
    val savedRoadmap = remember { AppStorage.loadRoadmap(context) }
    val savedGapReport = remember { AppStorage.loadGapReport(context) }
    val completedModules = savedRoadmap.count { it.isCompleted }
    val completionPct = if (savedRoadmap.isEmpty()) 0 else (completedModules * 100) / savedRoadmap.size
    var dailyGoalChecked by remember { mutableStateOf(false) }

    // 7-Day Consistency Strip State
    val weekDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    var activeDays by remember { mutableStateOf(setOf(0, 1)) }

    // 25-Minute Study Focus Timer State
    var secondsLeft by remember { mutableStateOf(25 * 60) }
    var isTimerRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning && secondsLeft > 0) {
            delay(1000L)
            secondsLeft -= 1
        }
        if (secondsLeft == 0) isTimerRunning = false
    }

    val mins = secondsLeft / 60
    val secs = secondsLeft % 60
    val formattedTimer = "%02d:%02d".format(mins, secs)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Banner with Radial Mastery Progress
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF1E3A8A), Color(0xFF1D4ED8), Color(0xFF0D9488))
                    ),
                    shape = RoundedCornerShape(22.dp)
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.16f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = Color(0xFFFDE047),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${activeDays.size}-DAY STREAK â€¢ ON-DEVICE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Text(
                        text = "${strings.welcomeBack}, ${profile.name}!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = profile.goal,
                        fontSize = 13.sp,
                        color = Color(0xFFE0F2FE)
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF0F172A).copy(alpha = 0.35f)
                    ) {
                        Text(
                            text = "${profile.subject} â€¢ ${profile.skillLevel}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFCCFBF1),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Circular Progress Ring
                Box(
                    modifier = Modifier.size(74.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { completionPct / 100f },
                        modifier = Modifier.fillMaxSize(),
                        color = Color(0xFF5EEAD4),
                        trackColor = Color.White.copy(alpha = 0.2f),
                        strokeWidth = 6.dp
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$completionPct%",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Mastery",
                            fontSize = 10.sp,
                            color = Color(0xFFDBEAFE)
                        )
                    }
                }
            }
        }

        // 7-Day Weekly Consistency Strip
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Weekly Study Consistency", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("${activeDays.size}/7 days", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.secondary)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    weekDays.forEachIndexed { idx, day ->
                        val checked = activeDays.contains(idx)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    activeDays = if (checked) activeDays - idx else activeDays + idx
                                }
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(if (checked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (checked) "âœ“" else day.take(1),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (checked) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(day, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // Daily Study Goal + 25-Min Focus Sprint Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                onClick = { dailyGoalChecked = !dailyGoalChecked },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                color = if (dailyGoalChecked) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, if (dailyGoalChecked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outlineVariant),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Daily Goal", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Icon(
                            imageVector = if (dailyGoalChecked) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (dailyGoalChecked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text("${profile.dailyHours} hrs/day", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                    Text(
                        text = if (dailyGoalChecked) "Logged offline âœ“" else "Tap to check in",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                            Text("Focus Sprint", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                    Text(formattedTimer, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilledTonalIconButton(
                            onClick = { isTimerRunning = !isTimerRunning },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Start/Pause",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        FilledTonalIconButton(
                            onClick = {
                                isTimerRunning = false
                                secondsLeft = 25 * 60
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset", modifier = Modifier.size(15.dp))
                        }
                    }
                }
            }
        }

        // Quick Actions Grid
        Text(strings.quickActions, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                onClick = { onNavigate(1) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Map, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                    Text(strings.studyRoadmap, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(
                        text = if (savedRoadmap.isEmpty()) strings.diagnosticDesc else "$completedModules/${savedRoadmap.size} modules done",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                onClick = { onNavigate(3) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                    }
                    Text(strings.aiMentor, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(strings.askDoubtsDesc, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        // Skill-Gap & Profile Summary Card
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
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(strings.learningProfile, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (savedGapReport != null) Color(0xFFCCFBF1) else Color(0xFFFEF3C7)
                    ) {
                        Text(
                            text = if (savedGapReport != null) "Gaps Mapped" else "Diagnostic Pending",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (savedGapReport != null) Color(0xFF0F766E) else Color(0xFF92400E),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                ProfileDetailRow("Daily Commitment", "${profile.dailyHours} hrs/day")
                ProfileDetailRow("Target Timeline", profile.targetDate)
                ProfileDetailRow("Regional Language", profile.preferredLanguage)
                ProfileDetailRow("Personal Interests", profile.interests)
            }
        }

        OutlinedButton(
            onClick = onResetProfile,
            modifier = Modifier.fillMaxWidth().height(46.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(strings.resetProfile, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun ProfileDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}
