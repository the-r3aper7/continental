package com.t27.continental.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.t27.continental.ui.navigation.screens.AddItem
import com.t27.continental.ui.navigation.screens.ChangeLocation
import com.t27.continental.ui.navigation.screens.ChangeLocationBySource
import com.t27.continental.ui.navigation.screens.Home
import com.t27.continental.ui.navigation.screens.Settings

@Composable
fun NavGraph(
    innerPadding: PaddingValues,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        modifier = modifier,
        startDestination = Routes.Home.route,
        navController = navController
    ) {
        composable(route = Routes.Home.route) {
            Home(
                navController = navController,
                modifier = modifier.padding(innerPadding),
            )
        }
        composable(route = Routes.Settings.route) {
            Settings(
                navController = navController,
                modifier = modifier.padding(innerPadding)
            )
        }
        composable(route = Routes.AddItem.route) {
            AddItem(
                navController = navController,
                modifier = modifier.padding(innerPadding),
            )
        }
        composable(
            route = Routes.ChangeLocation.route
        ) {
            ChangeLocation(modifier.padding(innerPadding), navController)
        }
        composable(
            route = Routes.ChangeLocationBySource.route,
            arguments = listOf(navArgument("source") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val source = backStackEntry.arguments?.getString("source") ?: ""
            ChangeLocationBySource(
                modifier.padding(innerPadding),
                navController = navController,
                source = source
            )
        }
    }
}