package org.ndl.core

import org.ndl.infrastructure.{ ConfigUtils, SparkUtils }
import org.ndl.models._

import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.sql.SparkSession
import pureconfig.generic.auto._
import org.apache.spark.sql.{ DataFrame, Dataset }
import org.apache.spark.sql.functions.udf

import scala.math.{ cos, pow, sin, sqrt }

case class StormRadarLocationJobConfig(
    stormLocationFile: String,
    radarLocationFile: String,
    outputResultFolder: String,
    maxAcceptedDistance: Int
  )

object MainCsvReader extends LazyLogging {

  def main(args: Array[String]): Unit = {
    logger.info("XXXX: Starting Storm Radar Locations SparkApp")
    implicit val appSettings =
      ConfigUtils.loadAppConfig[StormRadarLocationJobConfig](
        "org.ndl.core.storm-radar-locations"
      )

    logger.info(s"Loaded settings: $appSettings")

    val spark = SparkUtils.sparkSession(
      "spark-storm-radar-locations",
      "local[*]"
    )

    spark.udf.register("haversine", haversine)

    runJob(spark)
    spark.stop()
    logger.info("XXXX: Stopping Storm Radar Locations SparkApp")

  }

  def runJob(
      spark: SparkSession
    )(implicit
      conf: StormRadarLocationJobConfig
    ): Unit = {

    val stormEventDs: Dataset[RawStormEvent] = loadRawStormEvents(spark)
    val parsedEvents: Dataset[StormEvent] = parseRawStormEvents(stormEventDs)

    val radarLoc: Dataset[RawRadarLocation] = loadRawRadarLocation(spark)
    val parsedRadar: Dataset[RadarLocation] = parseRadarLocations(radarLoc)

    val eventsWithClosestRadar: DataFrame =
      joinWithClosestRadarStation(parsedEvents, parsedRadar)

    saveDataframeToCSV(eventsWithClosestRadar)
  }

  def loadRawStormEvents(
      spark: SparkSession
    )(implicit
      conf: StormRadarLocationJobConfig
    ): Dataset[RawStormEvent] = {
    import spark.implicits._
    spark
      .read
      .format("csv")
      .option("header", "true")
      .option("inferSchema", "true")
      .load(conf.stormLocationFile)
      .as[RawStormEvent]
  }

  def parseRawStormEvents(
      rawDs: Dataset[RawStormEvent]
    ): Dataset[StormEvent] = {
    import rawDs.sparkSession.implicits._
    rawDs
      .filter(r => r.begin_lat != null && r.begin_lon != null)
      .map(StormEvent(_))
      .flatMap(sopt =>
        sopt match {
          case Some(value) => Seq(value)
          case None        => Seq()
        }
      )
  }

  def loadRawRadarLocation(
      spark: SparkSession
    )(implicit
      conf: StormRadarLocationJobConfig
    ): Dataset[RawRadarLocation] = {
    import spark.implicits._
    spark
      .read
      .format("csv")
      .option("header", "true")
      .option("inferSchema", "true")
      .load(conf.radarLocationFile)
      .as[RawRadarLocation]
  }

  def parseRadarLocations(
      rawDs: Dataset[RawRadarLocation]
    ): Dataset[RadarLocation] = {
    import rawDs.sparkSession.implicits._
    rawDs
      .map(RadarLocation(_))
  }

  def joinWithClosestRadarStation(
      events: Dataset[StormEvent],
      radars: Dataset[RadarLocation]
    )(implicit
      conf: StormRadarLocationJobConfig
    ): DataFrame = {
    import org.apache.spark.sql.functions.col

    val largestDistance = conf.maxAcceptedDistance
    val haversineUdf = udf(haversine)

    val df = events
      .crossJoin(radars)
      .withColumn(
        "distance",
        haversineUdf(
          col("latitude"),
          col("stationLat"),
          col("longitude"),
          col("stationLon")
        )
      )

    df.createOrReplaceTempView("allEventRadarCombinations")

    df.sparkSession
      .sql(s"""SELECT 
                A.beginYear,
                A.beginMonth,
                A.beginDay, 
                A.beginHour, 
                A.beginMinute, 
                A.eventId, 
                A.eventType, 
                A.latitude, 
                A.longitude,
                A.stationId
                FROM allEventRadarCombinations A
                WHERE A.distance = (SELECT MIN(B.distance) FROM allEventRadarCombinations B WHERE A.eventId = B.eventId) AND A.distance < ${largestDistance}
                ORDER BY A.beginYear, A.beginMonth, A.beginDay, A.beginHour, A.beginMinute""")
  }

  def saveDataframeToCSV(
      df: DataFrame
    )(implicit
      conf: StormRadarLocationJobConfig
    ): Unit =
    df.coalesce(1)
      .write
      .format("csv")
      .option("header", "true")
      .mode("overwrite")
      .save(conf.outputResultFolder)

  //This function takes in two pairs of coordinates and returns an
  //estimated distance between the two coordinates on the globe. The
  //estimate assumes a radius of 6,378 km of Earth.
  //Returned distance is in Kilometers    
  val haversine = (lat1: Float, lat2: Float, lon1: Float, lon2: Float) => {
    val radLat1 = lat1.toRadians
    val radLat2 = lat2.toRadians
    val radLon1 = lon1.toRadians
    val radLon2 = lon2.toRadians
    val inter = pow(sin((radLat2 - radLat1) / 2), 2) + cos(radLat1) * cos(
      radLat2
    ) * pow(sin((radLon2 - radLon1) / 2), 2)
    2 * 6378 * sqrt(inter).toFloat
  }

}
