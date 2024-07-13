package com.t27.continental.ui.navigation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.t27.continental.data.models.SearchSource
import com.t27.continental.data.view_models.AppViewModelProvider
import com.t27.continental.data.view_models.LocationViewModel
import com.t27.continental.ui.navigation.Routes

@Composable
fun ChangeLocation(
    modifier: Modifier = Modifier,
    navController: NavController,
    locationModel: LocationViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    LocationsList(navController = navController, modifier = modifier, locationModel = locationModel)
}

@Composable
fun LocationsList(
    modifier: Modifier = Modifier,
    navController: NavController,
    locationModel: LocationViewModel
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SearchSource.entries.toTypedArray().forEach {
            item {
                val location = locationModel.getLocation(it).collectAsState()
                ListItem(
                    leadingContent = {
                        Image(
                            painter = painterResource(id = it.icon),
                            contentDescription = "",
                            modifier = Modifier
                                .width(64.dp)
                                .height(64.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    },
                    headlineContent = {
                        if (location.value == null) {
                            Text(text = "Setup the location")
                        } else {
                            Text(
                                text = location.value!!.mainText,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    supportingContent = {
                        if (location.value != null) {
                            Text(
                                text = location.value!!.description,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    modifier = Modifier
                        .clickable {
                            navController.navigate(
                                Routes.ChangeLocation.route.plus("?source=${it.name.lowercase()}")
                            )
                        }
                        .clip(RoundedCornerShape(8.dp)),
                    tonalElevation = 8.dp,
                    trailingContent = {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "arrow icon"
                        )
                    }
                )
            }
        }
    }
}
