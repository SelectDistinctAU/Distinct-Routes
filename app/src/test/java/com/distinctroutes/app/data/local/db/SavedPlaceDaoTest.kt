package com.distinctroutes.app.data.local.db

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.util.UUID

class SavedPlaceDaoTest {

    @Test
    fun `test SavedPlaceEntity mapping and field integrity`() {
        val id = UUID.randomUUID().toString()
        val place = SavedPlaceEntity(
            id = id,
            label = "Home",
            iconType = "HOME",
            addressText = "122 Hill View Terrace, St James, Perth",
            latitude = -32.0016,
            longitude = 115.8988,
            savedTimestamp = System.currentTimeMillis()
        )

        assertNotNull(place)
        assertEquals(id, place.id)
        assertEquals("Home", place.label)
        assertEquals("HOME", place.iconType)
        assertEquals("122 Hill View Terrace, St James, Perth", place.addressText)
        assertEquals(-32.0016, place.latitude, 0.0001)
        assertEquals(115.8988, place.longitude, 0.0001)
    }
}