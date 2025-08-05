package com.mindguard.app.data.repository

import com.mindguard.app.data.database.DailyStatisticsDao
import com.mindguard.app.data.model.DailyStatistics
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DailyStatisticsRepository(
    private val dailyStatisticsDao: DailyStatisticsDao
) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    suspend fun getTodayStatistics(): DailyStatistics {
        val today = dateFormat.format(Date())
        return dailyStatisticsDao.getStatisticsForDate(today) ?: createTodayStatistics()
    }

    private suspend fun createTodayStatistics(): DailyStatistics {
        val today = dateFormat.format(Date())
        val newStats = DailyStatistics(date = today)
        dailyStatisticsDao.insertStatistics(newStats)
        return newStats
    }

    fun getRecentStatistics(): Flow<List<DailyStatistics>> =
        dailyStatisticsDao.getRecentStatistics()

    suspend fun updateTodayScreenTime(screenTime: Long) {
        val today = dateFormat.format(Date())
        ensureTodayStatsExist()
        dailyStatisticsDao.updateScreenTime(today, screenTime)
    }

    suspend fun updateTimeSaved(timeSaved: Long) {
        val today = dateFormat.format(Date())
        ensureTodayStatsExist()
        dailyStatisticsDao.updateTimeSaved(today, timeSaved)
    }

    suspend fun incrementProblemsSolved() {
        val today = dateFormat.format(Date())
        ensureTodayStatsExist()
        dailyStatisticsDao.incrementProblemsSolved(today)
    }

    suspend fun incrementBreaksTaken() {
        val today = dateFormat.format(Date())
        ensureTodayStatsExist()
        dailyStatisticsDao.incrementBreaksTaken(today)
    }

    private suspend fun ensureTodayStatsExist() {
        val today = dateFormat.format(Date())
        if (dailyStatisticsDao.getStatisticsForDate(today) == null) {
            createTodayStatistics()
        }
    }

    private fun getTodayDateString(): String {
        return dateFormat.format(Date())
    }

    suspend fun resetTodayStatistics() {
        val today = getTodayDateString()
        val resetStats = DailyStatistics(
            date = today, totalScreenTime = 0L, problemsSolved = 0, breaksTaken = 0, timeSaved = 0L
        )
        dailyStatisticsDao.insertStatistics(resetStats)
    }
}