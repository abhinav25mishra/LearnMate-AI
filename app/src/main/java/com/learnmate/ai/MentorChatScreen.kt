package com.learnmate.ai

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

data class ChatMessage(
    val sender: String,
    val text: String
)

@Composable
fun MentorChatScreen(
    profile: StudentProfile,
    mentorEngine: AiMentorEngine = LocalLlamaEngine
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var input by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf(profile.skillLevel) }
    var hintOnly by remember { mutableStateOf(true) }
    var isThinking by remember { mutableStateOf(false) }

    val welcomeMessage = remember(profile.name, profile.subject, profile.preferredLanguage) {
        ChatMessage(
            sender = "LearnMate AI",
            text = "Hi ${profile.name}! I'm your offline ${profile.subject} mentor (${profile.preferredLanguage}). Ask me any concept or paste a mistake!"
        )
    }

    var messages by remember {
        val saved = AppStorage.loadChatHistory(context)
        mutableStateOf(if (saved.isNotEmpty()) saved else listOf(welcomeMessage))
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "On-Device AI Mentor (${profile.preferredLanguage})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            TextButton(
                onClick = {
                    messages = listOf(welcomeMessage)
                    AppStorage.saveChatHistory(context, messages)
                }
            ) {
                Text("Clear")
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("Beginner", "Intermediate").forEach { level ->
                FilterChip(
                    selected = difficulty == level,
                    onClick = { difficulty = level },
                    label = { Text(level) }
                )
            }
            FilterChip(
                selected = hintOnly,
                onClick = { hintOnly = !hintOnly },
                label = { Text(if (hintOnly) "Hints: ON" else "Full Ans") }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { msg ->
                val isUser = msg.sender == "You"
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isUser) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(msg.sender, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(msg.text, fontSize = 14.sp)
                    }
                }
            }
        }

        if (isThinking) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                placeholder = { Text("Ask a question...") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Button(
                onClick = {
                    val q = input.trim()
                    if (q.isNotEmpty() && !isThinking) {
                        input = ""
                        val withUser = messages + ChatMessage("You", q)
                        messages = withUser
                        AppStorage.saveChatHistory(context, withUser)
                        isThinking = true
                        scope.launch {
                            val reply = mentorEngine.askMentor(q, profile, difficulty, hintOnly)
                            val withAi = messages + ChatMessage("LearnMate AI", reply)
                            messages = withAi
                            AppStorage.saveChatHistory(context, withAi)
                            isThinking = false
                        }
                    }
                }
            ) {
                Text("Send")
            }
        }
    }
}