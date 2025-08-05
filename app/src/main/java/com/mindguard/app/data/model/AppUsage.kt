package com.mindguard.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_usage")
data class AppUsage(
    @PrimaryKey val packageName: String,
    val appName: String,
    val dailyTimeLimit: Long, // in minutes
    val currentUsage: Long = 0, // in minutes
    val isEnabled: Boolean = true,
    val lastResetDate: Long = System.currentTimeMillis()
) 