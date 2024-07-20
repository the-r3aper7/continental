package com.t27.continental.data.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.t27.continental.data.models.Location
import com.t27.continental.data.models.Products
import com.t27.continental.data.models.SearchSource
import com.t27.continental.data.network.MashinaApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

sealed interface SearchProductsState {
    data class Success(val data: Products) : SearchProductsState
    data object Error : SearchProductsState
    data object Loading : SearchProductsState
    data object Initial : SearchProductsState
}

class SearchProductsViewModel(
    private val apiService: MashinaApi
) : ViewModel() {

    private val _searchProductState =
        MutableStateFlow<SearchProductsState>(SearchProductsState.Initial)
    val searchProductState = _searchProductState.asStateFlow()

    private val _source = MutableStateFlow(SearchSource.Blinkit)
    val source = _source.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private var searchJob: Job? = null

    @OptIn(FlowPreview::class)
    fun updateSearchQuery(q: String, source: SearchSource, location: Location) {
        _searchQuery.value = q
        _source.value = source

        searchJob?.cancel()

        searchJob = searchQuery
            .debounce(750)
            .onEach { query ->
                if (query.isBlank()) {
                    _searchProductState.value = SearchProductsState.Initial
                    return@onEach
                }
                _searchProductState.value = SearchProductsState.Loading
                _searchProductState.value = searchProducts(q, source, location)
            }
            .launchIn(viewModelScope)
    }

    private suspend fun searchProducts(
        query: String,
        source: SearchSource,
        location: Location
    ): SearchProductsState {
        return try {
            val products = when (source) {
                SearchSource.Blinkit ->
                    apiService.retrofitService.searchBlinkitProducts(
                        query,
                        location.latitude,
                        location.longitude,
                        location.gr
                    )


                SearchSource.Instamart ->
                    apiService.retrofitService.searchInstamartProducts(
                        query,
                        location.latitude,
                        location.longitude,
                        location.storeId
                    )

            }
            SearchProductsState.Success(products)
        } catch (e: Exception) {
            Log.e("TAG", "searchProducts: $location $query $source $e")
            SearchProductsState.Error
        }
    }
}