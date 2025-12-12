package com.example.moneytracker.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.moneytracker.ui.MainScreen
import com.example.moneytracker.ui.auth.LoginScreen
import com.example.moneytracker.ui.auth.SignUpScreen
import com.example.moneytracker.viewmodel.AuthViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    webClientId: String
) {
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.uiState.collectAsState()

    // Determine start destination based on auth state
    val startDestination = if (authState.isLoggedIn) Screen.Main.route else Screen.Login.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                uiState = authState,
                webClientId = webClientId,
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route)
                }
            )

            // Navigate to main when logged in
            if (authState.isLoggedIn) {
                navController.navigate(Screen.Main.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                authViewModel = authViewModel,
                uiState = authState,
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )

            // Navigate to main when logged in
            if (authState.isLoggedIn) {
                navController.navigate(Screen.Main.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
        }

        composable(Screen.Main.route) {
            MainScreen(
                onSignOut = {
                    authViewModel.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
