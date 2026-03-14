package com.app4r4.ui.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Dashboard : Screen("dashboard")
    object Calculator : Screen("calculator")
    object Tips : Screen("tips")
    object History : Screen("history")
}
