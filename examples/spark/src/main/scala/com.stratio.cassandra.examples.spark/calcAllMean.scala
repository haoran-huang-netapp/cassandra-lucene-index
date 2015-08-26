package com.stratio.cassandra.examples.spark

/* SimpleApp.scala */


import com.datastax.spark.connector._
import org.apache.spark.{SparkConf, SparkContext}


object calcAllMean {
  def main(args: Array[String]) {

    val KEYSPACE: String = "spark_example_keyspace"
    val TABLE: String = "sensors"

    var totalMean = 0.0f

    val sc : SparkContext = new SparkContext(new SparkConf)
    sc.addJar("/home/example/spark-2.1.8.4-SNAPSHOT.jar")
    val tempRdd=sc.cassandraTable(KEYSPACE, TABLE).select("temp_value").map[Float]((row)=>row.getFloat("temp_value"))

    val totalNumElems: Long =tempRdd.count()

    if (totalNumElems>0) {
      val pairTempRdd = tempRdd.map(s => (1, s))
      val totalTempPairRdd = pairTempRdd.reduceByKey((a, b) => a + b)
      totalMean = totalTempPairRdd.first()._2 / totalNumElems.toFloat
    }

    println("Mean calculated on all data, mean: "+totalMean.toString +" numRows: "+ totalNumElems.toString)
  }
}