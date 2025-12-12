package com.example.moneytracker.ui

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneytracker.navigation.BottomNavItem
import com.example.moneytracker.ui.screens.BudgetsScreen
import com.example.moneytracker.ui.screens.ExpensesScreen
import com.example.moneytracker.ui.screens.GoalsScreen
import com.example.moneytracker.ui.screens.IncomesScreen
import com.example.moneytracker.viewmodel.MainViewModel
import io.flutter.embedding.android.FlutterActivity

data class BottomNavItemData(
    val item: BottomNavItem,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onSignOut: () -> Unit
) {
    val mainViewModel: MainViewModel = viewModel()
    val uiState by mainViewModel.uiState.collectAsState()
    val context = LocalContext.current

    var selectedTab by remember { mutableStateOf(0) }

    val navItems = listOf(
        BottomNavItemData(BottomNavItem.Budgets, Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet),
        BottomNavItemData(BottomNavItem.Expenses, Icons.Filled.Payments, Icons.Outlined.Payments),
        BottomNavItemData(BottomNavItem.Goals, Icons.Filled.Flag, Icons.Outlined.Flag),
        BottomNavItemData(BottomNavItem.Incomes, Icons.Filled.TrendingUp, Icons.Outlined.TrendingUp)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(navItems[selectedTab].item.title) },
                actions = {
                    // Flutter Visualization Button
                    IconButton(onClick = {
                        context.startActivity(
                            FlutterActivity.createDefaultIntent(context)
                        )
                    }) {
                        Icon(Icons.Default.BarChart, contentDescription = "Visualize")
                    }
                    TextButton(onClick = onSignOut) {
                        Text("Sign Out")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                navItems.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                if (selectedTab == index) navItem.selectedIcon else navItem.unselectedIcon,
                                contentDescription = navItem.item.title
                            )
                        },
                        label = { Text(navItem.item.title) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> BudgetsScreen(budgets = uiState.budgets, viewModel = mainViewModel)
                1 -> ExpensesScreen(expenses = uiState.expenses, budgets = uiState.budgets, viewModel = mainViewModel)
                2 -> GoalsScreen(goals = uiState.goals, viewModel = mainViewModel)
                3 -> IncomesScreen(incomes = uiState.incomes, viewModel = mainViewModel)
            }
        }
    }
}
