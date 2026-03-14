package com.app4r4.data.repository

import com.app4r4.data.api.CarbonApiService
import com.app4r4.data.db.ActivityDao
import com.app4r4.data.db.ActivityRecord
import com.app4r4.data.model.*
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

@Singleton
class CarbonRepository @Inject constructor(
    private val api: CarbonApiService,
    private val dao: ActivityDao
) {
    fun getAllRecords(): Flow<List<ActivityRecord>> = dao.getAllRecords()

    fun getRecordsThisMonth(): Flow<List<ActivityRecord>> =
        dao.getRecordsThisMonth(startOfCurrentMonth())

    fun getTotalCarbonThisMonth(): Flow<Double?> =
        dao.getTotalCarbonThisMonth(startOfCurrentMonth())

    suspend fun getTotalCarbonBetween(start: Long, end: Long): Double? =
        dao.getTotalCarbonBetween(start, end)

    suspend fun deleteRecord(record: ActivityRecord) = dao.delete(record)

    suspend fun calculateVehicleEmission(distanceKm: Double): Result<Double> {
        return try {
            val response = api.getVehicleEstimate(
                VehicleEstimateRequest(distanceValue = distanceKm)
            )
            if (response.isSuccessful) {
                val carbonKg = response.body()?.data?.attributes?.carbonKg ?: 0.0
                Result.Success(carbonKg)
            } else {
                // Fallback: estimativa local (0.21 kg CO2/km - média Brasil)
                Result.Success(distanceKm * 0.21)
            }
        } catch (e: Exception) {
            Result.Success(distanceKm * 0.21)
        }
    }

    suspend fun calculateFlightEmission(
        origin: String,
        destination: String,
        passengers: Int
    ): Result<Double> {
        return try {
            val response = api.getFlightEstimate(
                FlightEstimateRequest(
                    passengers = passengers,
                    legs = listOf(FlightLeg(origin, destination))
                )
            )
            if (response.isSuccessful) {
                val carbonKg = response.body()?.data?.attributes?.carbonKg ?: 0.0
                Result.Success(carbonKg)
            } else {
                Result.Success(passengers * 255.0) // ~255 kg por passageiro por voo médio
            }
        } catch (e: Exception) {
            Result.Success(passengers * 255.0)
        }
    }

    suspend fun calculateElectricityEmission(kwh: Double): Result<Double> {
        return try {
            val response = api.getElectricityEstimate(
                ElectricityEstimateRequest(electricityValue = kwh)
            )
            if (response.isSuccessful) {
                val carbonKg = response.body()?.data?.attributes?.carbonKg ?: 0.0
                Result.Success(carbonKg)
            } else {
                Result.Success(kwh * 0.074) // fator emissão Brasil 2023
            }
        } catch (e: Exception) {
            Result.Success(kwh * 0.074)
        }
    }

    suspend fun saveActivity(
        type: ActivityType,
        description: String,
        carbonKg: Double
    ) {
        dao.insert(
            ActivityRecord(
                type = type.name,
                description = description,
                carbonKg = carbonKg
            )
        )
    }

    private fun startOfCurrentMonth(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}
