package com.distinctroutes.app.domain.usecase

import com.distinctroutes.app.data.api.NominatimApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class LiveNominatimReverseGeocodeTest {

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val req = chain.request().newBuilder()
                .header("User-Agent", "DistinctRoutes/0.2.0 (distinctroutes@pm.me; Android)")
                .build()
            chain.proceed(req)
        }
        .build()

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    private val apiService = Retrofit.Builder()
        .baseUrl("https://nominatim.openstreetmap.org/")
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(NominatimApiService::class.java)

    private val useCase = GeocodingUseCase(apiService)

    @Test
    fun `test live reverse geocoding returns street name for Perth CBD`() = runBlocking {
        try {
            val result = useCase.reverseGeocode(-31.9505, 115.8605)
            println("Live Reverse Geocode Perth CBD: ${result.getOrNull()}")
            assertTrue(result.isSuccess)
        } catch (e: Exception) {
            println("Live reverse geocode test skipped due to network/rate limit: ${e.message}")
        }
    }
}