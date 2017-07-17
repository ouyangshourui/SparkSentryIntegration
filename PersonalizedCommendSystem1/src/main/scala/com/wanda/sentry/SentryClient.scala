package com.wanda.sentry
import org.apache.hadoop.security.UserGroupInformation
import org.apache.hadoop.hive.ql.session.SessionState
import org.apache.sentry.service.thrift._
import  org.apache.sentry.binding.hive.conf.HiveAuthzConf
import org.apache.spark.SparkContext


/**
  * Created by ouyangshourui on 17/6/30.
  */
object SentryClient {
  def main(args: Array[String]) {

    //val username = args(0)
    val sc = new SparkContext()

    import org.apache.hadoop.security.UserGroupInformation
    import org.apache.hadoop.hive.ql.session.SessionState
    import org.apache.sentry.service.thrift._
    import  org.apache.sentry.binding.hive.conf.HiveAuthzConf
    import org.apache.spark.SparkContext
    val username = "ourui"
    //UserGroupInformation.getCurrentUser().checkTGTAndReloginFromKeytab()
    val credentials = UserGroupInformation.getCurrentUser().getCredentials();
    val hiveconf= new  org.apache.hadoop.hive.conf.HiveConf
    hiveconf.get("sentry.service.security.mode")
    SessionState.start(hiveconf)  //
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)
    sqlContext.sql("show databases")
    val sd =sqlContext.sql("show databases")
    sd.collect()

    //val hiveAuthzConf = hiveconf.get(HiveAuthzConf.HIVE_SENTRY_CONF_URL)
    //hive.sentry.conf.url
    val hiveAuthzConf = hiveconf.get("hive.sentry.conf.url")
    val newAuthzConf = new HiveAuthzConf(new java.net.URL(hiveAuthzConf))

    val sentryClient = SentryServiceClientFactory.create(newAuthzConf)

   val roles = sentryClient.listUserRoles(username)

    val rolesIter=roles.iterator()
    while(rolesIter.hasNext){
      val role= rolesIter.next()
       println(sentryClient.listAllPrivilegesByRoleName(username,role.getRoleName))

    }
    sentryClient.close()

  }

}
