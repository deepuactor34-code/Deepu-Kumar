package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sender: String, // "user" or "yesmin"
    val content: String,
    val mode: String = "chat", // "chat", "voice_call", "coding", "automation"
    val language: String = "hinglish", // "en", "hi", "hinglish"
    val timestamp: Long = System.currentTimeMillis()
)
