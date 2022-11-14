package com.aneonex.bitcoinchecker.tester.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aneonex.bitcoinchecker.tester.ui.features.markettest.MarketTestScreen

@Composable
fun MyAppNavHost() {
    val navigation = rememberNavController()

    NavHost(
        navController = navigation,
        startDestination = ScreenRoute.MarketTest
    ) {
/*
        composable(
            route = Screen.Splash.route
        ) {
            SplashScreen(
                navigationController = navigation
            )
        }
*/
        composable(
            route = ScreenRoute.MarketTest
        ) {
            MarketTestScreen()
        }
    }
}