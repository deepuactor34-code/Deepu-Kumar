package com.example.data.repository

import com.example.data.dao.MemoryDao
import com.example.data.entity.MemoryEntity
import kotlinx.coroutines.flow.Flow

class MemoryRepository(private val memoryDao: MemoryDao) {
    val allMemories: Flow<List<MemoryEntity>> = memoryDao.getAllMemories()
    val nonSensitiveMemories: Flow<List<MemoryEntity>> = memoryDao.getNonSensitiveMemories()

    suspend fun getNonSensitiveMemoriesList(): List<MemoryEntity> {
        return memoryDao.getNonSensitiveMemoriesList()
    }

    suspend fun insertMemory(memory: MemoryEntity): Long {
        return memoryDao.insertMemory(memory)
    }

    suspend fun updateMemory(memory: MemoryEntity) {
        memoryDao.updateMemory(memory)
    }

    suspend fun deleteMemory(memory: MemoryEntity) {
        memoryDao.deleteMemory(memory)
    }

    suspend fun deleteMemoryById(id: Long) {
        memoryDao.deleteMemoryById(id)
    }

    suspend fun clearAll() {
        memoryDao.clearAllMemories()
    }
}
