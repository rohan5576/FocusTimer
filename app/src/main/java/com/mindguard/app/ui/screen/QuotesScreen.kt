package com.mindguard.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mindguard.app.MindGuardApplication
import com.mindguard.app.data.model.QuoteCategory
import com.mindguard.app.ui.viewmodel.QuotesViewModel
import com.mindguard.app.ui.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotesScreen(
    viewModel: QuotesViewModel = viewModel(
        factory = ViewModelFactory(
            (LocalContext.current.applicationContext as MindGuardApplication).appUsageRepository,
            (LocalContext.current.applicationContext as MindGuardApplication).motivationalQuoteRepository,
            (LocalContext.current.applicationContext as MindGuardApplication).dailyStatisticsRepository,
            LocalContext.current
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                QuotesHeader(onRefresh = { viewModel.refreshQuotes() })
            }

            // Daily Quote Section
            uiState.dailyQuote?.let { dailyQuote ->
                item {
                    DailyQuoteCard(
                        quote = dailyQuote, onShare = { viewModel.shareQuote(dailyQuote) })
                }
            }

            item {
                SearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = { viewModel.updateSearchQuery(it) },
                    onClearSearch = { viewModel.clearSearch() })
            }

            item {
                CategoryFilter(
                    categories = uiState.categories,
                    selectedCategory = uiState.selectedCategory,
                    onCategorySelected = { viewModel.selectCategory(it) })
            }

            item {
                QuotesSectionHeader(
                    selectedCategory = uiState.selectedCategory,
                    quotesCount = uiState.filteredQuotes.size
                )
            }

            if (uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else if (uiState.filteredQuotes.isEmpty()) {
                item {
                    EmptyQuotesState(
                        hasSearchQuery = uiState.searchQuery.isNotBlank(),
                        selectedCategory = uiState.selectedCategory,
                        onClearFilters = {
                            viewModel.clearSearch()
                            viewModel.selectCategory(null)
                        })
                }
            } else {
                items(uiState.filteredQuotes) { quote ->
                    ModernQuoteCard(
                        quote = quote, onShare = { viewModel.shareQuote(quote) })
                }
            }
        }
    }
}

@Composable
fun QuotesHeader(onRefresh: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Daily Inspiration",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Find motivation and wisdom for your digital wellness journey",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        IconButton(onClick = onRefresh) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Refresh quotes",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun DailyQuoteCard(
    quote: com.mindguard.app.data.model.MotivationalQuote, onShare: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f)
                        ), radius = 800f
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Quote of the Day",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "\"${quote.quote}\"",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontStyle = FontStyle.Italic
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "— ${quote.author}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }

                    IconButton(onClick = onShare) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share quote",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                CategoryChip(category = quote.category)
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String, onQueryChange: (String) -> Unit, onClearSearch: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Search quotes or authors") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null)
        },
        trailingIcon = {
            if (query.isNotBlank()) {
                IconButton(onClick = onClearSearch) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear search")
                }
            }
        },
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        singleLine = true
    )
}

@Composable
fun CategoryFilter(
    categories: List<QuoteCategory>,
    selectedCategory: QuoteCategory?,
    onCategorySelected: (QuoteCategory?) -> Unit
) {
    Column {
        Text(
            text = "Categories",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    onClick = { onCategorySelected(null) },
                    label = { Text("All") },
                    selected = selectedCategory == null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }

            items(categories) { category ->
                FilterChip(
                    onClick = { onCategorySelected(category) },
                    label = {
                        Text(
                            formatCategoryName(category), maxLines = 1
                        )
                    },
                    selected = selectedCategory == category,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }
    }
}

@Composable
fun QuotesSectionHeader(
    selectedCategory: QuoteCategory?, quotesCount: Int
) {
    val title = if (selectedCategory != null) {
        "${formatCategoryName(selectedCategory)} Quotes"
    } else {
        "All Quotes"
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "$quotesCount quotes",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ModernQuoteCard(
    quote: com.mindguard.app.data.model.MotivationalQuote, onShare: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "\"${quote.quote}\"",
                style = MaterialTheme.typography.bodyLarge,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "— ${quote.author}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    CategoryChip(category = quote.category)
                }

                IconButton(onClick = onShare) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share quote",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryChip(category: QuoteCategory) {
    val categoryColor = getCategoryColor(category)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(categoryColor.copy(alpha = 0.1f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = formatCategoryName(category),
            style = MaterialTheme.typography.labelMedium,
            color = categoryColor,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun EmptyQuotesState(
    hasSearchQuery: Boolean, selectedCategory: QuoteCategory?, onClearFilters: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ), contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.FormatQuote,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = if (hasSearchQuery || selectedCategory != null) "No matching quotes" else "No quotes found",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (hasSearchQuery || selectedCategory != null) {
                "Try adjusting your search or filters"
            } else {
                "Check back later for inspiring quotes"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        if (hasSearchQuery || selectedCategory != null) {
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedButton(
                onClick = onClearFilters, shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Clear Filters")
            }
        }
    }
}

private fun formatCategoryName(category: QuoteCategory): String {
    return category.name.lowercase().split('_').joinToString(" ") { word ->
        word.replaceFirstChar { it.uppercase() }
    }
}

private fun getCategoryColor(category: QuoteCategory): Color {
    return when (category) {
        QuoteCategory.PRODUCTIVITY -> Color(0xFF2196F3)
        QuoteCategory.MINDFULNESS -> Color(0xFF4CAF50)
        QuoteCategory.GOAL_SETTING -> Color(0xFFFF9800)
        QuoteCategory.SELF_IMPROVEMENT -> Color(0xFF9C27B0)
        QuoteCategory.DIGITAL_WELLNESS -> Color(0xFF00BCD4)
    }
} 