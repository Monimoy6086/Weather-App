package com.example.weatherapp.`data`.local

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performInTransactionSuspending
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.example.weatherapp.`data`.local.entities.CurrentWeatherEntity
import com.example.weatherapp.`data`.local.entities.DailyForecastEntity
import com.example.weatherapp.`data`.local.entities.HourlyForecastEntity
import com.example.weatherapp.`data`.local.entities.WeatherAlertEntity
import javax.`annotation`.processing.Generated
import kotlin.Double
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class WeatherDao_Impl(
  __db: RoomDatabase,
) : WeatherDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfCurrentWeatherEntity: EntityInsertAdapter<CurrentWeatherEntity>

  private val __insertAdapterOfHourlyForecastEntity: EntityInsertAdapter<HourlyForecastEntity>

  private val __insertAdapterOfDailyForecastEntity: EntityInsertAdapter<DailyForecastEntity>

  private val __insertAdapterOfWeatherAlertEntity: EntityInsertAdapter<WeatherAlertEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfCurrentWeatherEntity = object : EntityInsertAdapter<CurrentWeatherEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `current_weather` (`cityName`,`temperatureC`,`feelsLikeC`,`humidity`,`windSpeedKmh`,`uvIndex`,`airQualityIndex`,`conditionName`,`conditionText`,`updatedAtMillis`) VALUES (?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: CurrentWeatherEntity) {
        statement.bindText(1, entity.cityName)
        statement.bindDouble(2, entity.temperatureC)
        statement.bindDouble(3, entity.feelsLikeC)
        statement.bindLong(4, entity.humidity.toLong())
        statement.bindDouble(5, entity.windSpeedKmh)
        statement.bindDouble(6, entity.uvIndex)
        statement.bindLong(7, entity.airQualityIndex.toLong())
        statement.bindText(8, entity.conditionName)
        statement.bindText(9, entity.conditionText)
        statement.bindLong(10, entity.updatedAtMillis)
      }
    }
    this.__insertAdapterOfHourlyForecastEntity = object : EntityInsertAdapter<HourlyForecastEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `hourly_forecast` (`id`,`cityName`,`timeFormatted`,`timestampMillis`,`temperatureC`,`precipitationChance`,`conditionName`) VALUES (nullif(?, 0),?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: HourlyForecastEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.cityName)
        statement.bindText(3, entity.timeFormatted)
        statement.bindLong(4, entity.timestampMillis)
        statement.bindDouble(5, entity.temperatureC)
        statement.bindLong(6, entity.precipitationChance.toLong())
        statement.bindText(7, entity.conditionName)
      }
    }
    this.__insertAdapterOfDailyForecastEntity = object : EntityInsertAdapter<DailyForecastEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `daily_forecast` (`id`,`cityName`,`dayOfWeek`,`dateText`,`maxTempC`,`minTempC`,`precipitationChance`,`conditionName`,`conditionSummary`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: DailyForecastEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.cityName)
        statement.bindText(3, entity.dayOfWeek)
        statement.bindText(4, entity.dateText)
        statement.bindDouble(5, entity.maxTempC)
        statement.bindDouble(6, entity.minTempC)
        statement.bindLong(7, entity.precipitationChance.toLong())
        statement.bindText(8, entity.conditionName)
        statement.bindText(9, entity.conditionSummary)
      }
    }
    this.__insertAdapterOfWeatherAlertEntity = object : EntityInsertAdapter<WeatherAlertEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `weather_alerts` (`alertId`,`cityName`,`title`,`description`,`severity`,`issueTimeFormatted`) VALUES (?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: WeatherAlertEntity) {
        statement.bindText(1, entity.alertId)
        statement.bindText(2, entity.cityName)
        statement.bindText(3, entity.title)
        statement.bindText(4, entity.description)
        statement.bindText(5, entity.severity)
        statement.bindText(6, entity.issueTimeFormatted)
      }
    }
  }

  public override suspend fun insertCurrentWeather(weather: CurrentWeatherEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfCurrentWeatherEntity.insert(_connection, weather)
  }

  public override suspend fun insertHourlyForecasts(hourly: List<HourlyForecastEntity>): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfHourlyForecastEntity.insert(_connection, hourly)
  }

  public override suspend fun insertDailyForecasts(daily: List<DailyForecastEntity>): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfDailyForecastEntity.insert(_connection, daily)
  }

  public override suspend fun insertWeatherAlerts(alerts: List<WeatherAlertEntity>): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfWeatherAlertEntity.insert(_connection, alerts)
  }

  public override suspend fun updateFullWeatherCache(
    current: CurrentWeatherEntity,
    hourly: List<HourlyForecastEntity>,
    daily: List<DailyForecastEntity>,
    alerts: List<WeatherAlertEntity>,
  ): Unit = performInTransactionSuspending(__db) {
    super@WeatherDao_Impl.updateFullWeatherCache(current, hourly, daily, alerts)
  }

  public override fun getCurrentWeather(cityName: String): Flow<CurrentWeatherEntity?> {
    val _sql: String = "SELECT * FROM current_weather WHERE cityName = ? LIMIT 1"
    return createFlow(__db, false, arrayOf("current_weather")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, cityName)
        val _columnIndexOfCityName: Int = getColumnIndexOrThrow(_stmt, "cityName")
        val _columnIndexOfTemperatureC: Int = getColumnIndexOrThrow(_stmt, "temperatureC")
        val _columnIndexOfFeelsLikeC: Int = getColumnIndexOrThrow(_stmt, "feelsLikeC")
        val _columnIndexOfHumidity: Int = getColumnIndexOrThrow(_stmt, "humidity")
        val _columnIndexOfWindSpeedKmh: Int = getColumnIndexOrThrow(_stmt, "windSpeedKmh")
        val _columnIndexOfUvIndex: Int = getColumnIndexOrThrow(_stmt, "uvIndex")
        val _columnIndexOfAirQualityIndex: Int = getColumnIndexOrThrow(_stmt, "airQualityIndex")
        val _columnIndexOfConditionName: Int = getColumnIndexOrThrow(_stmt, "conditionName")
        val _columnIndexOfConditionText: Int = getColumnIndexOrThrow(_stmt, "conditionText")
        val _columnIndexOfUpdatedAtMillis: Int = getColumnIndexOrThrow(_stmt, "updatedAtMillis")
        val _result: CurrentWeatherEntity?
        if (_stmt.step()) {
          val _tmpCityName: String
          _tmpCityName = _stmt.getText(_columnIndexOfCityName)
          val _tmpTemperatureC: Double
          _tmpTemperatureC = _stmt.getDouble(_columnIndexOfTemperatureC)
          val _tmpFeelsLikeC: Double
          _tmpFeelsLikeC = _stmt.getDouble(_columnIndexOfFeelsLikeC)
          val _tmpHumidity: Int
          _tmpHumidity = _stmt.getLong(_columnIndexOfHumidity).toInt()
          val _tmpWindSpeedKmh: Double
          _tmpWindSpeedKmh = _stmt.getDouble(_columnIndexOfWindSpeedKmh)
          val _tmpUvIndex: Double
          _tmpUvIndex = _stmt.getDouble(_columnIndexOfUvIndex)
          val _tmpAirQualityIndex: Int
          _tmpAirQualityIndex = _stmt.getLong(_columnIndexOfAirQualityIndex).toInt()
          val _tmpConditionName: String
          _tmpConditionName = _stmt.getText(_columnIndexOfConditionName)
          val _tmpConditionText: String
          _tmpConditionText = _stmt.getText(_columnIndexOfConditionText)
          val _tmpUpdatedAtMillis: Long
          _tmpUpdatedAtMillis = _stmt.getLong(_columnIndexOfUpdatedAtMillis)
          _result = CurrentWeatherEntity(_tmpCityName,_tmpTemperatureC,_tmpFeelsLikeC,_tmpHumidity,_tmpWindSpeedKmh,_tmpUvIndex,_tmpAirQualityIndex,_tmpConditionName,_tmpConditionText,_tmpUpdatedAtMillis)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getHourlyForecasts(cityName: String): Flow<List<HourlyForecastEntity>> {
    val _sql: String = "SELECT * FROM hourly_forecast WHERE cityName = ? ORDER BY timestampMillis ASC"
    return createFlow(__db, false, arrayOf("hourly_forecast")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, cityName)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCityName: Int = getColumnIndexOrThrow(_stmt, "cityName")
        val _columnIndexOfTimeFormatted: Int = getColumnIndexOrThrow(_stmt, "timeFormatted")
        val _columnIndexOfTimestampMillis: Int = getColumnIndexOrThrow(_stmt, "timestampMillis")
        val _columnIndexOfTemperatureC: Int = getColumnIndexOrThrow(_stmt, "temperatureC")
        val _columnIndexOfPrecipitationChance: Int = getColumnIndexOrThrow(_stmt, "precipitationChance")
        val _columnIndexOfConditionName: Int = getColumnIndexOrThrow(_stmt, "conditionName")
        val _result: MutableList<HourlyForecastEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: HourlyForecastEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpCityName: String
          _tmpCityName = _stmt.getText(_columnIndexOfCityName)
          val _tmpTimeFormatted: String
          _tmpTimeFormatted = _stmt.getText(_columnIndexOfTimeFormatted)
          val _tmpTimestampMillis: Long
          _tmpTimestampMillis = _stmt.getLong(_columnIndexOfTimestampMillis)
          val _tmpTemperatureC: Double
          _tmpTemperatureC = _stmt.getDouble(_columnIndexOfTemperatureC)
          val _tmpPrecipitationChance: Int
          _tmpPrecipitationChance = _stmt.getLong(_columnIndexOfPrecipitationChance).toInt()
          val _tmpConditionName: String
          _tmpConditionName = _stmt.getText(_columnIndexOfConditionName)
          _item = HourlyForecastEntity(_tmpId,_tmpCityName,_tmpTimeFormatted,_tmpTimestampMillis,_tmpTemperatureC,_tmpPrecipitationChance,_tmpConditionName)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getDailyForecasts(cityName: String): Flow<List<DailyForecastEntity>> {
    val _sql: String = "SELECT * FROM daily_forecast WHERE cityName = ? ORDER BY id ASC"
    return createFlow(__db, false, arrayOf("daily_forecast")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, cityName)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCityName: Int = getColumnIndexOrThrow(_stmt, "cityName")
        val _columnIndexOfDayOfWeek: Int = getColumnIndexOrThrow(_stmt, "dayOfWeek")
        val _columnIndexOfDateText: Int = getColumnIndexOrThrow(_stmt, "dateText")
        val _columnIndexOfMaxTempC: Int = getColumnIndexOrThrow(_stmt, "maxTempC")
        val _columnIndexOfMinTempC: Int = getColumnIndexOrThrow(_stmt, "minTempC")
        val _columnIndexOfPrecipitationChance: Int = getColumnIndexOrThrow(_stmt, "precipitationChance")
        val _columnIndexOfConditionName: Int = getColumnIndexOrThrow(_stmt, "conditionName")
        val _columnIndexOfConditionSummary: Int = getColumnIndexOrThrow(_stmt, "conditionSummary")
        val _result: MutableList<DailyForecastEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DailyForecastEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpCityName: String
          _tmpCityName = _stmt.getText(_columnIndexOfCityName)
          val _tmpDayOfWeek: String
          _tmpDayOfWeek = _stmt.getText(_columnIndexOfDayOfWeek)
          val _tmpDateText: String
          _tmpDateText = _stmt.getText(_columnIndexOfDateText)
          val _tmpMaxTempC: Double
          _tmpMaxTempC = _stmt.getDouble(_columnIndexOfMaxTempC)
          val _tmpMinTempC: Double
          _tmpMinTempC = _stmt.getDouble(_columnIndexOfMinTempC)
          val _tmpPrecipitationChance: Int
          _tmpPrecipitationChance = _stmt.getLong(_columnIndexOfPrecipitationChance).toInt()
          val _tmpConditionName: String
          _tmpConditionName = _stmt.getText(_columnIndexOfConditionName)
          val _tmpConditionSummary: String
          _tmpConditionSummary = _stmt.getText(_columnIndexOfConditionSummary)
          _item = DailyForecastEntity(_tmpId,_tmpCityName,_tmpDayOfWeek,_tmpDateText,_tmpMaxTempC,_tmpMinTempC,_tmpPrecipitationChance,_tmpConditionName,_tmpConditionSummary)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getWeatherAlerts(cityName: String): Flow<List<WeatherAlertEntity>> {
    val _sql: String = "SELECT * FROM weather_alerts WHERE cityName = ?"
    return createFlow(__db, false, arrayOf("weather_alerts")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, cityName)
        val _columnIndexOfAlertId: Int = getColumnIndexOrThrow(_stmt, "alertId")
        val _columnIndexOfCityName: Int = getColumnIndexOrThrow(_stmt, "cityName")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfSeverity: Int = getColumnIndexOrThrow(_stmt, "severity")
        val _columnIndexOfIssueTimeFormatted: Int = getColumnIndexOrThrow(_stmt, "issueTimeFormatted")
        val _result: MutableList<WeatherAlertEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: WeatherAlertEntity
          val _tmpAlertId: String
          _tmpAlertId = _stmt.getText(_columnIndexOfAlertId)
          val _tmpCityName: String
          _tmpCityName = _stmt.getText(_columnIndexOfCityName)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpSeverity: String
          _tmpSeverity = _stmt.getText(_columnIndexOfSeverity)
          val _tmpIssueTimeFormatted: String
          _tmpIssueTimeFormatted = _stmt.getText(_columnIndexOfIssueTimeFormatted)
          _item = WeatherAlertEntity(_tmpAlertId,_tmpCityName,_tmpTitle,_tmpDescription,_tmpSeverity,_tmpIssueTimeFormatted)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearHourlyForCity(cityName: String) {
    val _sql: String = "DELETE FROM hourly_forecast WHERE cityName = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, cityName)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearDailyForCity(cityName: String) {
    val _sql: String = "DELETE FROM daily_forecast WHERE cityName = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, cityName)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearAlertsForCity(cityName: String) {
    val _sql: String = "DELETE FROM weather_alerts WHERE cityName = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, cityName)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
