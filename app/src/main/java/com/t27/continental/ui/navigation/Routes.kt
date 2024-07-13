package com.t27.continental.ui.navigation

import androidx.annotation.StringRes
import com.t27.continental.R

sealed class Routes(val route: String, @StringRes val title: Int) {
    data object Home : Routes(route = "home", title = R.string.app_name)
    data object Settings : Routes(route = "settings", title = R.string.settings)
    data object ChangeLocation : Routes(route = "change_location", title = R.string.change_location)
    data object ChangeLocationBySource :
        Routes(route = "change_location?source={source}", title = R.string.change_location)

    data object AddItem : Routes(route = "add_item", title = R.string.add_item)
}

