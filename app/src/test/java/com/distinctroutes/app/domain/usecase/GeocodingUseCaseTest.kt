package com.distinctroutes.app.domain.usecase

import com.distinctroutes.app.data.api.NominatimApiService
import com.distinctroutes.app.data.api.ReverseGeocodeDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Response

class GeocodingUseCaseTest {

    private val apiService: NominatimApiService = mockk()
    private val useCase = GeocodingUseCase(apiService)

    @Test
    fun `test reverseGeocode with suburb and city`() = runBlocking {
        val dto = ReverseGeocodeDto(
            place_id = 123L,
            display_name = "Leeming, City of Melville, Western Australia, 6149, Australia",
            address = mapOf("suburb" to "Leeming", "city" to "Perth")
        )
        coEvery { apiService.reverseGeocode(-32.08, 115.86) } returns Response.success(dto)

        val result = useCase.reverseGeocode(-32.08, 115.86)
        assertEquals("Leeming", result.getOrNull())
    }

    @Test
    fun `test reverseGeocode with road and suburb`() = runBlocking {
        val dto = ReverseGeocodeDto(
            place_id = 456L,
            display_name = "Kwinana Freeway, Jandakot, Western Australia, 6164, Australia",
            address = mapOf("road" to "Kwinana Freeway", "suburb" to "Jandakot")
        )
        coEvery { apiService.reverseGeocode(-32.10, 115.85) } returns Response.success(dto)

        val result = useCase.reverseGeocode(-32.10, 115.85)
        assertEquals("Kwinana Freeway, Jandakot", result.getOrNull())
    }

    @Test
    fun `test reverseGeocode fallback when network fails`() = runBlocking {
        coEvery { apiService.reverseGeocode(any(), any()) } throws RuntimeException("Network timeout")

        val result = useCase.reverseGeocode(-32.0834, 115.8612)
        assertEquals("Point (-32.0834, 115.8612)", result.getOrNull())
    }
}