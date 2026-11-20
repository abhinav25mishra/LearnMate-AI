package com.learnmate.ai

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    strings: UiStrings = StringsManager.getStrings(profile.preferredLanguage),
    mentorEngine: AiMentorEngine = LocalLlamaEngine
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val gapReport = remember { AppStorage.loadGapReport(context) }

    var inputText by remember { mutableStateOf("") }
    var isThinking by remember { mutableStateOf(false) }
    var messages by remember {
        val saved = AppStorage.loadChatHistory(context)
        mutableStateOf(
            saved.ifEmpty {
                listOf(ChatMessage("VicharaX", strings.mentorGreeting))
            }
        )
    }

    LaunchedEffect(messages.size, messages.lastOrNull()?.text?.length) {
        if (messages.isNotEmpty()) {
            listState.scrollToItem(messages.lastIndex)
        }
    }

    fun sendQuestion(rawQuestion: String) {
        val q = rawQuestion.trim()
        if (q.isEmpty() || isThinking) return

        ProfileStorage.recordActivityToday(context, profile.name)
        val withUser = messages + ChatMessage("You", q)
        messages = withUser
        inputText = ""
        isThinking = true

        val selectedTopic = profile.subject.ifBlank { "Python" }
        val extraTopics = (gapReport?.weakTopics ?: emptyList()) + (gapReport?.missingPrerequisites ?: emptyList())
        if (!TopicGuardrail.isQuestionAllowed(q, selectedTopic, extraTopics)) {
            val refusalMsg = TopicGuardrail.buildRefusalMessage(selectedTopic)
            val updated = withUser + ChatMessage("VicharaX", refusalMsg)
            messages = updated
            AppStorage.saveChatHistory(context, updated)
            isThinking = false
            return
        }

        val streamingPlaceholder = withUser + ChatMessage("VicharaX", "")
        messages = streamingPlaceholder

        coroutineScope.launch {
            val reply = mentorEngine.askMentorStream(
                question = q,
                profile = profile,
                difficulty = profile.skillLevel.ifBlank { "Beginner" },
                hintOnly = false,
                onToken = { partialText ->
                    if (partialText.isNotEmpty()) {
                        messages = withUser + ChatMessage("VicharaX", partialText)
                    }
                }
            )
            val updated = withUser + ChatMessage("VicharaX", reply)
            messages = updated
            AppStorage.saveChatHistory(context, updated)
            isThinking = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Minimal single-line topic scope bar with clear chat button (no tutor/level selector box)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(
                    text = "Topic Scope: ${profile.subject} • ${profile.preferredLanguage}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }

            IconButton(
                onClick = {
                    val reset = listOf(ChatMessage("VicharaX", strings.mentorGreeting))
                    messages = reset
                    AppStorage.saveChatHistory(context, reset)
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Clear Chat",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Clean Direct Q&A Message List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(messages) { msg ->
                val isUser = msg.sender == "You"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = if (isUser) 18.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 18.dp
                        ),
                        color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        border = if (isUser) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        shadowElevation = 1.dp,
                        modifier = Modifier.widthIn(max = 310.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = msg.sender,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isUser) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = msg.text.ifEmpty { "..." },
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Input Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text(strings.askPlaceholder, fontSize = 14.sp) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                singleLine = false,
                maxLines = 3
            )

            FilledIconButton(
                onClick = { sendQuestion(inputText) },
                enabled = inputText.isNotBlank() && !isThinking,
                modifier = Modifier.size(50.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send"
                )
            }
        }
    }
}
