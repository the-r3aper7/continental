package com.t27.continental.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.t27.continental.data.models.Location
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Insert
    suspend fun insert(location: Location)

    @Update
    suspend fun update(location: Location)

    @Query("select * from location where source = :source")
    fun getLocation(source: String): Flow<Location>

    @Query("select * from location")
    fun getAllLocations(): Flow<List<Location>>
}