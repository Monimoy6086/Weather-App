package com.example.weatherapp.data.remote

import com.google.gson.annotations.SerializedName

data class HourlyWeatherForecastResponse(
    @SerializedName("lat") val lat: Double?,
    @SerializedName("lon") val lon: Double?,
    @SerializedName("timezone") val timezone: String?,
    @SerializedName("timezone_offset") val timezoneOffset: Int?,
    @SerializedName("data") val data: List<HourlyItemDto>?,
    @SerializedName("prev") val prev: String?,
    @SerializedName("next") val next: String?
)

data class HourlyItemDto(
    @SerializedName("dt") val dt: Long?,
    @SerializedName("temp") val temp: Double?,
    @SerializedName("feels_like") val feelsLike: Double?,
    @SerializedName("pressure") val pressure: Double?,
    @SerializedName("humidity") val humidity: Int?,
    @SerializedName("dew_point") val dewPoint: Double?,
    @SerializedName("uvi") val uvi: Double?,
    @SerializedName("clouds") val clouds: Int?,
    @SerializedName("visibility") val visibility: Int?,
    @SerializedName("wind_speed") val windSpeed: Double?,
    @SerializedName("wind_deg") val windDeg: Int?,
    @SerializedName("wind_gust") val windGust: Double?,
    @SerializedName("weather") val weather: List<WeatherConditionDto>?,
    @SerializedName("pop") val pop: Double?
)