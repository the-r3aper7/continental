package com.t27.continental.data.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.t27.continental.data.models.Location
import com.t27.continental.data.models.Products
import com.t27.continental.data.models.SearchSource
import com.t27.continental.data.network.MashinaApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

sealed interface SearchProductsState {
    data class Success(val data: Products?) : SearchProductsState
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

    private val _location = MutableStateFlow(Location())
    val location = _location.asStateFlow()

    init {
        observeSearchQuery()
    }

    fun setSource(source: SearchSource) {
        _source.value = source
    }

    fun updateSearchQuery(q: String, location: Location) {
        _searchQuery.value = q
        _location.value = location
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun observeSearchQuery() {
        searchQuery
            .debounce(750)
            .combine(source) { query, source -> Pair(query, source) }
            .flatMapLatest { (query, source) ->
                flow {
                    if (query.isBlank()) {
                        emit(SearchProductsState.Initial)
                    } else {
                        emit(SearchProductsState.Loading)
                        emit(searchProducts(query, source, location.value))
                    }
                }
            }
            .onEach { state -> _searchProductState.value = state }
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
            SearchProductsState.Success(
                products ?: Products(
                    productData = null,
                    statusCode = false
                )
            )
        } catch (e: Exception) {
            SearchProductsState.Error
        }
    }
}