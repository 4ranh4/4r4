package com.app4r4.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app4r4.ui.onboarding.OnboardingScreen
import com.app4r4.ui.onboarding.OnboardingViewModel
import com.app4r4.ui.dashboard.DashboardScreen
import com.app4r4.ui.calculator.CalculatorScreen
import com.app4r4.ui.tips.TipsScreen
import com.app4r4.ui.history.HistoryScreen

@Composable
fun App4R4NavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Onboarding.route
    ) {
        composable(Screen.Onboarding.route) {
            val viewModel: OnboardingViewModel = hiltViewModel()
            val isCompleted by viewModel.onboardingCompleted.collectAsState()
            if (isCompleted) {
                navController.navigate(Screen.Dashboard.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            } else {
                OnboardingScreen(
                    onFinish = { transport, diet ->
                        viewModel.completeOnboarding(transport, diet)
                    }
                )
            }
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToCalculator = { navController.navigate(Screen.Calculator.route) },
                onNavigateToTips = { navController.navigate(Screen.Tips.route) },
                onNavigateToHistory = { navController.navigate(Screen.History.route) }
            )
        }

        composable(Screen.Calculator.route) {
            CalculatorScreen(
                onBack = { navController.popBackStack() },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Tips.route) {
            TipsScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.History.route) {
            HistoryScreen(onBack = { navController.popBackStack() })
        }
    }
}
