package com.example.calculator.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.calculator.ui.navigation.Destinations
import com.example.calculator.ui.screens.CalculatorScreen
import com.example.calculator.ui.screens.HistoryScreen
import com.example.calculator.viewmodel.CalculatorViewModel

@Composable
fun CalculatorApp(viewModel: CalculatorViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Destinations.CALCULATOR) {
        composable(Destinations.CALCULATOR) {
            CalculatorScreen(
                uiState = uiState,
                onAction = viewModel::onAction,
                onOpenHistory = { navController.navigate(Destinations.HISTORY) },
            )
        }
        composable(Destinations.HISTORY) {
            HistoryScreen(
                history = uiState.history,
                onBack = { navController.navigateUp() },
            )
        }
    }
}
