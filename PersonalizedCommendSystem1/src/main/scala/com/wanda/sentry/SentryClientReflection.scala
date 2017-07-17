package com.wanda.sentry
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


       /**
         * val roles = sentryClient.listUserRoles(username)

    val rolesIter=roles.iterator()
    while(rolesIter.hasNext){
      val role= rolesIter.next()
       println(sentryClient.listAllPrivilegesByRoleName(username,role.getRoleName))

    }
    sentryClient.close()
         */

      /**
       val roles=listUserRolesMethod.invoke(sentryClient,"wumei10").asInstanceOf[java.util.Set]
       val TSentryRoleClass=Class.forName("org.apache.sentry.provider.db.service.thrift.TSentryRole")
       val TSentryRoleConstructor=TSentryRoleClass.getConstructor()
       val rolesIter=roles.iterator()
       while(rolesIter.hasNext){
         val role= rolesIter.next().asInstanceOf[TSentryRoleConstructor.type]
       }


**/


     }
}
