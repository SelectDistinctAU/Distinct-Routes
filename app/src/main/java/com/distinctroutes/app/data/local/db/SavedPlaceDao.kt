package com.distinctroutes.app.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Room Data Access Object for on-device saved places.
 */
@Dao
interface SavedPlaceDao {

    @Query("SELECT * FROM saved_places ORDER BY saved_timestamp DESC")
    fun getAllSavedPlacesFlow(): Flow<List<SavedPlaceEntity>>

    @Query("SELECT * FROM saved_places ORDER BY saved_timestamp DESC")
    suspend fun getAllSavedPlaces(): List<SavedPlaceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedPlace(entity: SavedPlaceEntity)

    @Query("DELETE FROM saved_places WHERE id = :id")
    suspend fun deleteSavedPlaceById(id: String)

    @Query("DELETE FROM saved_places")
    suspend fun deleteAllSavedPlaces()
}