package com.app4r4.data.model

import com.google.gson.annotations.SerializedName

// Request models
data class VehicleEstimateRequest(
    val type: String = "vehicle",
    @SerializedName("distance_unit") val distanceUnit: String = "km",
    @SerializedName("distance_value") val distanceValue: Double,
    @SerializedName("vehicle_model_id") val vehicleModelId: String = "7268a9b7-17e8-4c8d-acca-57059252afe9" // sedan médio
)

data class FlightEstimateRequest(
    val type: String = "flight",
    val passengers: Int = 1,
    val legs: List<FlightLeg>
)

data class FlightLeg(
    @SerializedName("departure_airport") val departureAirport: String,
    @SerializedName("destination_airport") val destinationAirport: String
)

data class ElectricityEstimateRequest(
    val type: String = "electricity",
    @SerializedName("electricity_unit") val electricityUnit: String = "kwh",
    @SerializedName("electricity_value") val electricityValue: Double,
    val country: String = "br"
)

// Response models
data class EstimateResponse(
    val data: EstimateData
)

data class EstimateData(
    val id: String,
    val type: String,
    val attributes: EstimateAttributes
)

data class EstimateAttributes(
    @SerializedName("carbon_g") val carbonG: Int,
    @SerializedName("carbon_kg") val carbonKg: Double,
    @SerializedName("carbon_mt") val carbonMt: Double,
    @SerializedName("estimated_at") val estimatedAt: String
)

// Internal activity model
data class ActivityEntry(
    val type: ActivityType,
    val description: String,
    val carbonKg: Double,
    val date: Long = System.currentTimeMillis()
)

enum class ActivityType(val label: String, val icon: String) {
    CAR("Carro", "directions_car"),
    FLIGHT("Avião", "flight"),
    ELECTRICITY("Energia elétrica", "bolt"),
    FOOD_BEEF("Carne bovina", "restaurant"),
    FOOD_CHICKEN("Frango", "set_meal")
}
