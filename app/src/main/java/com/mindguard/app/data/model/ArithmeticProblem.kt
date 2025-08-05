package com.mindguard.app.data.model

data class ArithmeticProblem(
    val question: String,
    val answer: Int,
    val difficulty: ProblemDifficulty,
    val timeLimit: Long = 60, // seconds
    val extraTimeReward: Long = 10 // minutes
)

enum class ProblemDifficulty {
    EASY, MEDIUM, HARD
} 