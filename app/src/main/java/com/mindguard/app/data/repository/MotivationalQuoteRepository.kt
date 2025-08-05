package com.mindguard.app.data.repository

import com.mindguard.app.data.database.MotivationalQuoteDao
import com.mindguard.app.data.model.MotivationalQuote
import com.mindguard.app.data.model.QuoteCategory
import kotlinx.coroutines.flow.Flow

class MotivationalQuoteRepository(
    private val motivationalQuoteDao: MotivationalQuoteDao
) {

    fun getAllActiveQuotes(): Flow<List<MotivationalQuote>> =
        motivationalQuoteDao.getAllActiveQuotes()

    fun getQuotesByCategory(category: QuoteCategory): Flow<List<MotivationalQuote>> =
        motivationalQuoteDao.getQuotesByCategory(category)

    suspend fun getRandomQuote(): MotivationalQuote? = motivationalQuoteDao.getRandomQuote()

    suspend fun getRandomQuoteByCategory(category: QuoteCategory): MotivationalQuote? =
        motivationalQuoteDao.getRandomQuoteByCategory(category)

    suspend fun insertQuote(quote: MotivationalQuote) = motivationalQuoteDao.insertQuote(quote)

    suspend fun insertQuotes(quotes: List<MotivationalQuote>) =
        motivationalQuoteDao.insertQuotes(quotes)

    suspend fun updateQuote(quote: MotivationalQuote) = motivationalQuoteDao.updateQuote(quote)

    suspend fun deleteQuote(quote: MotivationalQuote) = motivationalQuoteDao.deleteQuote(quote)

    // Initialize with default motivational quotes
    suspend fun initializeDefaultQuotes() {
        // Try to fetch quotes from online sources first
        try {
            val onlineQuotes = com.mindguard.app.data.remote.QuoteApiService.fetchMultipleQuotes(15)
            val motivationalQuotes = onlineQuotes.map { quoteResponse ->
                MotivationalQuote(
                    quote = quoteResponse.quote,
                    author = quoteResponse.author,
                    category = when (quoteResponse.category.lowercase()) {
                        "motivation", "motivational" -> QuoteCategory.SELF_IMPROVEMENT
                        "success" -> QuoteCategory.GOAL_SETTING
                        "wisdom" -> QuoteCategory.MINDFULNESS
                        "productivity" -> QuoteCategory.PRODUCTIVITY
                        "digital wellness", "technology" -> QuoteCategory.DIGITAL_WELLNESS
                        else -> QuoteCategory.SELF_IMPROVEMENT
                    }
                )
            }
            motivationalQuoteDao.insertQuotes(motivationalQuotes)
            return
        } catch (e: Exception) {
            println("Failed to fetch online quotes, using offline fallback: ${e.message}")
        }

        // Fallback to offline quotes
        val defaultQuotes = listOf(
            MotivationalQuote(
                quote = "The best time to plant a tree was 20 years ago. The second best time is now.",
                author = "Chinese Proverb",
                category = QuoteCategory.PRODUCTIVITY
            ), MotivationalQuote(
                quote = "Your time is limited, don't waste it living someone else's life.",
                author = "Steve Jobs",
                category = QuoteCategory.SELF_IMPROVEMENT
            ), MotivationalQuote(
                quote = "The only way to do great work is to love what you do.",
                author = "Steve Jobs",
                category = QuoteCategory.PRODUCTIVITY
            ), MotivationalQuote(
                quote = "Mindfulness isn't difficult. We just need to remember to do it.",
                author = "Sharon Salzberg",
                category = QuoteCategory.MINDFULNESS
            ), MotivationalQuote(
                quote = "Set your goals high, and don't stop till you get there.",
                author = "Bo Jackson",
                category = QuoteCategory.GOAL_SETTING
            ), MotivationalQuote(
                quote = "The present moment is the only time over which we have dominion.",
                author = "Thích Nhất Hạnh",
                category = QuoteCategory.MINDFULNESS
            ), MotivationalQuote(
                quote = "Success is not final, failure is not fatal: it is the courage to continue that counts.",
                author = "Winston Churchill",
                category = QuoteCategory.SELF_IMPROVEMENT
            ), MotivationalQuote(
                quote = "The future depends on what you do today.",
                author = "Mahatma Gandhi",
                category = QuoteCategory.GOAL_SETTING
            ), MotivationalQuote(
                quote = "Digital minimalism is the philosophy of being intentional about what technology you let into your life.",
                author = "Cal Newport",
                category = QuoteCategory.DIGITAL_WELLNESS
            ), MotivationalQuote(
                quote = "The more you use social media, the less social you become.",
                author = "Anonymous",
                category = QuoteCategory.DIGITAL_WELLNESS
            ), MotivationalQuote(
                quote = "Focus on being productive instead of busy.",
                author = "Tim Ferriss",
                category = QuoteCategory.PRODUCTIVITY
            ), MotivationalQuote(
                quote = "The mind is everything. What you think you become.",
                author = "Buddha",
                category = QuoteCategory.MINDFULNESS
            ), MotivationalQuote(
                quote = "Don't watch the clock; do what it does. Keep going.",
                author = "Sam Levenson",
                category = QuoteCategory.PRODUCTIVITY
            ), MotivationalQuote(
                quote = "The only limit to our realization of tomorrow is our doubts of today.",
                author = "Franklin D. Roosevelt",
                category = QuoteCategory.GOAL_SETTING
            ), MotivationalQuote(
                quote = "Be present in all things and thankful for all things.",
                author = "Maya Angelou",
                category = QuoteCategory.MINDFULNESS
            )
        )

        motivationalQuoteDao.insertQuotes(defaultQuotes)
    }

    suspend fun refreshQuotesFromOnline() {
        try {
            val onlineQuotes = com.mindguard.app.data.remote.QuoteApiService.fetchMultipleQuotes(20)
            val motivationalQuotes = onlineQuotes.map { quoteResponse ->
                MotivationalQuote(
                    quote = quoteResponse.quote,
                    author = quoteResponse.author,
                    category = when (quoteResponse.category.lowercase()) {
                        "motivation", "motivational" -> QuoteCategory.SELF_IMPROVEMENT
                        "success" -> QuoteCategory.GOAL_SETTING
                        "wisdom" -> QuoteCategory.MINDFULNESS
                        "productivity" -> QuoteCategory.PRODUCTIVITY
                        "digital wellness", "technology" -> QuoteCategory.DIGITAL_WELLNESS
                        else -> QuoteCategory.SELF_IMPROVEMENT
                    }
                )
            }
            motivationalQuoteDao.insertQuotes(motivationalQuotes)
        } catch (e: Exception) {
            println("Failed to refresh quotes from online: ${e.message}")
            throw e
        }
    }

    suspend fun getDailyQuote(): MotivationalQuote? {
        return try {
            // Try to get a fresh quote from online
            val onlineQuote = com.mindguard.app.data.remote.QuoteApiService.fetchRandomQuote()
            onlineQuote?.let { quoteResponse ->
                MotivationalQuote(
                    quote = quoteResponse.quote,
                    author = quoteResponse.author,
                    category = when (quoteResponse.category.lowercase()) {
                        "motivation", "motivational" -> QuoteCategory.SELF_IMPROVEMENT
                        "success" -> QuoteCategory.GOAL_SETTING
                        "wisdom" -> QuoteCategory.MINDFULNESS
                        "productivity" -> QuoteCategory.PRODUCTIVITY
                        "digital wellness", "technology" -> QuoteCategory.DIGITAL_WELLNESS
                        else -> QuoteCategory.SELF_IMPROVEMENT
                    }
                )
            } ?: run {
                // Fallback to local database
                getRandomQuote()
            }
        } catch (e: Exception) {
            println("Failed to get daily quote: ${e.message}")
            getRandomQuote()
        }
    }
} 