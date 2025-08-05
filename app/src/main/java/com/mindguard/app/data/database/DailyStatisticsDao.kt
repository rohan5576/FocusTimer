package com.mindguard.app.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mindguard.app.data.model.DailyStatistics
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyStatisticsDao {

    @Query("SELECT * FROM daily_statistics WHERE date = :date")
    suspend fun getStatisticsForDate(date: String): DailyStatistics?

    @Query("SELECT * FROM daily_statistics ORDER BY date DESC LIMIT 7")
    fun getRecentStatistics(): Flow<List<DailyStatistics>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatistics(stats: DailyStatistics)

    @Update
    suspend fun updateStatistics(stats: DailyStatistics)

    @Query("UPDATE daily_statistics SET problemsSolved = problemsSolved + 1 WHERE date = :date")
    suspend fun incrementProblemsSolved(date: String)

    @Query("UPDATE daily_statistics SET breaksTaken = breaksTaken + 1 WHERE date = :date")
    suspend fun incrementBreaksTaken(date: String)

    @Query("UPDATE daily_statistics SET timeSaved = :timeSaved WHERE date = :date")
    suspend fun updateTimeSaved(date: String, timeSaved: Long)

    @Query("UPDATE daily_statistics SET totalScreenTime = :screenTime WHERE date = :date")
    suspend fun updateScreenTime(date: String, screenTime: Long)
}