package com.t27.continental.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.t27.continental.R
import com.t27.continental.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    currentScreen: Routes
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title), fontWeight = FontWeight.SemiBold) },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        },
        actions = {
            IconButton(
                onClick = { navController.navigate(Routes.ChangeLocation.route) },
                enabled = currentScreen != Routes.ChangeLocation
            ) {
                Icon(
                    Icons.Filled.LocationOn,
                    contentDescription = stringResource(id = R.string.settings)
                )
            }
            IconButton(
                onClick = { navController.navigate(Routes.Settings.route) },
                enabled = currentScreen != Routes.Settings
            ) {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = stringResource(id = R.string.settings)
                )
            }
        },
        modifier = modifier
    )
}