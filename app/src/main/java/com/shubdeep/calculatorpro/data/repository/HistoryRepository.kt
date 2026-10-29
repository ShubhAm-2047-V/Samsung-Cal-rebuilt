package com.shubdeep.calculatorpro.data.repository

import com.shubdeep.calculatorpro.data.local.CalculationEntity
import com.shubdeep.calculatorpro.data.local.CalculationHistoryDao
import com.shubdeep.calculatorpro.data.local.CalculationType
import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val dao: CalculationHistoryDao) {

    fun getAllHistory(): Flow<List<CalculationEntity>> = dao.getAllHistory()

    fun getHistoryByType(type: CalculationType): Flow<List<CalculationEntity>> = dao.getHistoryByType(type)

    fun searchHistory(query: String): Flow<List<CalculationEntity>> = dao.searchHistory(query)

    suspend fun addRecord(
        expression: String,
        result: String,
        type: CalculationType = CalculationType.STANDARD,
        details: String? = null
    ): Long {
        if (expression.isBlank() || result.isBlank()) return -1
        val entity = CalculationEntity(
            expression = expression,
            result = result,
            type = type,
            timestamp = System.currentTimeMillis(),
            details = details
        )
        return dao.insert(entity)
    }

    suspend fun deleteRecord(entity: CalculationEntity) {
        dao.delete(entity)
    }

    suspend fun deleteById(id: Long) {
        dao.deleteById(id)
    }

    suspend fun clearAll() {
        dao.clearAll()
    }
}
