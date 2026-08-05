package com.nishant.kointrack.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nishant.kointrack.ui.add_transaction.AddTransactionScreen
import com.nishant.kointrack.ui.add_transaction.AddTransactionViewModel
import com.nishant.kointrack.ui.dashboard.DashboardScreen
import com.nishant.kointrack.ui.dashboard.DashboardViewModel
import com.nishant.kointrack.ui.history.HistoryScreen
import com.nishant.kointrack.ui.history.HistoryViewModel

@Composable
fun KoinTrackNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        composable(Screen.Dashboard.route) {
            val dashboardViewModel: DashboardViewModel = hiltViewModel()
            DashboardScreen(viewModel = dashboardViewModel)
        }

        composable(Screen.AddExpense.route) {
            val addTransactionViewModel: AddTransactionViewModel = hiltViewModel()
            AddTransactionScreen(
                viewModel = addTransactionViewModel,
                onTransactionAdded = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = false }
                    }
                }
            )
        }

        composable(Screen.History.route) {
            val historyViewModel: HistoryViewModel = hiltViewModel()
            HistoryScreen(viewModel = historyViewModel)
        }
    }
}
