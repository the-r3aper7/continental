package com.t27.continental.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.t27.continental.R
import com.t27.continental.data.models.Product
import com.t27.continental.data.models.SearchSource
import com.t27.continental.ui.theme.ContinentalTheme
import kotlinx.serialization.json.Json

@Composable
fun HomeProductCard(
    modifier: Modifier = Modifier,
    similarItem: List<Product>,
    onRemove: () -> Unit
) {
    var product by remember { mutableStateOf(similarItem.first()) }

    Card(
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(product.lowQuality)
                            .crossfade(true).build(),
                        contentDescription = product.name,
                        modifier = Modifier
                            .height(148.dp)
                            .width(148.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Color.White
                            ),
                        contentScale = ContentScale.Crop
                    )
                    if (!product.totalSavings.equals(0f)) {
                        SavingsChips(Modifier.align(Alignment.BottomCenter), product)
                    }
                    MoreOptions(Modifier.align(Alignment.TopEnd), onRemove)
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {

                    Text(
                        text = product.name, maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                    )

                    Text(
                        text = "${product.currency}${product.mrpPrice}",
                        maxLines = 1,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(text = product.weight)


                }
            }
            Text(
                text = stringResource(id = R.string.home_product_card_helper_text),
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 14.sp
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large))
            ) {
                items(similarItem) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.medium)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.shapes.medium
                            )
                            .clickable {
                                product =
                                    similarItem.first { item -> item.productId == it.productId }
                            }
                            .background(
                                if (product.source == it.source) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(
                                    0.25F
                                )
                            )
                            .padding(
                                top = dimensionResource(id = R.dimen.padding_extra_small),
                                bottom = dimensionResource(id = R.dimen.padding_extra_small),
                                start = dimensionResource(id = R.dimen.padding_small),
                                end = dimensionResource(id = R.dimen.padding_medium)
                            )
                    ) {
                        SearchSourceIcon(
                            source = SearchSource.fromString(it.source), modifier = modifier
                                .padding(4.dp)
                                .width(38.dp)
                        )
                        DisplayPrice(product = it)
                    }
                }
            }
        }
    }
}

@Composable
fun MoreOptions(modifier: Modifier = Modifier, onRemove: () -> Unit) {
    var expanded by remember {
        mutableStateOf(false)
    }
    Box(
        modifier = modifier
            .padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        Icon(
            Icons.Filled.MoreVert, contentDescription = "more options",
            modifier = Modifier.clickable {
                expanded = true
            },
            tint = Color.Black
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = "remove item icon")
                        Text(text = "Remove item")
                    }
                },
                onClick = onRemove
            )
        }
    }
}

@Composable
fun DisplayPrice(modifier: Modifier = Modifier, product: Product) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy((-8).dp)
    ) {
        if (!product.totalSavings.equals(0f)) {
            Text(
                text = "${product.currency}${product.mrpPrice}",
                maxLines = 1,
                fontWeight = FontWeight.Normal,
                fontSize = 10.sp,
                color = Color.Gray,
                textDecoration = TextDecoration.LineThrough
            )
        }
        Text(
            text = "${product.currency}${product.storePrice}",
            maxLines = 1,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Preview
@Composable
fun HomeProductCardPreview() {
    val products = """[
    {
      "source": "instamart",
      "currency": "₹",
      "is_variation": true,
      "product_id": "instamart-JKFJW8ZUYW-1",
      "brand": "Amul",
      "name": "Amul Taaza Milky Milk",
        "best_quality": "https://instamart-media-assets.swiggy.com/swiggy/image/upload/otx1jdphqy1gvdlwsddr",
        "low_quality": "https://instamart-media-assets.swiggy.com/swiggy/image/upload/w_256/otx1jdphqy1gvdlwsddr",
      "similar_with_id": "blinkit-19512-0",
      "weight": "500 ml",
      "total_savings": 1,
      "mrp_price": 28,
      "store_price": 27,
      "availability": true,
      "max_in_cart": 20,
      "inventory": 1,
      "category": "Dairy, Bread and Eggs",
      "deep_link": ""
    },
    {
      "source": "blinkit",
      "currency": "₹",
      "is_variation": false,
      "product_id": "blinkit-19512-0",
      "brand": "Amul",
      "name": "Amul Taaza Toned Fresh Milk",
        "best_quality": "http://cdn.grofers.com/cdn-cgi/image/w=256/app/assets/products/sliding_images/jpeg/5ee4441d-9109-48fa-9343-f5ce82b905a6.jpg?ts=1706182143",
        "low_quality": "http://cdn.grofers.com/cdn-cgi/image/w=256/app/assets/products/sliding_images/jpeg/5ee4441d-9109-48fa-9343-f5ce82b905a6.jpg?ts=1706182143",
      "similar_with_id": "blinkit-19512-0",
      "weight": "500 ml",
      "total_savings": 2,
      "mrp_price": 28,
      "store_price": 26,
      "availability": true,
      "max_in_cart": 12,
      "inventory": 12,
      "category": "",
      "deep_link": ""
    }
  ]"""
    val json = Json { ignoreUnknownKeys = true }
    val productList: List<Product> = json.decodeFromString(products)
    ContinentalTheme {
        HomeProductCard(similarItem = productList, onRemove = {})
    }
}
