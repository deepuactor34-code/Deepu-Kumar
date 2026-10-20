package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val category: String, // "preference", "personal", "emotional", "work", "fact"
    val isSensitive: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
