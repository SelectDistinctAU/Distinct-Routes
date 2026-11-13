package com.distinctroutes.app.data.local.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room ORM Entity representing an on-device saved place (e.g. Home, Work, Local Bike Shop).
 */
@Entity(tableName = "saved_places")
data class SavedPlaceEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "label")
    val label: String,

    @ColumnInfo(name = "icon_type")
    val iconType: String = "FAVORITE",

    @ColumnInfo(name = "address_text")
    val addressText: String,

    @ColumnInfo(name = "latitude")
    val latitude: Double,

    @ColumnInfo(name = "longitude")
    val longitude: Double,

    @ColumnInfo(name = "saved_timestamp")
    val savedTimestamp: Long = System.currentTimeMillis()
)