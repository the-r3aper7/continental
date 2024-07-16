package com.t27.continental.ui.navigation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.t27.continental.data.models.Location
import com.t27.continental.data.models.Product
import com.t27.continental.data.models.SearchSource
import com.t27.continental.data.view_models.AppViewModelProvider
import com.t27.continental.data.view_models.LocationViewModel
import com.t27.continental.data.view_models.SearchProductsState
import com.t27.continental.data.view_models.SearchProductsViewModel
import com.t27.continental.data.view_models.ShoppingListViewModel
import com.t27.continental.ui.components.SearchField
import com.t27.continental.ui.components.SearchProductCard

@Composable
fun AddItem(
    modifier: Modifier = Modifier,
    navController: NavController,
    shoppingListModel: ShoppingListViewModel = viewModel(factory = AppViewModelProvider.Factory),
    viewModel: SearchProductsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    locationModel: LocationViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchProductState by viewModel.searchProductState.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    val source by viewModel.source.collectAsState()
    val location by locationModel.getLocation(source).collectAsState()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SearchField(
            leadingIcon = {
                location?.let {
                    ProductSourceIcon(
                        source = source,
                        value = searchQuery,
                        searchProductsViewModel = viewModel,
                        expanded = expanded,
                        location = it,
                        onExpandedChange = { expanded = it }
                    )
                }
            },
            value = searchQuery,
            onValueChange = {
                location?.let { location ->
                    viewModel.updateSearchQuery(
                        it,
                        location
                    )
                }
            },
            label = "Search via ${source.name.lowercase()}",
            supportingText = "The default product source would be ${source.name.lowercase()}"
        )
        when (searchProductState) {
            is SearchProductsState.Initial -> StateInitial(
                "Search for products",
                modifier.fillMaxSize()
            )

            is SearchProductsState.Loading -> StateLoading(modifier.fillMaxSize())
            is SearchProductsState.Success ->
                ProductSuggestions(
                    location,
                    source,
                    (searchProductState as SearchProductsState.Success).data?.productData
                        ?: listOf(),
                    shoppingListModel,
                )

            is SearchProductsState.Error -> Text(text = "Error")
        }
    }
}

@Composable
fun Filters(modifier: Modifier = Modifier) {
    LazyRow(
        modifier = Modifier.fillMaxWidth()
    ) {
        item {
            FilterChip(
                selected = true,
                onClick = { /*TODO*/ },
                label = { Text(text = "Low to High") })
        }
    }
}

@Composable
fun ProductSuggestions(
    location: Location?,
    source: SearchSource,
    products: List<Product>,
    shoppingListModel: ShoppingListViewModel,
) {
    Filters()
    LazyVerticalGrid(
        columns = GridCells.Adaptive(160.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(products) {
            var isAdded by remember {
                mutableStateOf(false)
            }
            val product = shoppingListModel.getProduct(it).collectAsState(initial = Product)

            SearchProductCard(
                product = it,
                isAdded = product.value != null,
                showSource = false,
                onAddToList = {
                    shoppingListModel.fetchSimilarItems(source, it, location ?: Location())
                    isAdded = true
                }
            )

        }
    }
}

@Composable
fun StateInitial(text: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            maxLines = 2,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun StateLoading(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ProductSourceIcon(
    location: Location,
    source: SearchSource,
    value: String,
    searchProductsViewModel: SearchProductsViewModel,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(8.dp)
            .clickable { onExpandedChange(true) }
    ) {
        Image(
            painter = painterResource(source.icon),
            contentDescription = "${source.name} icon",
            modifier = Modifier
                .width(38.dp)
                .clip(MaterialTheme.shapes.medium)
        )
        if (expanded) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "right",
                modifier = Modifier.width(32.dp)
            )
        } else {
            Icon(
                Icons.Filled.KeyboardArrowDown,
                contentDescription = "down",
                modifier = Modifier.width(32.dp)
            )

        }
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onExpandedChange(false) }
    ) {
        SearchSource.entries.toTypedArray().forEach {
            DropdownMenuItem(
                leadingIcon = {
                    Image(
                        painter = painterResource(it.icon),
                        contentDescription = "${it.name.lowercase()} icon",
                        modifier = Modifier
                            .width(32.dp)
                            .clip(MaterialTheme.shapes.medium)
                    )
                },
                text = { Text(it.title) },
                onClick = {
                    searchProductsViewModel.setSource(it)
                    searchProductsViewModel.updateSearchQuery(value, location)
                    onExpandedChange(false)
                }
            )

        }
    }
}