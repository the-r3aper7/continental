package com.t27.continental.data.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.t27.continental.data.models.Location
import com.t27.continental.data.models.Product
import com.t27.continental.data.models.SearchSource
import com.t27.continental.data.network.MashinaApi
import com.t27.continental.data.repository.ShoppingListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ShoppingListViewModel(
    private val shoppingListRepository: ShoppingListRepository,
    private val mashinaApi: MashinaApi
) : ViewModel() {

    val shoppingListItems: StateFlow<ShoppingListState> =
        shoppingListRepository.getDistinctProductsStream()
            .map { ShoppingListState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ShoppingListState()
            )

    fun deleteProducts(products: List<Product>) {
        products.forEach {
            viewModelScope.launch {
                shoppingListRepository.deleteProduct(it)
            }
        }
    }

    private fun insertProducts(products: List<Product>) {
        products.forEach {
            viewModelScope.launch {
                shoppingListRepository.insertProduct(it)
            }
        }
    }

    fun getProduct(product: Product): Flow<Product?> {
        return shoppingListRepository.getProductStream(product)
    }

    fun getSimilarItems(product: Product): Flow<List<Product>> =
        shoppingListRepository.getSimilarProductsStream(product)

    fun fetchSimilarItems(source: SearchSource, product: Product, location: Location) {
        viewModelScope.launch {
            try {
                val similarProducts = mashinaApi.retrofitService.similarProducts(
                    product = Json.encodeToString(product),
                    source = Json.encodeToString(createLocation(source, location)),
                    targets = Json.encodeToString(createTargets(source, location))
                )
                insertProducts(similarProducts.productData.orEmpty())
            } catch (e: Exception) {
                Log.e("GetSimilarItems", "Error fetching similar items", e)
            }
        }
    }

    private fun createLocation(source: SearchSource, location: Location): Location = when (source) {
        SearchSource.Instamart -> Location(
            source = source.name.lowercase(),
            latitude = location.latitude,
            longitude = location.longitude,
            storeId = location.storeId
        )

        SearchSource.Blinkit -> Location(
            source = source.name.lowercase(),
            latitude = location.latitude,
            longitude = location.longitude,
            gr = location.gr
        )
    }

    private fun createTargets(source: SearchSource, location: Location): List<Location> =
        when (source) {
            SearchSource.Instamart -> listOf(
                Location(
                    source = SearchSource.Blinkit.name.lowercase(),
                    latitude = location.latitude,
                    longitude = location.longitude,
                    gr = location.gr
                )
            )

            SearchSource.Blinkit -> listOf(
                Location(
                    source = SearchSource.Instamart.name.lowercase(),
                    latitude = location.latitude,
                    longitude = location.longitude,
                    storeId = location.storeId
                )
            )
        }
}

data class ShoppingListState(val shoppingListItems: List<Product> = emptyList())