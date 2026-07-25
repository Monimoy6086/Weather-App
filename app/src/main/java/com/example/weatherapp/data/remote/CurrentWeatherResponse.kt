package com.example.weatherapp.data.remote

import com.google.gson.annotations.SerializedName

data class CurrentWeatherResponse(
    @SerializedName("lat") val lat: Double?,
    @SerializedName("lon") val lon: Double?,
    @SerializedName("timezone") val timezone: String?,
    @SerializedName("timezone_offset") val timezoneOffset: Int?,
    @SerializedName("data") val data: List<WeatherDataItemDto>?,
)
data class WeatherDataItemDto(
    @SerializedName("dt") val dt: Long?,
    @SerializedName("sunrise") val sunrise: Long?,
    @SerializedName("sunset") val sunset: Long?,
    @SerializedName("temp") val temp: Double?,
    @SerializedName("feels_like") val feelsLike: Double?,
    @SerializedName("pressure") val pressure: Int?,
    @SerializedName("humidity") val humidity: Int?,
    @SerializedName("dew_point") val dewPoint: Double?,
    @SerializedName("uvi") val uvi: Double?,
    @SerializedName("clouds") val clouds: Int?,
    @SerializedName("visibility") val visibility: Int?,
    @SerializedName("wind_speed") val windSpeed: Double?,
    @SerializedName("wind_deg") val windDeg: Int?,
    @SerializedName("wind_gust") val windGust: Double?,
    @SerializedName("weather") val weather: List<WeatherConditionDto>?,
    @SerializedName("rain") val rain: RainDto?,
    @SerializedName("snow") val snow: SnowDto?,
    @SerializedName("alerts") val alerts: List<String>?
)

data class WeatherConditionDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("main") val main: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("icon") val icon: String?
)

data class RainDto(
    @SerializedName("1h") val rain1h: Double?
)

data class SnowDto(
    @SerializedName("1h") val snow1h: Double?
)

data class AlertsContainerDto(
    @SerializedName("alert") val alertList: List<AlertDto>?
)

data class AlertDto(
    @SerializedName("id") val id: String?,
    @SerializedName("sender_name") val senderName: String?,
    @SerializedName("event") val event: String?,
    @SerializedName("start") val start: Long?,
    @SerializedName("end") val end: Long?,
    @SerializedName("desc") val desc: String?
)
