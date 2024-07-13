package com.t27.continental.data.repository

import com.t27.continental.data.daos.LocationDao
import com.t27.continental.data.models.Location
import com.t27.continental.data.models.SearchSource
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    suspend fun insertLocation(location: Location)

    suspend fun updateLocation(location: Location)

    fun getLocationStream(source: SearchSource): Flow<Location?>

    fun getAllLocationsStream(): Flow<List<Location>>
}

class LocationRepositoryImpl(private val locationDao: LocationDao) : LocationRepository {
    override suspend fun updateLocation(location: Location) = locationDao.update(location)

    override suspend fun insertLocation(location: Location) = locationDao.insert(location)

    override fun getLocationStream(source: SearchSource): Flow<Location?> =
        locationDao.getLocation(source.name.lowercase())

    override fun getAllLocationsStream(): Flow<List<Location>> = locationDao.getAllLocations()
}
