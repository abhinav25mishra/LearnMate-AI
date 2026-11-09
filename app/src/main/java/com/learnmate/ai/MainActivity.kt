package com.learnmate.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                LearnMateApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnMateApp() {
    val context = LocalContext.current
    var profile by remember { mutableStateOf(ProfileStorage.loadProfile(context)) }
    var selectedTab by remember { mutableStateOf(0) }

    if (profile == null) {
        Surface(modifier = Modifier.fillMaxSize()) {
            OnboardingScreen(
                onComplete = { newProfile ->
                    ProfileStorage.saveProfile(context, newProfile)
                    profile = newProfile
                }
            )
        }
    } else {
        val currentProfile = profile!!
        val tabs = listOf("Home", "Roadmap", "AI Mentor", "Perf")

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("LearnMate AI", fontWeight = FontWeight.Bold) },
                    actions = {
                        AssistChip(
                            onClick = {},
                            label = { Text("100% Offline") },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    tabs.forEachIndexed { index, title ->
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            icon = { Text(title.take(1), fontWeight = FontWeight.Bold) },
                            label = { Text(title) }
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
fun HomeDashboardTab(profile: StudentProfile, onResetProfile: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Hello, ${profile.name}!", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Student Profile", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Subject: ${profile.subject}")
                Text("Goal: ${profile.goal}")
                Text("Level: ${profile.skillLevel}")
                Text("Daily Study: ${profile.dailyHours} hrs | Target: ${profile.targetDate}")
                Text("Language: ${profile.preferredLanguage}")
            }
        }
        OutlinedButton(onClick = onResetProfile) {
            Text("Edit / Reset Profile")
        }
    }
}

@Composable
fun PlaceholderTab(title: String, subtitle: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(subtitle, fontSize = 14.sp)
    }
}