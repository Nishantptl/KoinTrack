package com.nishant.kointrack.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nishant.kointrack.ui.add_transaction.AddTransactionScreen
import com.nishant.kointrack.ui.add_transaction.AddTransactionViewModel
import com.nishant.kointrack.ui.home.HomeScreen
import com.nishant.kointrack.ui.home.HomeViewModel

@Composable
fun KoinTrackNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            val homeViewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToAddTransaction = {
                    navController.navigate(Screen.AddTransaction.route)
                }
            )
        }

        composable(Screen.AddTransaction.route) {
            val addTransactionViewModel: AddTransactionViewModel = hiltViewModel()
            AddTransactionScreen(
                viewModel = addTransactionViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
