package com.learnmate.ai

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ChatMessage(
    val sender: String,
    val text: String
)

@Composable
fun MentorChatScreen(
    profile: StudentProfile,
    strings: UiStrings = StringsManager.getStrings(profile.preferredLanguage),
    mentorEngine: AiMentorEngine = LocalLlamaEngine
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var input by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf(profile.skillLevel) }
    var hintOnly by remember { mutableStateOf(true) }
    var isThinking by remember { mutableStateOf(false) }

    val gapReport = remember { AppStorage.loadGapReport(context) }

    val welcomeMessage = remember(profile.name, profile.subject, profile.preferredLanguage, gapReport) {
        val gapNote = if (gapReport != null && gapReport.weakTopics.isNotEmpty()) {
            " I noticed from your diagnostic that we should reinforce ${gapReport.weakTopics.joinToString(", ")}."
        } else {
            ""
        }
        ChatMessage(
            sender = "LearnMate AI",
            text = "Namaste ${profile.name}! I'm your offline ${profile.subject} mentor (${profile.preferredLanguage}).$gapNote Ask me any concept or tap a prompt below!"
        )
    }

    var messages by remember {
        val saved = AppStorage.loadChatHistory(context)
        mutableStateOf(if (saved.isNotEmpty()) saved else listOf(welcomeMessage))
    }

    val quickPrompts = remember(profile.subject, gapReport) {
        val weakFocus = gapReport?.weakTopics?.firstOrNull()
        listOfNotNull(
            weakFocus?.let { "Help me master $it step-by-step" },
            "Explain ${profile.subject} basics with an example",
            "Give me a 15-min practice problem"
        )
    }

    fun sendQuestion(questionText: String) {
        val q = questionText.trim()
        if (q.isEmpty() || isThinking) return
        input = ""
        val withUser = messages + ChatMessage("You", q)
        messages = withUser
        AppStorage.saveChatHistory(context, withUser)
        isThinking = true

        // Inject diagnosed weak topics into prompt context automatically
        val contextualPrompt = if (gapReport != null && gapReport.weakTopics.isNotEmpty()) {
            "[Student Weak Topics: ${gapReport.weakTopics.joinToString(", ")}; Missing Prerequisites: ${gapReport.missingPrerequisites.joinToString(", ")}] $q"
        } else {
            q
        }

        scope.launch {
            val fullReply = mentorEngine.askMentor(contextualPrompt, profile, difficulty, hintOnly)
            isThinking = false

            // Stream response word-by-word for smooth real-time token rendering
            val baseMessages = messages
            val words = fullReply.split(" ")
            val builder = StringBuilder()
            for (i in words.indices) {
                if (i > 0) builder.append(" ")
                builder.append(words[i])
                messages = baseMessages + ChatMessage("LearnMate AI", builder.toString())
                delay(28L)
            }
            AppStorage.saveChatHistory(context, messages)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header & Mode Controls
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFCCFBF1)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF0D9488), modifier = Modifier.size(18.dp))
                        }
                        Column {
                            Text("Llama 3.2 On-Device Tutor", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(
                                text = if (gapReport?.weakTopics?.isNotEmpty() == true) {
                                    "Context: ${gapReport.weakTopics.joinToString(", ")} • ${profile.preferredLanguage}"
                                } else {
                                    "Language: ${profile.preferredLanguage} • 100% Local"
                                },
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(
                        onClick = {
                            messages = listOf(welcomeMessage)
                            AppStorage.saveChatHistory(context, messages)
                        }
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Clear Chat", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Beginner", "Intermediate").forEach { level ->
                        FilterChip(
                            selected = difficulty == level,
                            onClick = { difficulty = level },
                            label = { Text(level, fontSize = 12.sp) }
                        )
                    }
                    FilterChip(
                        selected = hintOnly,
                        onClick = { hintOnly = !hintOnly },
                        label = { Text(if (hintOnly) strings.socraticHints else strings.fullSolution, fontSize = 12.sp) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Chat Feed
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { msg ->
                val isUser = msg.sender == "You"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
                    verticalAlignment = Alignment.Top
                ) {
                    if (!isUser) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0D9488)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Surface(
                        modifier = Modifier.widthIn(max = 275.dp),
                        shape = RoundedCornerShape(
                            topStart = if (isUser) 18.dp else 4.dp,
                            topEnd = if (isUser) 4.dp else 18.dp,
                            bottomStart = 18.dp,
                            bottomEnd = 18.dp
                        ),
                        color = if (isUser) Color(0xFF1D4ED8) else MaterialTheme.colorScheme.surface,
                        border = if (isUser) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        shadowElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(13.dp)) {
                            Text(
                                text = if (isUser) "You" else "LearnMate AI • Offline",
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = if (isUser) Color(0xFFBAE6FD) else Color(0xFF0D9488)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = msg.text,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    if (isUser) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFDBEAFE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF1D4ED8), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        if (isThinking) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF0FDFA),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = Color(0xFF0D9488))
                    Text("Generating offline response on Arm CPU...", fontSize = 12.sp, color = Color(0xFF0F766E))
                }
            }
        }

        // Quick Prompt Suggestions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickPrompts.forEach { prompt ->
                SuggestionChip(
                    onClick = { sendQuestion(prompt) },
                    label = { Text(prompt, fontSize = 12.sp) },
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }

        // Elevated Input Dock
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(start = 16.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = input,
                    onValueChange = { input = it },
                    placeholder = { Text(strings.askPlaceholder, fontSize = 14.sp) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
                FilledIconButton(
                    onClick = { sendQuestion(input) },
                    modifier = Modifier.size(42.dp),
                    shape = CircleShape
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}