package com.mrmrmr.kafka

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.{col, from_json}
import org.apache.spark.sql.types.{DoubleType, IntegerType, StringType, StructType}

object CollectFromKafkaToObjects {
  def collectToHotelWithWeather(session: SparkSession): DataFrame = {
    val df = session
      .read
      .format("kafka")
      .option("kafka.bootstrap.servers", "sandbox-hdp.hortonworks.com:6667")
      .option("subscribe", "test")
      .option("startingOffsets", "earliest")
      .load()

    import session.implicits._

    val values = df.selectExpr("CAST(value as string) as value")
    values.printSchema()

    val hotelWithWeatherSchema = new StructType()
      .add("Id", StringType)
      .add("Name", StringType)
      .add("Country", StringType)
      .add("City", StringType)
      .add("Address", StringType)
      .add("Latitude", StringType)
      .add("Longitude", StringType)
      .add("GeoHash", StringType)
      .add("year", IntegerType)
      .add("month", IntegerType)
      .add("day", IntegerType)
      .add("avg_tmpr_c", DoubleType)
      .add("lng", DoubleType)
      .add("lat", DoubleType)
      .add("Precision", IntegerType)

    values
      .drop(col("key"))
      .select(from_json(col("value"), hotelWithWeatherSchema).as("data"))
      .select("data.*")
  }
}


case class HotelWithWeatherOption(Id: Option[String],
                            Name: Option[String],
                            Country: Option[String],
                            City: Option[String],
                            Address: Option[String],
                            Longitude: Option[String],
                            Latitude : Option[String],
                            GeoHash: Option[String],
                            Year: Option[Int],
                            Month: Option[Int],
                            Day: Option[Int],
                            avg_tmpr_c: Option[Double],
                            lng: Option[Double],
                            lat: Option[Double],
                            Precision: Option[Int])