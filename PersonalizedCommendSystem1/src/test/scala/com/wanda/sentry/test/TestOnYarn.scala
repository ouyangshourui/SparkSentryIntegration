package com.wanda.sentry.test
import org.apache.spark.sql.SparkSession

/**
  * Created by ouyangshourui on 17/7/17.
  */
object TestOnYarn {

  def main(args: Array[String]) {

    val spark = SparkSession
      .builder()
      .appName("Spark Hive Example")
      .enableHiveSupport()
      .getOrCreate()
    import spark.sql
    println("*************show  databases **********")
    sql("show databases").collect()
    println("*************end  databases **********")
    val ss1=sql("select * from idc_infrastructure_db.hdfs_meta limit 10")
    ss1.collect()
    val ss2=sql("select * from wifi_ffan.dw_portallog limit 10")
    ss2.collect()
  }

}
