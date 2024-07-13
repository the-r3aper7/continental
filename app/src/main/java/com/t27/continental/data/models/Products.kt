package com.t27.continental.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Products(
    @SerialName("status_code")
    val statusCode: Boolean,
    @SerialName("product_data")
    val productData: List<Product>?,
)

@Serializable
@Entity(tableName = "shopping_list")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val source: String,
    @SerialName("product_id")
    @ColumnInfo(name = "product_id")
    val productId: String,
    val currency: String,
    val brand: String,
    val name: String,
    val category: String,
    @SerialName("deep_link")
    @ColumnInfo(name = "deep_link")
    val deepLink: String,
    val weight: String,
    @SerialName("is_variation")
    @ColumnInfo(name = "is_variation")
    val isVariation: Boolean,
    @SerialName("similar_with_id")
    @ColumnInfo(name = "similar_with_id")
    val similarWithId: String,
    @SerialName("total_savings")
    @ColumnInfo(name = "total_savings")
    val totalSavings: Float,
    @SerialName("mrp_price")
    @ColumnInfo(name = "mrp_price")
    val mrpPrice: Float,
    @SerialName("store_price")
    @ColumnInfo(name = "store_price")
    val storePrice: Float,
    val availability: Boolean,
    @SerialName("max_in_cart")
    @ColumnInfo(name = "max_in_cart")
    val maxInCart: Long,
    val inventory: Long,
    @SerialName("best_quality")
    @ColumnInfo(name = "best_quality")
    val bestQuality: String,
    @SerialName("low_quality")
    @ColumnInfo(name = "low_quality")
    val lowQuality: String,
)