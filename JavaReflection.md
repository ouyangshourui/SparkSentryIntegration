

# orgin code 
 ```
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
 ```
 
 
# scala code  with java reflection 
```
package com.wanda.sentry

import java.net.URL

import org.apache.hadoop.hive.ql.session.SessionState
import org.apache.sentry.service.thrift.SentryServiceClientFactory
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession


/**
  * Created by ouyangshourui on 17/7/4.
  */
object SentryClientReflection {
     def main(args: Array[String]) {
       val spark = SparkSession
         .builder
         .appName("GroupBy Test")
         .getOrCreate()
       import java.net.URL

       import org.apache.hadoop.hive.ql.session.SessionState
       import org.apache.spark.SparkContext
       import org.apache.spark.sql.SparkSession

       val hiveconf= new  org.apache.hadoop.hive.conf.HiveConf

      // SessionState.setCurrentSessionState() spark 里面设置
       SessionState.start(hiveconf)  //
       val hiveAuthzConf = hiveconf.get("hive.sentry.conf.url")
       val cla=Class.forName("org.apache.sentry.binding.hive.conf.HiveAuthzConf")
       val constructor = cla.getConstructor(Class.forName("java.net.URL"))
       val newAuthzConf = constructor.newInstance(new java.net.URL(hiveAuthzConf))
       val SentryServiceClientFactoryClass=Class.forName("org.apache.sentry.service.thrift.SentryServiceClientFactory")
       val sentryServiceClientFactoryConstructor = SentryServiceClientFactoryClass.getDeclaredConstructor()
       sentryServiceClientFactoryConstructor.setAccessible(true)
       val  sentryServiceClientFactory = sentryServiceClientFactoryConstructor.newInstance()


     //  val sentryClient = SentryServiceClientFactory.create(newAuthzConf)
       val createMethod = SentryServiceClientFactoryClass.getMethod("create",Class.forName("org.apache.hadoop.conf.Configuration"))
       //    val sentryClient = SentryServiceClientFactory.create(newAuthzConf)
       val sentryClient=createMethod.invoke(sentryServiceClientFactory,newAuthzConf.asInstanceOf[org.apache.hadoop.conf.Configuration])

       //val roles = sentryClient.listUserRoles(username)
       val stringClass = Class.forName("java.lang.String")
       val listUserRolesMethod = sentryClient.getClass.getMethod("listUserRoles",stringClass)

        listUserRolesMethod.invoke(sentryClient,"wumei10")

     }
}
```

# reference paper
1、http://docs.scala-lang.org/overviews/reflection/overview.html
2、java reflection:http://www.journaldev.com/1789/java-reflection-example-tutorial#get-class-object


 
