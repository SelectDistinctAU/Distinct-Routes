package com.distinctroutes.app.data.repository

import com.distinctroutes.app.data.local.db.SavedPlaceDao
import com.distinctroutes.app.data.local.db.SavedPlaceEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavedPlacesRepository @Inject constructor(
    private val savedPlaceDao: SavedPlaceDao
) {
    fun getSavedPlacesFlow(): Flow<List<SavedPlaceEntity>> {
        return savedPlaceDao.getAllSavedPlacesFlow()
    }

    suspend fun getSavedPlaces(): List<SavedPlaceEntity> {
        return savedPlaceDao.getAllSavedPlaces()
    }

    suspend fun savePlace(place: SavedPlaceEntity) {
        savedPlaceDao.insertSavedPlace(place)
    }

    suspend fun deletePlace(id: String) {
        savedPlaceDao.deleteSavedPlaceById(id)
    }
}