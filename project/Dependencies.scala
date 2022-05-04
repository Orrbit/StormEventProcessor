import sbt._

object Dependencies {
  lazy val scalaTest = Seq(
    "org.scalatest" %% "scalatest" % "3.2.11" % "test"    
  )

  val pureconfigVersion = "0.15.0"
  val sparkVersion = "3.2.1"

  lazy val core = Seq(

    // support for typesafe configuration
    "com.github.pureconfig" %% "pureconfig" % pureconfigVersion,

    // spark
    "org.apache.spark" %% "spark-sql" % sparkVersion % Provided, // for submiting spark app as a job to cluster
    // "org.apache.spark" %% "spark-sql" % sparkVersion, // for simple standalone spark app

    // logging
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
    "ch.qos.logback" % "logback-classic" % "1.2.3"

  )
}
