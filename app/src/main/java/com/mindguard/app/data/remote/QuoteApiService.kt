package com.mindguard.app.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

data class QuoteResponse(
    val quote: String, val author: String, val category: String = "motivation"
)

object QuoteApiService {

    private val quoteApis = listOf(
        "https://api.quotable.io/random?tags=motivational,success,wisdom,inspirational",
        "https://zenquotes.io/api/random",
        "https://api.quotable.io/random?minLength=50&maxLength=200"
    )

    suspend fun fetchRandomQuote(): QuoteResponse? = withContext(Dispatchers.IO) {
        for (apiUrl in quoteApis) {
            try {
                when {
                    apiUrl.contains("quotable.io") -> {
                        return@withContext fetchFromQuotable(apiUrl)
                    }

                    apiUrl.contains("zenquotes.io") -> {
                        return@withContext fetchFromZenQuotes(apiUrl)
                    }
                }
            } catch (e: Exception) {
                println("Failed to fetch from $apiUrl: ${e.message}")
                continue
            }
        }

        getOfflineQuote()
    }

    suspend fun fetchMultipleQuotes(count: Int = 10): List<QuoteResponse> =
        withContext(Dispatchers.IO) {
            val quotes = mutableListOf<QuoteResponse>()

            try {
                // Try to fetch from quotable.io with multiple quotes
                val url =
                    "https://api.quotable.io/quotes?tags=motivational,success,wisdom,inspirational&limit=$count"
                val response = makeHttpRequest(url)
                val jsonObject = JSONObject(response)
                val results = jsonObject.getJSONArray("results")

                for (i in 0 until results.length()) {
                    val quote = results.getJSONObject(i)
                    quotes.add(
                        QuoteResponse(
                            quote = quote.getString("content"),
                            author = quote.getString("author"),
                            category = quote.optString("tags", "motivation")
                        )
                    )
                }
            } catch (e: Exception) {
                println("Failed to fetch multiple quotes: ${e.message}")
                // Fallback to offline quotes
                quotes.addAll(getOfflineQuotes())
            }

            if (quotes.isEmpty()) {
                quotes.addAll(getOfflineQuotes())
            }

            quotes
        }

    private fun fetchFromQuotable(apiUrl: String): QuoteResponse? {
        return try {
            val response = makeHttpRequest(apiUrl)
            val jsonObject = JSONObject(response)

            QuoteResponse(
                quote = jsonObject.getString("content"),
                author = jsonObject.getString("author"),
                category = jsonObject.optString("tags", "motivation")
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun fetchFromZenQuotes(apiUrl: String): QuoteResponse? {
        return try {
            val response = makeHttpRequest(apiUrl)
            val jsonArray = JSONArray(response)
            val quote = jsonArray.getJSONObject(0)

            QuoteResponse(
                quote = quote.getString("q"), author = quote.getString("a"), category = "motivation"
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun makeHttpRequest(urlString: String): String {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            connection.setRequestProperty("User-Agent", "MindGuard-App/1.0")

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()

                return response.toString()
            } else {
                throw Exception("HTTP Error: $responseCode")
            }
        } finally {
            connection.disconnect()
        }
    }

    private fun getOfflineQuote(): QuoteResponse {
        val offlineQuotes = getOfflineQuotes()
        return offlineQuotes.random()
    }

    private fun getOfflineQuotes(): List<QuoteResponse> {
        return listOf(
            QuoteResponse(
                "The only way to do great work is to love what you do.", "Steve Jobs", "success"
            ), QuoteResponse(
                "Success is not final, failure is not fatal: it is the courage to continue that counts.",
                "Winston Churchill",
                "motivation"
            ), QuoteResponse(
                "The future belongs to those who believe in the beauty of their dreams.",
                "Eleanor Roosevelt",
                "inspiration"
            ), QuoteResponse(
                "It is during our darkest moments that we must focus to see the light.",
                "Aristotle",
                "wisdom"
            ), QuoteResponse(
                "The way to get started is to quit talking and begin doing.",
                "Walt Disney",
                "action"
            ), QuoteResponse(
                "Don't let yesterday take up too much of today.", "Will Rogers", "mindfulness"
            ), QuoteResponse(
                "You learn more from failure than from success. Don't let it stop you. Failure builds character.",
                "Unknown",
                "resilience"
            ), QuoteResponse(
                "If you are working on something that you really care about, you don't have to be pushed. The vision pulls you.",
                "Steve Jobs",
                "passion"
            ), QuoteResponse(
                "Believe you can and you're halfway there.", "Theodore Roosevelt", "confidence"
            ), QuoteResponse(
                "The only impossible journey is the one you never begin.", "Tony Robbins", "action"
            ), QuoteResponse(
                "In the middle of difficulty lies opportunity.", "Albert Einstein", "opportunity"
            ), QuoteResponse(
                "Digital wellness is about creating intentional relationships with technology.",
                "Digital Wellness Institute",
                "digital wellness"
            ), QuoteResponse(
                "Technology should amplify human capability, not replace human connection.",
                "Reid Hoffman",
                "technology"
            ), QuoteResponse(
                "The goal is not to eliminate technology, but to use it more mindfully.",
                "Cal Newport",
                "mindful technology"
            ), QuoteResponse(
                "Time spent on screens should be time well spent.",
                "Common Sense Media",
                "screen time"
            )
        )
    }
}