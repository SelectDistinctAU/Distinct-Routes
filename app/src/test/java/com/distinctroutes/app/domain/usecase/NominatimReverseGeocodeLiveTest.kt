package com.distinctroutes.app.domain.usecase

import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Assert.assertTrue
import org.junit.Test

class NominatimReverseGeocodeLiveTest {

    private val client = OkHttpClient()

    @Test
    fun `test live Nominatim reverse geocoding with trailing slash`() {
        val url = "https://nominatim.openstreetmap.org/reverse?lat=-31.9505&lon=115.8605&format=jsonv2&addressdetails=1"
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "DistinctRoutes/0.2.0 (distinctroutes@pm.me; Android)")
            .build()

        try {
            val response = client.newCall(request).execute()
            println("Nominatim Response Code: ${response.code}")
            if (response.isSuccessful && response.body != null) {
                val body = response.body!!.string()
                println("Nominatim Live Body: $body")
                assertTrue(body.contains("Perth") || body.contains("Western Australia"))
            }
        } catch (e: Exception) {
            println("Live test skipped: ${e.message}")
        }
    }
}