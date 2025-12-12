package com.example.moneytracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.moneytracker.navigation.AppNavigation
import com.example.moneytracker.ui.theme.MoneyTrackerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoneyTrackerTheme {
                val navController = rememberNavController()
                
                // Replace with your actual Web Client ID from Firebase Console
                // Go to: Firebase Console > Project Settings > Your apps > Web app > Web client ID
                val webClientId = getString(R.string.default_web_client_id)
                
                AppNavigation(
                    navController = navController,
                    webClientId = webClientId
                )
            }
        }
    }
}