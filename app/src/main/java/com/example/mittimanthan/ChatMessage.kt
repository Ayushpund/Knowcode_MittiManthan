package com.example.mittimanthan

data class ChatMessage(
    val text: String,
    val isBot: Boolean,
    val isLoading: Boolean = false
)