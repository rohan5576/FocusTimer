package com.mindguard.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_statistics")
data class DailyStatistics(
    @PrimaryKey val date: String, // Format: yyyy-MM-dd
    val totalScreenTime: Long = 0, // in minutes
    val problemsSolved: Int = 0,
    val problemsAttempted: Int = 0,
    val breaksTaken: Int = 0,
    val timeSaved: Long = 0, // in minutes
    val appsBlocked: Int = 0,
    val streakDays: Int = 0,
    val focusSessions: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_session_logs")
data class AppSessionLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val packageName: String,
    val appName: String,
    val sessionStartTime: Long,
    val sessionEndTime: Long,
    val sessionDuration: Long, // in milliseconds
    val wasBlocked: Boolean = false,
    val problemSolved: Boolean = false,
    val date: String // Format: yyyy-MM-dd
)

@Entity(tableName = "blocking_events")
data class BlockingEvent(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val packageName: String,
    val appName: String,
    val blockedAt: Long,
    val action: BlockingAction,
    val problemAttempted: Boolean = false,
    val problemSolved: Boolean = false,
    val extraTimeEarned: Long = 0, // in minutes
    val date: String // Format: yyyy-MM-dd
)

enum class BlockingAction {
    BREAK_TAKEN, PROBLEM_SOLVED, FORCE_CLOSED, CONTINUED_ANYWAY
}

@Entity(tableName = "focus_sessions")
data class FocusSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startTime: Long,
    val endTime: Long? = null,
    val duration: Long = 0, // in minutes
    val targetDuration: Long, // in minutes
    val isCompleted: Boolean = false,
    val appsBlocked: List<String> = emptyList(), // JSON string
    val date: String // Format: yyyy-MM-dd
)