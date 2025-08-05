package com.mindguard.app.util

import com.mindguard.app.data.model.ArithmeticProblem
import com.mindguard.app.data.model.ProblemDifficulty
import kotlin.random.Random

object ArithmeticProblemGenerator {
    
    fun generateProblem(difficulty: ProblemDifficulty): ArithmeticProblem {
        return when (difficulty) {
            ProblemDifficulty.EASY -> generateEasyProblem()
            ProblemDifficulty.MEDIUM -> generateMediumProblem()
            ProblemDifficulty.HARD -> generateHardProblem()
        }
    }
    
    private fun generateEasyProblem(): ArithmeticProblem {
        val operations = listOf("+", "-", "×")
        val operation = operations.random()
        
        val (num1, num2, answer) = when (operation) {
            "+" -> {
                val a = Random.nextInt(1, 10)
                val b = Random.nextInt(1, 10)
                Triple(a, b, a + b)
            }
            "-" -> {
                val a = Random.nextInt(5, 20)
                val b = Random.nextInt(1, a)
                Triple(a, b, a - b)
            }
            "×" -> {
                val a = Random.nextInt(2, 10)
                val b = Random.nextInt(2, 10)
                Triple(a, b, a * b)
            }
            else -> {
                val a = Random.nextInt(1, 10)
                val b = Random.nextInt(1, 10)
                Triple(a, b, a + b)
            }
        }
        
        return ArithmeticProblem(
            question = "$num1 $operation $num2 = ?",
            answer = answer,
            difficulty = ProblemDifficulty.EASY,
            timeLimit = 30,
            extraTimeReward = 5
        )
    }
    
    private fun generateMediumProblem(): ArithmeticProblem {
        val operations = listOf("+", "-", "×", "÷")
        val operation = operations.random()
        
        val (num1, num2, answer) = when (operation) {
            "+" -> {
                val a = Random.nextInt(10, 100)
                val b = Random.nextInt(10, 100)
                Triple(a, b, a + b)
            }
            "-" -> {
                val a = Random.nextInt(50, 200)
                val b = Random.nextInt(10, a)
                Triple(a, b, a - b)
            }
            "×" -> {
                val a = Random.nextInt(10, 25)
                val b = Random.nextInt(2, 12)
                Triple(a, b, a * b)
            }
            "÷" -> {
                val b = Random.nextInt(2, 12)
                val answer = Random.nextInt(5, 20)
                val a = b * answer
                Triple(a, b, answer)
            }
            else -> {
                val a = Random.nextInt(10, 100)
                val b = Random.nextInt(10, 100)
                Triple(a, b, a + b)
            }
        }
        
        return ArithmeticProblem(
            question = "$num1 $operation $num2 = ?",
            answer = answer,
            difficulty = ProblemDifficulty.MEDIUM,
            timeLimit = 45,
            extraTimeReward = 10
        )
    }
    
    private fun generateHardProblem(): ArithmeticProblem {
        val problemTypes = listOf("multiplication", "division", "mixed")
        val problemType = problemTypes.random()
        
        val result = when (problemType) {
            "multiplication" -> {
                val a = Random.nextInt(20, 50)
                val b = Random.nextInt(10, 25)
                Pair("$a × $b = ?", a * b)
            }
            "division" -> {
                val b = Random.nextInt(5, 15)
                val answer = Random.nextInt(8, 25)
                val a = b * answer
                Pair("$a ÷ $b = ?", answer)
            }
            "mixed" -> {
                val a = Random.nextInt(10, 30)
                val b = Random.nextInt(5, 15)
                val c = Random.nextInt(2, 8)
                val answer = (a + b) * c
                Pair("($a + $b) × $c = ?", answer)
            }
            else -> {
                val a = Random.nextInt(20, 50)
                val b = Random.nextInt(10, 25)
                Pair("$a × $b = ?", a * b)
            }
        }
        val question = result.first
        val answer = result.second
        
        return ArithmeticProblem(
            question = question,
            answer = answer,
            difficulty = ProblemDifficulty.HARD,
            timeLimit = 60,
            extraTimeReward = 15
        )
    }
} 