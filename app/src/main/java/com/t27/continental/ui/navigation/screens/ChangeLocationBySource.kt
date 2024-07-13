package com.t27.continental.ui.navigation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.t27.continental.data.models.Location
import com.t27.continental.data.models.SearchSource
import com.t27.continental.data.view_models.AppViewModelProvider
import com.t27.continental.data.view_models.LocationViewModel
import com.t27.continental.data.view_models.SearchLocationsState
import com.t27.continental.ui.components.SearchField
import kotlinx.coroutines.launch

@Composable
fun ChangeLocationBySource(
    modifier: Modifier = Modifier,
    navController: NavController,
    locationModel: LocationViewModel = viewModel(factory = AppViewModelProvider.Factory),
    source: String,
) {
    SearchLocation(
        navController = navController,
        modifier = modifier,
        locationModel = locationModel,
        source = source
    )
}

@Composable
fun SearchLocation(
    modifier: Modifier = Modifier,
    navController: NavController,
    source: String,
    locationModel: LocationViewModel
) {
    val locationQuery by locationModel.locationQuery.collectAsState()
    val searchLocationState by locationModel.searchLocationState.collectAsState()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SearchField(
            value = locationQuery,
            label = "Set location for $source",
            leadingIcon = { Icon(Icons.Filled.LocationOn, contentDescription = "location pin") },
            onValueChange = {
                locationModel.updateLocationQuery(it, source)
            }
        )
        when (searchLocationState) {
            is SearchLocationsState.Initial -> StateInitial(
                text = "Search for location",
                modifier.fillMaxSize()
            )

            is SearchLocationsState.Loading -> StateLoading(modifier.fillMaxSize())
            is SearchLocationsState.Success -> LocationSuggestions(
                source = source,
                viewModel = locationModel,
                locations = (searchLocationState as SearchLocationsState.Success).data.locations,
                navigateUp = { navController.navigateUp() }
            )

            is SearchLocationsState.Error -> Text(text = "error")
        }
    }
}

@Composable
fun LocationSuggestions(
    source: String,
    viewModel: LocationViewModel,
    locations: List<Location>,
    navigateUp: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val location = viewModel.getLocation(SearchSource.fromString(source) ?: SearchSource.Blinkit)
        .collectAsState()
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(locations) {
            ListItem(
                leadingContent = {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = "location pin"
                    )
                },
                headlineContent = {
                    Text(
                        text = it.mainText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                supportingContent = {
                    Text(
                        text = it.description,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .clickable {
                        coroutineScope.launch {
                            viewModel.insertLocation(it, location.value)
                            navigateUp()
                        }
                    },
                tonalElevation = 4.dp,
            )
        }
    }
}