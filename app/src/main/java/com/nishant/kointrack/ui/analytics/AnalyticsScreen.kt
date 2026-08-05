package com.nishant.kointrack.ui.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nishant.kointrack.domain.model.AnalyticsData
import com.nishant.kointrack.domain.model.CategoryDistribution
import com.nishant.kointrack.domain.model.MonthlyTrend
import com.nishant.kointrack.domain.model.TopCategory
import com.nishant.kointrack.domain.model.TransactionCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics & Insights", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is AnalyticsUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is AnalyticsUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                is AnalyticsUiState.Success -> {
                    AnalyticsContent(data = state.data)
                }
            }
        }
    }
}

@Composable
private fun AnalyticsContent(data: AnalyticsData) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Top Spending Categories Card
        if (data.topCategories.isNotEmpty()) {
            TopCategoriesCard(topCategories = data.topCategories)
        }

        // 2. Category Distribution Donut Chart
        CategoryDonutChartCard(
            distributions = data.categoryDistributions,
            totalExpenseEUR = data.totalExpenseEUR
        )

        // 3. 6-Month Expense Trend Bar Chart
        MonthlyTrendBarChartCard(trends = data.monthlyTrends)
    }
}

@Composable
fun TopCategoriesCard(topCategories: List<TopCategory>) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Top Categories",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Top Spending Categories",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            topCategories.forEachIndexed { index, topCat ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(getCategoryColor(topCat.category)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "#${index + 1}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = topCat.category.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "€%.2f".format(topCat.totalEUR),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "%.1f%%".format(topCat.percentage),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryDonutChartCard(
    distributions: List<CategoryDistribution>,
    totalExpenseEUR: Double
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Category Distribution",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (distributions.isEmpty()) {
                Text(
                    text = "No expense data available for charts.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(180.dp)) {
                        var startAngle = -90f
                        val strokeWidth = 36.dp.toPx()

                        distributions.forEach { dist ->
                            val sweepAngle = dist.percentage * 3.6f
                            drawArc(
                                color = getCategoryColor(dist.category),
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                style = Stroke(width = strokeWidth)
                            )
                            startAngle += sweepAngle
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Total Spent",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "€%.0f".format(totalExpenseEUR),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Chart Legend
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    distributions.forEach { dist ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(getCategoryColor(dist.category))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = dist.category.name,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Text(
                                text = "€%.2f (%.1f%%)".format(dist.totalEUR, dist.percentage),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MonthlyTrendBarChartCard(trends: List<MonthlyTrend>) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "6-Month Expense Trend",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            val maxVal = (trends.maxOfOrNull { it.totalEUR } ?: 1.0).coerceAtLeast(1.0)
            val primaryColor = MaterialTheme.colorScheme.primary
            val gridColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)

            Column {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                ) {
                    val barWidth = 24.dp.toPx()
                    val totalBars = trends.size
                    val spaceBetween = (size.width - (barWidth * totalBars)) / (totalBars + 1)
                    val maxHeight = size.height - 20.dp.toPx()

                    // Draw reference grid line
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, size.height - 5.dp.toPx()),
                        end = Offset(size.width, size.height - 5.dp.toPx()),
                        strokeWidth = 2f
                    )

                    trends.forEachIndexed { index, trend ->
                        val barHeight = (trend.totalEUR / maxVal * maxHeight).toFloat()
                        val x = spaceBetween + index * (barWidth + spaceBetween)
                        val y = size.height - 5.dp.toPx() - barHeight

                        drawRoundRect(
                            color = primaryColor,
                            topLeft = Offset(x, y),
                            size = Size(barWidth, barHeight.coerceAtLeast(4f)),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Bar X-Axis Labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    trends.forEach { trend ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = trend.monthLabel,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "€%.0f".format(trend.totalEUR),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

fun getCategoryColor(category: TransactionCategory): Color {
    return when (category) {
        TransactionCategory.FOOD -> Color(0xFFFF7043)
        TransactionCategory.TRANSPORT -> Color(0xFF42A5F5)
        TransactionCategory.HOUSING -> Color(0xFFAB47BC)
        TransactionCategory.ENTERTAINMENT -> Color(0xFFEC407A)
        TransactionCategory.SHOPPING -> Color(0xFFFFCA28)
        TransactionCategory.UTILITIES -> Color(0xFF26A69A)
        TransactionCategory.INCOME -> Color(0xFF66BB6A)
        TransactionCategory.OTHER -> Color(0xFF78909C)
    }
}

@Preview(showBackground = true)
@Composable
fun AnalyticsContentPreview() {
    val sampleData = AnalyticsData(
        categoryDistributions = listOf(
            CategoryDistribution(TransactionCategory.FOOD, 450.0, 45f),
            CategoryDistribution(TransactionCategory.HOUSING, 350.0, 35f),
            CategoryDistribution(TransactionCategory.ENTERTAINMENT, 200.0, 20f)
        ),
        monthlyTrends = listOf(
            MonthlyTrend("Mar", 800.0),
            MonthlyTrend("Apr", 950.0),
            MonthlyTrend("May", 1100.0),
            MonthlyTrend("Jun", 900.0),
            MonthlyTrend("Jul", 1050.0),
            MonthlyTrend("Aug", 1000.0)
        ),
        topCategories = listOf(
            TopCategory(TransactionCategory.FOOD, 450.0, 45f),
            TopCategory(TransactionCategory.HOUSING, 350.0, 35f),
            TopCategory(TransactionCategory.ENTERTAINMENT, 200.0, 20f)
        ),
        totalExpenseEUR = 1000.0
    )

    MaterialTheme {
        AnalyticsContent(data = sampleData)
    }
}
