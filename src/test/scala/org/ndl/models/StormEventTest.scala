package org.ndl.models

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.BeforeAndAfterAll

class StormEventTest extends AnyWordSpec with Matchers with BeforeAndAfterAll {
  "apply" should {
    "Load some value of valid RawStormEvent" in {
      val rs = RawStormEvent(
        "06-JUL-10 09:51:00",
        "EST-5",
        950756,
        "Thunderstorm Wind",
        "34.127",
        "-78.3541"
      )
      val expected = StormEvent(
        2010,
        7,
        6,
        14,
        51,
        950756,
        "Thunderstorm Wind",
        34.127f,
        -78.3541f
      )

      StormEvent(rs) should be(Some(expected))
    }

    "Reject a Raw Storm Event with empty location details" in {
      val rs = RawStormEvent(
        "06-JUL-10 09:51:00",
        "EST-5",
        950756,
        "Thunderstorm Wind",
        "",
        ""
      )

      StormEvent(rs) should be(None)
    }

    "Reject a Raw Storm Event with null location details" in {
      val rs = RawStormEvent(
        "06-JUL-10 09:51:00",
        "EST-5",
        950756,
        "Thunderstorm Wind",
        null,
        null
      )

      StormEvent(rs) should be(None)
    }

    "Reject a Raw Storm Event with incorrectly formatted date" in {
      val rs = RawStormEvent(
        "06-07-2010 09:51:00",
        "EST-5",
        950756,
        "Thunderstorm Wind",
        "34.127",
        "-78.3541"
      )

      StormEvent(rs) should be(None)
    }

    "Reject a Raw Storm Event with a bad timezone" in {
      val rs = RawStormEvent(
        "06-JUL-10 09:51:00",
        "EST",
        950756,
        "Thunderstorm Wind",
        "34.127",
        "-78.3541"
      )

      StormEvent(rs) should be(None)
    }
  }
}
