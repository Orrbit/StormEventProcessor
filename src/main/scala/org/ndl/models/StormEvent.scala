package org.ndl.models

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.{ DateTimeFormatter, DateTimeFormatterBuilder }
import scala.util.Try
import java.time.temporal.ChronoUnit

final case class StormEvent(
    beginDateTime: String,
    beginYear: Int,
    beginMonth: Int,
    beginDay: Int,
    eventId: Long,
    eventType: String,
    latitude: Float,
    longitude: Float
  )

object StormEvent {
  def apply(raw: RawStormEvent): Option[StormEvent] =
    Try {

      val tzOffset =
        raw.cz_timezone.substring(raw.cz_timezone.lastIndexOf("-") + 1).toInt

      val formatter = new DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .appendPattern("dd-MMM-yy HH:mm:ss")
        .toFormatter

      val beginDateTimeActualTimeZone = LocalDateTime
        .parse(raw.begin_date_time, formatter)

      val utcBeginDateTime =
        beginDateTimeActualTimeZone.plus(tzOffset, ChronoUnit.HOURS)

      val targetFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")
      val beginDateTimeString = utcBeginDateTime.format(targetFormatter)

      StormEvent(
        beginDateTimeString,
        utcBeginDateTime.getYear(),
        utcBeginDateTime.getMonthValue(),
        utcBeginDateTime.getDayOfMonth(),
        raw.event_id,
        raw.event_type,
        raw.begin_lat.toFloat,
        raw.begin_lon.toFloat
      )
    }.toOption
}
