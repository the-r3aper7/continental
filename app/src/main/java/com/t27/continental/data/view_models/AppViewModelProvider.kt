package com.t27.continental.data.view_models

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.t27.continental.MainApplication
import com.t27.continental.data.network.MashinaApi

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            ShoppingListViewModel(mainApplication().container.shoppingListRepository, MashinaApi)
        }
        initializer {
            LocationViewModel(mainApplication().container.locationRepository)
        }
        initializer {
            SearchProductsViewModel(MashinaApi)
        }
    }
}

fun CreationExtras.mainApplication(): MainApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as MainApplication)
