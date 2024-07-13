package com.t27.continental.data.models

import com.t27.continental.R

enum class SearchSource(val title: String, val icon: Int) {
    Blinkit(title = "Blinkit", icon = R.drawable.blinkit),
    Instamart(title = "Instamart", icon = R.drawable.instamart);

    companion object {
        fun fromString(value: String): SearchSource? {
            return entries.find { it.name.equals(value, ignoreCase = true) }
        }
    }
}