package com.t27.continental.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Locations(
    val locations: List<Location>
)

@Serializable
@Entity(tableName = "location")
data class Location(
    @PrimaryKey
    val source: String = "",

    @SerialName("main_text")
    @ColumnInfo(name = "main_text")
    val mainText: String = "",
    val description: String = "",

    @SerialName("place_id")
    @ColumnInfo(name = "place_id")
    val placeId: String = "",
    val longitude: String = "",
    val latitude: String = "",

    @SerialName("store_id")
    @ColumnInfo(name = "store_id")
    val storeId: String = "",
    val gr: String = ""
)