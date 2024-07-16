package com.t27.continental.ui.navigation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
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
                    shoppingListModel = shoppingListModel,
                    navigateBack = { navController.navigateUp() }
                )

            is SearchProductsState.Error -> Text(text = "Error")
        }
    }
}

@Composable
fun AddItemFilters(
    modifier: Modifier = Modifier,
    onClickLowToHigh: () -> Unit,
    onClickHighToLow: () -> Unit,
    onClickCross: () -> Unit,
    onClickWeight: (weight: String) -> Unit,
    weights: List<Product>,
    brands: List<Product>,
    onClickBrand: (brand: String) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            var expanded by remember {
                mutableStateOf(false)
            }
            var currBrand by remember {
                mutableStateOf("")
            }
            FilterChip(
                selected = currBrand.isNotEmpty(),
                onClick = { expanded = true },
                label = {
                    if (currBrand.isEmpty()) {
                        Text(text = "Brand")
                    } else {
                        Text(text = currBrand)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.heightIn(max = 186.dp)
                    ) {
                        brands.forEach {
                            DropdownMenuItem(
                                text = { Text(text = it.brand) },
                                onClick = {
                                    currBrand = it.brand
                                    onClickBrand(currBrand)
                                    expanded = false
                                }
                            )
                        }
                    }
                },
                trailingIcon = {
                    if (currBrand.isNotEmpty()) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "close",
                            modifier = Modifier
                                .clickable {
                                    currBrand = ""
                                    onClickBrand(currBrand)
                                }
                        )
                    }
                }
            )
        }
        item {
            var priceFilters by remember { mutableStateOf<PriceFilters?>(null) }
            FilterChip(
                selected = priceFilters != null,
                onClick = {
                    when (priceFilters) {
                        null, PriceFilters.HighToLow -> {
                            onClickLowToHigh()
                            priceFilters = PriceFilters.LowToHigh
                        }

                        PriceFilters.LowToHigh -> {
                            onClickHighToLow()
                            priceFilters = PriceFilters.HighToLow
                        }
                    }
                },
                trailingIcon = {
                    if (priceFilters != null) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "close icon",
                            modifier = Modifier
                                .clickable {
                                    onClickCross()
                                    priceFilters = null
                                }
                                .width(24.dp)
                        )
                    }
                },
                label = {
                    Text(text = priceFilters?.title ?: "Low to High")
                }
            )
        }
        item {
            var expanded by remember {
                mutableStateOf(false)
            }
            var currWeight by remember {
                mutableStateOf("")
            }
            FilterChip(
                selected = currWeight.isNotEmpty(),
                onClick = { expanded = true },
                label = {
                    if (currWeight.isEmpty()) {
                        Text(text = "Quantity")
                    } else {
                        Text(text = currWeight)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.heightIn(max = 186.dp)
                    ) {
                        weights.forEach {
                            DropdownMenuItem(
                                text = { Text(text = it.weight) },
                                onClick = {
                                    currWeight = it.weight
                                    onClickWeight(currWeight)
                                    expanded = false
                                }
                            )
                        }
                    }
                },
                trailingIcon = {
                    if (currWeight.isNotEmpty()) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "close",
                            modifier = Modifier
                                .clickable {
                                    currWeight = ""
                                    onClickWeight(currWeight)
                                }
                        )
                    }
                }
            )
        }
    }
}

enum class PriceFilters(val title: String) {
    LowToHigh("Low to High"),
    HighToLow("High to Low")
}

@Composable
fun ProductSuggestions(
    location: Location?,
    source: SearchSource,
    products: List<Product>,
    navigateBack: () -> Unit,
    shoppingListModel: ShoppingListViewModel,
) {
    var productsState by remember {
        mutableStateOf(products)
    }

    var weightState by remember {
        mutableStateOf(products.distinctBy { it.weight })
    }

    var brandState by remember {
        mutableStateOf(products.distinctBy { it.brand })
    }

    data class FilterState(
        var brandFilter: String = "",
        var priceSort: PriceFilters? = null,
        var weightFilter: String = "",
    )

    var filterState by remember { mutableStateOf(FilterState()) }

    fun applyFilters(currentProducts: List<Product>): List<Product> {
        var filteredProducts = currentProducts

        // Apply brand filter
        if (filterState.brandFilter.isNotEmpty()) {
            filteredProducts = filteredProducts.filter { it.brand == filterState.brandFilter }
            // Update weight options based on selected brand
            weightState = filteredProducts.distinctBy { it.weight }
        }

        // Apply weight filter
        if (filterState.weightFilter.isNotEmpty()) {
            filteredProducts = filteredProducts.filter { it.weight == filterState.weightFilter }
            // Update brand options based on selected weight
            brandState = filteredProducts.distinctBy { it.brand }
        }

        // If no filters are applied, reset both states
        if (filterState.brandFilter.isEmpty() && filterState.weightFilter.isEmpty()) {
            weightState = products.distinctBy { it.weight }
            brandState = products.distinctBy { it.brand }
        }

        // Apply price sorting
        when (filterState.priceSort) {
            PriceFilters.LowToHigh -> filteredProducts = filteredProducts.sortedBy { it.mrpPrice }
            PriceFilters.HighToLow -> filteredProducts =
                filteredProducts.sortedByDescending { it.mrpPrice }

            null -> {}
        }

        return filteredProducts
    }

    AddItemFilters(
        onClickLowToHigh = {
            filterState = filterState.copy(priceSort = PriceFilters.LowToHigh)
            productsState = applyFilters(products)
        },
        onClickHighToLow = {
            filterState = filterState.copy(priceSort = PriceFilters.HighToLow)
            productsState = applyFilters(products)
        },
        onClickCross = {
            filterState = filterState.copy(priceSort = null)
            productsState = applyFilters(products)
        },
        weights = weightState,
        onClickWeight = { weight ->
            filterState = filterState.copy(weightFilter = weight)
            productsState = applyFilters(products)
        },
        brands = brandState,
        onClickBrand = { brand ->
            filterState = filterState.copy(brandFilter = brand)
            productsState = applyFilters(products)
        }
    )

    LazyVerticalGrid(
        columns = GridCells.Adaptive(160.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(productsState) {
            var isAdded by remember {
                mutableStateOf(false)
            }
            val product = shoppingListModel.getProduct(it).collectAsState(initial = Product)
            SearchProductCard(
                product = it,
                isAdded = product.value != null,
                onAddToList = {
                    shoppingListModel.fetchSimilarItems(source, it, location ?: Location())
                    isAdded = true
                    navigateBack()
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