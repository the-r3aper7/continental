package com.t27.continental.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.navigation.NavHostController
import com.t27.continental.R
import com.t27.continental.ui.components.AppBar

@Composable
fun AppScaffold(
    currentScreen: Routes,
    navController: NavHostController,
) {
    Scaffold(
        floatingActionButton = {
            if (currentScreen.route == Routes.Home.route) {
                FloatingActionButton(onClick = {
                    navController.navigate(Routes.AddItem.route)
                }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add item icon")
                }
            }
        },
        topBar = {
            AppBar(
                navController = navController,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                currentScreen = currentScreen
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavGraph(
            innerPadding = PaddingValues(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding(),
                start = dimensionResource(id = R.dimen.padding_medium),
                end = dimensionResource(id = R.dimen.padding_medium)
            ),
            navController = navController,
            modifier = Modifier
                .fillMaxSize()
        )
    }
}
