package com.nishant.kointrack.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nishant.kointrack.domain.model.Transaction
import com.nishant.kointrack.domain.model.TransactionType
import com.nishant.kointrack.ui.home.TransactionItem
import com.nishant.kointrack.util.CsvExporter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transaction History", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = {
                        if (uiState is HistoryUiState.Success) {
                            val transactions = (uiState as HistoryUiState.Success).transactions
                            CsvExporter.exportAndShareCsv(context, transactions)
                        }
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Export CSV")
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
                is HistoryUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is HistoryUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                is HistoryUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        OutlinedTextField(
                            value = state.filter.query,
                            onValueChange = { viewModel.updateQuery(it) },
                            placeholder = { Text("Search title or notes...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                            trailingIcon = {
                                if (state.filter.query.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.updateQuery("") }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear Search")
                                    }
                                }
                            },
                            shape = RoundedCornerShape(24.dp),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = state.filter.type == null,
                                onClick = { viewModel.updateType(null) },
                                label = { Text("All") },
                                shape = RoundedCornerShape(20.dp)
                            )
                            FilterChip(
                                selected = state.filter.type == TransactionType.INCOME,
                                onClick = { viewModel.updateType(if (state.filter.type == TransactionType.INCOME) null else TransactionType.INCOME) },
                                label = { Text("Income") },
                                shape = RoundedCornerShape(20.dp)
                            )
                            FilterChip(
                                selected = state.filter.type == TransactionType.EXPENSE,
                                onClick = { viewModel.updateType(if (state.filter.type == TransactionType.EXPENSE) null else TransactionType.EXPENSE) },
                                label = { Text("Expenses") },
                                shape = RoundedCornerShape(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (state.transactions.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No matching transactions found.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            val groupedTransactions = groupTransactionsByDate(state.transactions)

                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                groupedTransactions.forEach { (dateHeader, txList) ->
                                    item(key = dateHeader) {
                                        Text(
                                            text = dateHeader.uppercase(Locale.getDefault()),
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)
                                        )
                                    }

                                    items(
                                        items = txList,
                                        key = { tx -> tx.id }
                                    ) { transaction ->
                                        TransactionItem(
                                            transaction = transaction,
                                            onDelete = { viewModel.deleteTransaction(transaction) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun groupTransactionsByDate(transactions: List<Transaction>): Map<String, List<Transaction>> {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return transactions.groupBy { tx ->
        dateFormat.format(Date(tx.timestamp))
    }
}
