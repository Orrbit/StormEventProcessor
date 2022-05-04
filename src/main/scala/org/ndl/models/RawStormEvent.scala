package org.ndl.models

final case class RawStormEvent(
    begin_date_time: String,
    cz_timezone: String,
    event_id: Long,
    event_type: String,
    begin_lat: String,
    begin_lon: String
  )
