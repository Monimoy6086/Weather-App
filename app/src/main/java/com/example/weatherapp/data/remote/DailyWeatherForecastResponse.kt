package com.example.weatherapp.data.remote

import com.google.gson.annotations.SerializedName

data class DailyWeatherForecastResponse(
    @SerializedName("lat") val lat: Double?,
    @SerializedName("lon") val lon: Double?,
    @SerializedName("timezone") val timezone: String?,
    @SerializedName("timezone_offset") val timezoneOffset: Int?,
    @SerializedName("data") val data: List<DailyItemDto>?,
    @SerializedName("prev") val prev: String?,
    @SerializedName("next") val next: String?
)

data class DailyItemDto(
    @SerializedName("dt") val dt: Long?,
    @SerializedName("sunrise") val sunrise: Long?,
    @SerializedName("sunset") val sunset: Long?,
    @SerializedName("moonrise") val moonrise: Long?,
    @SerializedName("moonset") val moonset: Long?,
    @SerializedName("moon_phase") val moonPhase: Double?,
    @SerializedName("temp") val temp: DailyTempDto?,
    @SerializedName("feels_like") val feelsLike: DailyFeelsLikeDto?,
    @SerializedName("pressure") val pressure: Double?,
    @SerializedName("humidity") val humidity: Int?,
    @SerializedName("wind_speed") val windSpeed: Double?,
    @SerializedName("wind_deg") val windDeg: Int?,
    @SerializedName("weather") val weather: List<WeatherConditionDto>?,
    @SerializedName("clouds") val clouds: Int?,
    @SerializedName("pop") val pop: Double?,
    @SerializedName("rain") val rain: Double?,
    @SerializedName("uvi") val uvi: Double?
)

data class DailyTempDto(
    @SerializedName("day") val day: Double?,
    @SerializedName("min") val min: Double?,
    @SerializedName("max") val max: Double?,
    @SerializedName("night") val night: Double?,
    @SerializedName("eve") val eve: Double?,
    @SerializedName("morn") val morn: Double?
)
data class DailyFeelsLikeDto(
    @SerializedName("day") val day: Double?,
    @SerializedName("night") val night: Double?,
    @SerializedName("eve") val eve: Double?,
    @SerializedName("morn") val morn: Double?
)