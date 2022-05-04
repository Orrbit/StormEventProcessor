package org.ndl.models

final case class RadarLocation(
    stationId: String,
    stationLat: Float,
    stationLon: Float
  )

object RadarLocation {
  def apply(raw: RawRadarLocation): RadarLocation = {
    val latStr :: lonStr :: _ = raw.lat_long.split("/").map(_.trim()).toList
    RadarLocation(
      raw.station_id,
      degMinSecToFloat(
        latStr.substring(0, 2).toInt,
        latStr.substring(2, 4).toInt,
        latStr.substring(4, 6).toInt
      ),
      -degMinSecToFloat( //negate since all coordinates are in W hemisphere
        lonStr.substring(0, 3).toInt,
        lonStr.substring(3, 5).toInt,
        lonStr.substring(5, 7).toInt
      )
    )
  }

  def degMinSecToFloat(
      deg: Int,
      min: Int,
      sec: Int
    ): Float =
    return deg.toFloat + min / 60f + sec / 3600f
}
