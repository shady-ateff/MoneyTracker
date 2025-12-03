package com.example.moneytracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.moneytracker.ui.ExpenseScreen
import com.example.moneytracker.ui.theme.MoneyTrackerTheme
import com.example.moneytracker.viewmodel.ExpenseViewModel
import io.flutter.embedding.android.FlutterActivity

class MainActivity : ComponentActivity() {
    private val viewModel: ExpenseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoneyTrackerTheme {
                ExpenseScreen(
                    viewModel = viewModel,
                    onVisualizeClick = {
                        // Integration Snippet: Launch Flutter Activity
                        startActivity(
                            FlutterActivity.createDefaultIntent(this)
                        )
                    }
                )
            }
        }
    }
}