package com.t27.continental.ui.navigation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.t27.continental.R
import com.t27.continental.data.view_models.AppViewModelProvider
import com.t27.continental.data.view_models.LocationViewModel
import com.t27.continental.data.view_models.ShoppingListViewModel
import com.t27.continental.ui.components.HomeProductCard
import com.t27.continental.ui.navigation.Routes

@Composable
fun Home(
    modifier: Modifier = Modifier,
    navController: NavController,
    shoppingListViewModel: ShoppingListViewModel = viewModel(factory = AppViewModelProvider.Factory),
    locationModel: LocationViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val shoppingListItems by shoppingListViewModel.shoppingListItems.collectAsState()
    val locations by locationModel.locationListState.collectAsState()

    LazyColumn(
        modifier = modifier,
        verticalArrangement = if (shoppingListItems.shoppingListItems.isEmpty()) Arrangement.Center else Arrangement.spacedBy(
            dimensionResource(id = R.dimen.padding_medium)
        )
    ) {
        if (locations.locationList.size <= 1) {
            item {
                NoLocation(
                    locations.locationList.size,
                    navigateToLocation = { navController.navigate(Routes.ChangeLocation.route) }
                )
            }
        } else if (shoppingListItems.shoppingListItems.isEmpty()) {
            item {
                EmptyScreen()
            }
        } else {
            items(shoppingListItems.shoppingListItems) { item ->
                val similarItems = shoppingListViewModel.getSimilarItems(
                    item
                ).collectAsState(initial = listOf())
                if (similarItems.value.isNotEmpty()) {
                    HomeProductCard(similarItem = similarItems.value, onRemove = {
                        shoppingListViewModel.deleteProducts(similarItems.value)
                    })
                }
            }
        }
    }
}

@Composable
fun EmptyScreen() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(id = R.string.empty_list),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}


@Composable
fun NoLocation(
    size: Int,
    navigateToLocation: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        when (size) {
            0 -> Text(
                text = stringResource(id = R.string.no_location_atleast2_helper_text),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.secondary
            )

            1 -> Text(
                text = stringResource(id = R.string.no_location_one_more_helper_text),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        Button(onClick = { navigateToLocation() }) {
            Text(text = "Your location")
        }
    }
}