package org.ndl.core

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.BeforeAndAfterAll

class MainCsvReaderTest
    extends AnyWordSpec
       with Matchers
       with BeforeAndAfterAll {
  "haversine" should {
    "return a close estimate" in {
      val actual = MainCsvReader.haversine(
        28.1216f,
        27.7842f,
        -97.9342f,
        -97.5111f
      )

      val expected =
        55.96f //calculated on https://www.gpsvisualizer.com/calculators
      val tolerance = .1f

      actual should equal(expected +- tolerance)
    }
  }
}
