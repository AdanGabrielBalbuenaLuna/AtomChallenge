package com.example.atomchallenge.feature.countries.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.atomchallenge.feature.countries.presentation.detail.DetailScreen
import com.example.atomchallenge.feature.countries.presentation.home.HomeScreen

object AppRoutes {
    const val HOME = "presentation/detail/home"
    const val DETAIL = "presentation/detail/{countryName}"

    fun detailRoute(countryName: String) = "detail/$countryName"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.HOME
    ) {
        composable(route = AppRoutes.HOME) {
            HomeScreen(
                onCountryClick = { countryName ->
                    navController.navigate(AppRoutes.detailRoute(countryName))
                }
            )
        }

        composable(
            route = AppRoutes.DETAIL,
            arguments = listOf(
                navArgument("countryName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val countryName = backStackEntry.arguments
                ?.getString("countryName") ?: ""

            DetailScreen(
                countryName = countryName,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}