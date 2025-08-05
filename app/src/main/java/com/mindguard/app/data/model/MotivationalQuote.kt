package com.mindguard.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "motivational_quotes")
data class MotivationalQuote(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val quote: String,
    val author: String,
    val category: QuoteCategory,
    val isActive: Boolean = true
)

enum class QuoteCategory {
    PRODUCTIVITY, MINDFULNESS, GOAL_SETTING, SELF_IMPROVEMENT, DIGITAL_WELLNESS
} 