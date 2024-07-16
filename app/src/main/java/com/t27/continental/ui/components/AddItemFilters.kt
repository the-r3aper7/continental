package com.t27.continental.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.t27.continental.data.models.Product

@Composable
fun AddItemFilters(
    modifier: Modifier = Modifier,
    onClickLowToHigh: () -> Unit,
    onClickHighToLow: () -> Unit,
    onClickCross: () -> Unit,
    weights: List<Product>,
    onClickWeight: (weight: String) -> Unit,
    brands: List<Product>,
    onClickBrand: (brand: String) -> Unit
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
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