package com.mindguard.app.ui.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindguard.app.data.model.MotivationalQuote
import com.mindguard.app.data.model.QuoteCategory
import com.mindguard.app.data.repository.MotivationalQuoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class QuotesUiState(
    val allQuotes: List<MotivationalQuote> = emptyList(),
    val filteredQuotes: List<MotivationalQuote> = emptyList(),
    val selectedCategory: QuoteCategory? = null,
    val searchQuery: String = "",
    val categories: List<QuoteCategory> = QuoteCategory.values().toList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val dailyQuote: MotivationalQuote? = null
)

class QuotesViewModel(
    private val motivationalQuoteRepository: MotivationalQuoteRepository,
    private val context: Context
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(QuotesUiState())
    val uiState: StateFlow<QuotesUiState> = _uiState.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow<QuoteCategory?>(null)
    
    init {
        loadQuotes()
        loadDailyQuote()
        setupFiltering()
    }
    
    private fun setupFiltering() {
        viewModelScope.launch {
            combine(
                _uiState,
                _searchQuery,
                _selectedCategory
            ) { state, query, category ->
                val filtered = filterQuotes(state.allQuotes, query, category)
                state.copy(
                    filteredQuotes = filtered,
                    searchQuery = query,
                    selectedCategory = category
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }
    
    private fun filterQuotes(
        quotes: List<MotivationalQuote>,
        searchQuery: String,
        category: QuoteCategory?
    ): List<MotivationalQuote> {
        return quotes.filter { quote ->
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                quote.quote.contains(searchQuery, ignoreCase = true) ||
                quote.author.contains(searchQuery, ignoreCase = true)
            }
            
            val matchesCategory = category == null || quote.category == category
            
            matchesSearch && matchesCategory
        }
    }
    
    private fun loadQuotes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                motivationalQuoteRepository.getAllActiveQuotes().collect { quotes ->
                    val filtered = filterQuotes(quotes, _searchQuery.value, _selectedCategory.value)
                    _uiState.value = _uiState.value.copy(
                        allQuotes = quotes,
                        filteredQuotes = filtered,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }
    
    private fun loadDailyQuote() {
        viewModelScope.launch {
            try {
                val dailyQuote = motivationalQuoteRepository.getDailyQuote()
                _uiState.value = _uiState.value.copy(dailyQuote = dailyQuote)
            } catch (e: Exception) {
                // Daily quote is optional, don't show error for this
            }
        }
    }
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun selectCategory(category: QuoteCategory?) {
        _selectedCategory.value = category
    }
    
    fun shareQuote(quote: MotivationalQuote) {
        val shareText = "\"${quote.quote}\" - ${quote.author}"
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        
        val chooser = Intent.createChooser(intent, "Share Quote")
        chooser.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(chooser)
    }
    
    fun refreshQuotes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // Try to refresh from online sources
                motivationalQuoteRepository.refreshQuotesFromOnline()
                loadQuotes()
                loadDailyQuote()
            } catch (e: Exception) {
                // If online refresh fails, just reload existing quotes
                loadQuotes()
                loadDailyQuote()
            }
        }
    }
    
    fun clearSearch() {
        _searchQuery.value = ""
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
} 