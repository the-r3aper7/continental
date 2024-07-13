package com.t27.continental

import android.app.Application
import android.content.Context
import com.t27.continental.data.database
import com.t27.continental.data.repository.LocationRepository
import com.t27.continental.data.repository.LocationRepositoryImpl
import com.t27.continental.data.repository.ShoppingListRepository
import com.t27.continental.data.repository.ShoppingListRepositoryImpl

interface AppContainer {
    val shoppingListRepository: ShoppingListRepository
    val locationRepository: LocationRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val shoppingListRepository: ShoppingListRepository by lazy {
        ShoppingListRepositoryImpl(database.getDatabase(context).shoppingListDao())
    }
    override val locationRepository: LocationRepository by lazy {
        LocationRepositoryImpl(database.getDatabase(context).locationDao())
    }
}

class MainApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}