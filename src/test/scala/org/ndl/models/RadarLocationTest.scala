package org.ndl.models

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.BeforeAndAfterAll

class RadarLocationTest
    extends AnyWordSpec
       with Matchers
       with BeforeAndAfterAll {
  "apply" should {
    "Correctly parse degree minute second" in {
      val rl = RawRadarLocation(
        "KABR",
        "452721 / 0982447"
      )
      val radarlocation = RadarLocation(rl)

      radarlocation.stationLat should equal(45.4558f +- .0001f)
      radarlocation.stationLon should equal(-98.4130f +- .0001f)
    }
  }
}
