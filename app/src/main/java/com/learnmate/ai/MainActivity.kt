package com.learnmate.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.OfflineBolt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val LearnMateColorScheme = lightColorScheme(
    primary = Color(0xFF1E3A8A),        // Deep Indigo
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E3A8A),
    secondary = Color(0xFF0D9488),      // Teal
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCCFBF1),
    onSecondaryContainer = Color(0xFF115E59),
    background = Color(0xFFF8FAFC),
    surface = Color.White,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = LearnMateColorScheme) {
                LearnMateApp()
            }
        }
    }
}

private data class NavTab(val label: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnMateApp() {
    val context = LocalContext.current
    var profile by remember { mutableStateOf(ProfileStorage.loadProfile(context)) }
    var selectedTab by remember { mutableStateOf(0) }

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
            NavTab(strings.tabRoadmap, Icons.Default.Map),
            NavTab(strings.tabMentor, Icons.Default.AutoAwesome),
            NavTab(strings.tabPerf, Icons.Default.Speed)
        )

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "LearnMate AI",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    actions = {
                        AssistChip(
                            onClick = {},
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.OfflineBolt,
                                    contentDescription = null,
                                    tint = Color(0xFF0D9488),
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = strings.offlineBadge,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF0F766E)
                                )
                            },
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    }
                )
            },
            bottomBar = {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    tabs.forEachIndexed { index, tab ->
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            icon = { Icon(imageVector = tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label, fontSize = 11.sp, maxLines = 1) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                when (selectedTab) {
                    0 -> HomeDashboardTab(
                        profile = currentProfile,
                        strings = strings,
                        onNavigate = { tabIndex -> selectedTab = tabIndex },
                        onResetProfile = {
                            ProfileStorage.clearProfile(context)
                            profile = null
                        }
                    )
                    1 -> RoadmapScreen(profile = currentProfile)
                    2 -> MentorChatScreen(profile = currentProfile)
                    3 -> PerformanceScreen()
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "${strings.welcomeBack}, ${profile.name}!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Goal: ${profile.goal}",
                    fontSize = 14.sp,
                    color = Color(0xFFDBEAFE)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF0D9488)
                ) {
                    Text(
                        text = "${profile.subject} • ${profile.skillLevel}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Quick Actions
        Text(strings.quickActions, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ElevatedCard(
                onClick = { onNavigate(1) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(strings.studyRoadmap, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(strings.diagnosticDesc, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            ElevatedCard(
                onClick = { onNavigate(2) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Text(strings.aiMentor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(strings.askDoubtsDesc, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        // Profile Details Card
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(strings.learningProfile, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                HorizontalDivider()
                Text("Daily Study: ${profile.dailyHours} hrs/day", fontSize = 14.sp)
                Text("Target Timeline: ${profile.targetDate}", fontSize = 14.sp)
                Text("Preferred Language: ${profile.preferredLanguage}", fontSize = 14.sp)
                Text("Interests: ${profile.interests}", fontSize = 14.sp)
            }
        }

        OutlinedButton(
            onClick = onResetProfile,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(strings.resetProfile)
        }
    }
}