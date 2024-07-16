package com.t27.continental.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.t27.continental.R
import com.t27.continental.data.models.Product

@Composable
fun SearchProductCard(
    modifier: Modifier = Modifier,
    product: Product,
    isAdded: Boolean,
    onAddToList: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .width(168.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large)),
            horizontalAlignment = Alignment.CenterHorizontally
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
                    SavingsChips(modifier.align(Alignment.BottomCenter), product)
                }
            }
            Text(
                text = product.name,
                maxLines = 2,
                minLines = 2,
                modifier = Modifier
                    .widthIn(max = 168.dp),
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold,
            )
            Box {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DisplayPrice(modifier.weight(1f), product = product)

                    SuggestionChip(
                        modifier = Modifier.weight(1f),
                        onClick = { expanded = true },
                        label = {
                            Text(
                                text = product.weight,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                    )

                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    }) {
                    DropdownMenuItem(
                        text = { Text(product.weight) },
                        onClick = {}
                    )
                }
            }
            Button(
                onClick = {
                    if (!isAdded) {
                        onAddToList()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    disabledContentColor = MaterialTheme.colorScheme.onSecondary
                ),
                enabled = !isAdded
            ) {
                Text(text = if (isAdded) "Added" else "Add to list")
            }
        }
    }
}

@Composable
fun SavingsChips(modifier: Modifier = Modifier, product: Product) {
    AssistChip(
        onClick = { },
        label = {
            Text(
                text = "Saves ${product.currency}${product.totalSavings}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
            )
        },
        enabled = true,
        colors = AssistChipDefaults.assistChipColors(
            Color.Black.copy(alpha = 0.65f)
        ),
        modifier = modifier
    )
}