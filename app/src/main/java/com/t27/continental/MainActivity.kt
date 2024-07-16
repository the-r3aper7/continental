package com.t27.continental

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.t27.continental.ui.navigation.AppScaffold
import com.t27.continental.ui.navigation.Routes
import com.t27.continental.ui.theme.ContinentalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = 0,
                darkScrim = 0
            )
        )
        setContent {
            ContinentalTheme {
                ContinentalApp()
            }
        }
    }
}

@Composable
fun ContinentalApp(
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = when (backStackEntry?.destination?.route) {
        Routes.Home.route -> Routes.Home
        Routes.Settings.route -> Routes.Settings
        Routes.ChangeLocation.route -> Routes.ChangeLocation
        Routes.AddItem.route -> Routes.AddItem
        else -> {
            if (backStackEntry?.destination?.route?.startsWith(
                    Routes.ChangeLocation.route.substringBefore(
                        "?"
                    )
                ) == true
            ) {
                Routes.ChangeLocation
            } else {
                Routes.Home
            }
        }
    }

    AppScaffold(
        currentScreen = currentScreen,
        navController = navController,
    )
}