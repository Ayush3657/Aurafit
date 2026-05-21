package com.example.aurafit.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aurafit.ui.components.AuraGlassCard
import com.example.aurafit.ui.components.AuraGradientButton
import com.example.aurafit.ui.components.AuraGradientText
import com.example.aurafit.ui.main.ChatMessage
import com.example.aurafit.ui.main.MainScreenViewModel
import com.example.aurafit.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiCoachScreen(
    viewModel: MainScreenViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.chatMessages.collectAsState()
    val isLoading by viewModel.aiLoading.collectAsState()
    val apiKey by viewModel.apiKey.collectAsState()
    
    var inputText by remember { mutableStateOf("") }
    var showApiKeyConfig by remember { mutableStateOf(apiKey.isBlank()) }
    var enteredKey by remember { mutableStateOf(apiKey) }
    
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Scroll to bottom when messages list size changes
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Page Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Aura Coach", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("Your personalized AI trainer", color = TextSecondary, fontSize = 14.sp)
            }
            
            // Subtle API key indicator
            Text(
                text = if (apiKey.isBlank()) "⚠️ Offline Mode" else "⚡ AI Active",
                color = if (apiKey.isBlank()) AuraOrange else AuraCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { showApiKeyConfig = !showApiKeyConfig }
                    .background(Color(0x0AFFFFFF), RoundedCornerShape(12.dp))
                    .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // API Key Settings Panel (Collapsible)
        AnimatedVisibility(
            visible = showApiKeyConfig,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            AuraGlassCard(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                Text("GEMINI API KEY SETUP", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "To generate real-time AI nutrition plans and workout updates, get a free key from Google AI Studio and enter it below.",
                    color = TextMuted,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = enteredKey,
                        onValueChange = { enteredKey = it },
                        placeholder = { Text("AIzaSy...") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0x0DFFFFFF),
                            unfocusedContainerColor = Color(0x0DFFFFFF),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    AuraGradientButton(onClick = {
                        viewModel.saveApiKey(enteredKey)
                        showApiKeyConfig = false
                    }) {
                        Text("Save", color = Color.White)
                    }
                }
            }
        }

        // Messages list
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { msg ->
                ChatBubble(message = msg)
            }
            
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "Aura is tailoring insights...",
                            color = AuraPurple,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Quick Suggestions Prompts
        if (messages.size == 1) { // Only show when chat is starting
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickPromptChip("Get Daily Review") {
                    viewModel.getDailySummaryCoaching()
                }
                QuickPromptChip("Snack Idea") {
                    viewModel.askGemini("Suggest a healthy, high-protein snack for my goal.")
                }
                QuickPromptChip("Workout Check") {
                    viewModel.askGemini("Based on my logged workouts today, how was my training?")
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        // Chat Input Box
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x0AFFFFFF), RoundedCornerShape(24.dp))
                .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Ask Aura anything...", color = TextMuted) },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true
            )
            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        viewModel.askGemini(inputText)
                        inputText = ""
                    }
                },
                enabled = inputText.isNotBlank() && !isLoading
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = if (inputText.isNotBlank()) AuraCyan else TextMuted
                )
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val alignEnd = message.isUser
    val bubbleShape = if (alignEnd) {
        RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)
    }
    
    val bubbleBackground = if (alignEnd) {
        Brush.linearGradient(colors = listOf(AuraPurple, AuraPink))
    } else {
        Brush.linearGradient(colors = listOf(SurfaceDark, SurfaceDark))
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (alignEnd) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(bubbleShape)
                .background(bubbleBackground)
                .border(1.dp, if (alignEnd) Color.Transparent else GlassBorder, bubbleShape)
                .padding(12.dp)
        ) {
            Text(
                text = message.text,
                color = TextPrimary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun QuickPromptChip(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x0EFFFFFF))
            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text = text, color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}
