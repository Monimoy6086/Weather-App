package com.example.weatherapp.`data`.local

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class WeatherDatabase_Impl : WeatherDatabase() {
  private val _weatherDao: Lazy<WeatherDao> = lazy {
    WeatherDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(2, "57440840ea365b911800f2c7a27d3977", "72ba7f1a7206fd96e269583738a920dc") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `current_weather` (`cityName` TEXT NOT NULL, `temperatureC` REAL NOT NULL, `feelsLikeC` REAL NOT NULL, `humidity` INTEGER NOT NULL, `windSpeedKmh` REAL NOT NULL, `uvIndex` REAL NOT NULL, `airQualityIndex` INTEGER NOT NULL, `conditionName` TEXT NOT NULL, `conditionText` TEXT NOT NULL, `updatedAtMillis` INTEGER NOT NULL, PRIMARY KEY(`cityName`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `hourly_forecast` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `cityName` TEXT NOT NULL, `timeFormatted` TEXT NOT NULL, `timestampMillis` INTEGER NOT NULL, `temperatureC` REAL NOT NULL, `precipitationChance` INTEGER NOT NULL, `conditionName` TEXT NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `daily_forecast` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `cityName` TEXT NOT NULL, `dayOfWeek` TEXT NOT NULL, `dateText` TEXT NOT NULL, `maxTempC` REAL NOT NULL, `minTempC` REAL NOT NULL, `precipitationChance` INTEGER NOT NULL, `conditionName` TEXT NOT NULL, `conditionSummary` TEXT NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `weather_alerts` (`alertId` TEXT NOT NULL, `cityName` TEXT NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `severity` TEXT NOT NULL, `issueTimeFormatted` TEXT NOT NULL, PRIMARY KEY(`alertId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '57440840ea365b911800f2c7a27d3977')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `current_weather`")
        connection.execSQL("DROP TABLE IF EXISTS `hourly_forecast`")
        connection.execSQL("DROP TABLE IF EXISTS `daily_forecast`")
        connection.execSQL("DROP TABLE IF EXISTS `weather_alerts`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection): RoomOpenDelegate.ValidationResult {
        val _columnsCurrentWeather: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsCurrentWeather.put("cityName", TableInfo.Column("cityName", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCurrentWeather.put("temperatureC", TableInfo.Column("temperatureC", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCurrentWeather.put("feelsLikeC", TableInfo.Column("feelsLikeC", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCurrentWeather.put("humidity", TableInfo.Column("humidity", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCurrentWeather.put("windSpeedKmh", TableInfo.Column("windSpeedKmh", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCurrentWeather.put("uvIndex", TableInfo.Column("uvIndex", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCurrentWeather.put("airQualityIndex", TableInfo.Column("airQualityIndex", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCurrentWeather.put("conditionName", TableInfo.Column("conditionName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCurrentWeather.put("conditionText", TableInfo.Column("conditionText", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCurrentWeather.put("updatedAtMillis", TableInfo.Column("updatedAtMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCurrentWeather: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesCurrentWeather: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoCurrentWeather: TableInfo = TableInfo("current_weather", _columnsCurrentWeather, _foreignKeysCurrentWeather, _indicesCurrentWeather)
        val _existingCurrentWeather: TableInfo = read(connection, "current_weather")
        if (!_infoCurrentWeather.equals(_existingCurrentWeather)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |current_weather(com.example.weatherapp.data.local.entities.CurrentWeatherEntity).
              | Expected:
              |""".trimMargin() + _infoCurrentWeather + """
              |
              | Found:
              |""".trimMargin() + _existingCurrentWeather)
        }
        val _columnsHourlyForecast: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsHourlyForecast.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHourlyForecast.put("cityName", TableInfo.Column("cityName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHourlyForecast.put("timeFormatted", TableInfo.Column("timeFormatted", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHourlyForecast.put("timestampMillis", TableInfo.Column("timestampMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHourlyForecast.put("temperatureC", TableInfo.Column("temperatureC", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHourlyForecast.put("precipitationChance", TableInfo.Column("precipitationChance", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHourlyForecast.put("conditionName", TableInfo.Column("conditionName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysHourlyForecast: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesHourlyForecast: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoHourlyForecast: TableInfo = TableInfo("hourly_forecast", _columnsHourlyForecast, _foreignKeysHourlyForecast, _indicesHourlyForecast)
        val _existingHourlyForecast: TableInfo = read(connection, "hourly_forecast")
        if (!_infoHourlyForecast.equals(_existingHourlyForecast)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |hourly_forecast(com.example.weatherapp.data.local.entities.HourlyForecastEntity).
              | Expected:
              |""".trimMargin() + _infoHourlyForecast + """
              |
              | Found:
              |""".trimMargin() + _existingHourlyForecast)
        }
        val _columnsDailyForecast: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsDailyForecast.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyForecast.put("cityName", TableInfo.Column("cityName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyForecast.put("dayOfWeek", TableInfo.Column("dayOfWeek", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyForecast.put("dateText", TableInfo.Column("dateText", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyForecast.put("maxTempC", TableInfo.Column("maxTempC", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyForecast.put("minTempC", TableInfo.Column("minTempC", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyForecast.put("precipitationChance", TableInfo.Column("precipitationChance", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyForecast.put("conditionName", TableInfo.Column("conditionName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDailyForecast.put("conditionSummary", TableInfo.Column("conditionSummary", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysDailyForecast: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesDailyForecast: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoDailyForecast: TableInfo = TableInfo("daily_forecast", _columnsDailyForecast, _foreignKeysDailyForecast, _indicesDailyForecast)
        val _existingDailyForecast: TableInfo = read(connection, "daily_forecast")
        if (!_infoDailyForecast.equals(_existingDailyForecast)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |daily_forecast(com.example.weatherapp.data.local.entities.DailyForecastEntity).
              | Expected:
              |""".trimMargin() + _infoDailyForecast + """
              |
              | Found:
              |""".trimMargin() + _existingDailyForecast)
        }
        val _columnsWeatherAlerts: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsWeatherAlerts.put("alertId", TableInfo.Column("alertId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWeatherAlerts.put("cityName", TableInfo.Column("cityName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWeatherAlerts.put("title", TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWeatherAlerts.put("description", TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWeatherAlerts.put("severity", TableInfo.Column("severity", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWeatherAlerts.put("issueTimeFormatted", TableInfo.Column("issueTimeFormatted", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysWeatherAlerts: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesWeatherAlerts: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoWeatherAlerts: TableInfo = TableInfo("weather_alerts", _columnsWeatherAlerts, _foreignKeysWeatherAlerts, _indicesWeatherAlerts)
        val _existingWeatherAlerts: TableInfo = read(connection, "weather_alerts")
        if (!_infoWeatherAlerts.equals(_existingWeatherAlerts)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |weather_alerts(com.example.weatherapp.data.local.entities.WeatherAlertEntity).
              | Expected:
              |""".trimMargin() + _infoWeatherAlerts + """
              |
              | Found:
              |""".trimMargin() + _existingWeatherAlerts)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "current_weather", "hourly_forecast", "daily_forecast", "weather_alerts")
  }

  public override fun clearAllTables() {
    super.performClear(false, "current_weather", "hourly_forecast", "daily_forecast", "weather_alerts")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(WeatherDao::class, WeatherDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>): List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun weatherDao(): WeatherDao = _weatherDao.value
}
