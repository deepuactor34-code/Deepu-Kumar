package com.example.data.repository

import com.example.data.dao.ChatDao
import com.example.data.entity.ChatEntity
import kotlinx.coroutines.flow.Flow

class ChatRepository(private val chatDao: ChatDao) {
    val allMessages: Flow<List<ChatEntity>> = chatDao.getAllMessages()

    fun getMessagesByMode(mode: String): Flow<List<ChatEntity>> {
        return chatDao.getMessagesByMode(mode)
    }

    suspend fun getRecentMessages(limit: Int = 10): List<ChatEntity> {
        return chatDao.getRecentMessages(limit)
    }

    suspend fun insertMessage(sender: String, content: String, mode: String = "chat", language: String = "hinglish"): Long {
        val entity = ChatEntity(
            sender = sender,
            content = content,
            mode = mode,
            language = language
        )
        return chatDao.insertMessage(entity)
    }

    suspend fun clearHistory() {
        chatDao.clearHistory()
    }
}
