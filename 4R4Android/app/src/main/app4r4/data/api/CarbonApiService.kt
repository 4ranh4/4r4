package com.app4r4.data.api

import com.app4r4.data.model.ElectricityEstimateRequest
import com.app4r4.data.model.EstimateResponse
import com.app4r4.data.model.FlightEstimateRequest
import com.app4r4.data.model.VehicleEstimateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface CarbonApiService {

    @POST("estimates")
    suspend fun getVehicleEstimate(
        @Body request: VehicleEstimateRequest
    ): Response<EstimateResponse>

    @POST("estimates")
    suspend fun getFlightEstimate(
        @Body request: FlightEstimateRequest
    ): Response<EstimateResponse>

    @POST("estimates")
    suspend fun getElectricityEstimate(
        @Body request: ElectricityEstimateRequest
    ): Response<EstimateResponse>
}
