package com.nishant.kointrack.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Dashboard : BottomNavItem(
        route = "dashboard",
        title = "Dashboard",
        icon = Icons.Default.Star
    )

    object Expense : BottomNavItem(
        route = "add_expense",
        title = "Add Expense",
        icon = Icons.Default.AddCircle
    )

    object History : BottomNavItem(
        route = "history",
        title = "History",
        icon = Icons.AutoMirrored.Filled.List
    )
}
