package com.t27.continental.ui.components

import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.t27.continental.R
import com.t27.continental.data.models.SearchSource

@Composable
fun SearchSourceIcon(modifier: Modifier = Modifier, source: SearchSource?) {
    val iconId = when (source) {
        SearchSource.Blinkit -> R.drawable.blinkit
        SearchSource.Instamart -> R.drawable.instamart
        else -> R.drawable.instamart
    }
    Image(
        painter = painterResource(id = iconId),
        contentDescription = "${source?.name?.lowercase()} icon",
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
    )
}