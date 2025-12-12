package com.example.moneytracker.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Main : Screen("main")
}

sealed class BottomNavItem(val route: String, val title: String, val icon: String) {
    object Budgets : BottomNavItem("budgets", "Budgets", "account_balance_wallet")
    object Expenses : BottomNavItem("expenses", "Expenses", "payments")
    object Goals : BottomNavItem("goals", "Goals", "flag")
    object Incomes : BottomNavItem("incomes", "Incomes", "trending_up")
}
