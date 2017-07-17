package com.wanda.sentry.test

import org.apache.hadoop.hive.ql.session.SessionState
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession

/**
  * Created by ouyangshourui on 17/7/17.
  */
object TestOnYarnOld {

  def main(args: Array[String]) {
    val hiveconf= new  org.apache.hadoop.hive.conf.HiveConf
    val url = Thread.currentThread.getContextClassLoader.getResource("sentry-site.xml")
    hiveconf.set("hive.sentry.conf.url", url.toString)
    SessionState.start(hiveconf)
    val sc = new SparkContext()
    val sqlContext = new org.apache.spark.sql.hive.HiveContext(sc)
    println("*************show  databases **********")
    sqlContext.sql("show databases").collect()
    println("*************end  databases **********")
    //val ss1=sqlContext.sql("select * from idc_infrastructure_db.hdfs_meta limit 10")
    //ss1.collect()
   // val ss2=sqlContext.sql("select * from wifi_ffan.dw_portallog limit 10")
   // ss2.collect()
  }

}
