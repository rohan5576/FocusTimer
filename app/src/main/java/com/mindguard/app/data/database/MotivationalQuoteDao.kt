package com.mindguard.app.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mindguard.app.data.model.MotivationalQuote
import com.mindguard.app.data.model.QuoteCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface MotivationalQuoteDao {

    @Query("SELECT * FROM motivational_quotes WHERE isActive = 1")
    fun getAllActiveQuotes(): Flow<List<MotivationalQuote>>

    @Query("SELECT * FROM motivational_quotes WHERE category = :category AND isActive = 1")
    fun getQuotesByCategory(category: QuoteCategory): Flow<List<MotivationalQuote>>

    @Query("SELECT * FROM motivational_quotes WHERE isActive = 1 ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomQuote(): MotivationalQuote?

    @Query("SELECT * FROM motivational_quotes WHERE category = :category AND isActive = 1 ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomQuoteByCategory(category: QuoteCategory): MotivationalQuote?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(quote: MotivationalQuote)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuotes(quotes: List<MotivationalQuote>)

    @Update
    suspend fun updateQuote(quote: MotivationalQuote)

    @Delete
    suspend fun deleteQuote(quote: MotivationalQuote)
} 