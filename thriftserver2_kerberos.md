
# QA:org.apache.hive.service.ServiceException: Unable to login to kerberos with given principal/keytab

```
org.apache.hive.service.ServiceException: Unable to login to kerberos with given principal/keytab
17/08/10 18:55:59 INFO ObjectStore: Initialized ObjectStore
17/08/10 18:56:00 INFO HiveMetaStore: Added public role in metastore
17/08/10 18:56:00 INFO HiveMetaStore: No user is added in admin role, since config is empty
17/08/10 18:56:00 INFO SessionState: Created local directory: /tmp/a7337ac2-e51b-4c00-91a8-c4bad41ab48c_resources
17/08/10 18:56:00 INFO SessionState: Created HDFS directory: /tmp/hive/ganjianling/a7337ac2-e51b-4c00-91a8-c4bad41ab48c
17/08/10 18:56:00 INFO SessionState: Created local directory: /tmp/root/a7337ac2-e51b-4c00-91a8-c4bad41ab48c
17/08/10 18:56:00 INFO SessionState: Created HDFS directory: /tmp/hive/ganjianling/a7337ac2-e51b-4c00-91a8-c4bad41ab48c/_tmp_space.db
17/08/10 18:56:00 INFO HiveClientImpl: Warehouse location for Hive client (version 1.2.1) is /user/hive/warehouse
17/08/10 18:56:00 ERROR HiveThriftServer2: Error starting HiveThriftServer2
org.apache.hive.service.ServiceException: Unable to login to kerberos with given principal/keytab
        at org.apache.spark.sql.hive.thriftserver.SparkSQLCLIService.init(SparkSQLCLIService.scala:58)
        at org.apache.spark.sql.hive.thriftserver.ReflectedCompositeService$$anonfun$initCompositeService$1.apply(SparkSQLCLIService.scala:79)
        at org.apache.spark.sql.hive.thriftserver.ReflectedCompositeService$$anonfun$initCompositeService$1.apply(SparkSQLCLIService.scala:79)
        at scala.collection.Iterator$class.foreach(Iterator.scala:893)
        at scala.collection.AbstractIterator.foreach(Iterator.scala:1336)
        at scala.collection.IterableLike$class.foreach(IterableLike.scala:72)
        at scala.collection.AbstractIterable.foreach(Iterable.scala:54)
        at org.apache.spark.sql.hive.thriftserver.ReflectedCompositeService$class.initCompositeService(SparkSQLCLIService.scala:79)
        at org.apache.spark.sql.hive.thriftserver.HiveThriftServer2.initCompositeService(HiveThriftServer2.scala:272)
        at org.apache.spark.sql.hive.thriftserver.HiveThriftServer2.init(HiveThriftServer2.scala:292)
        at org.apache.spark.sql.hive.thriftserver.HiveThriftServer2$.main(HiveThriftServer2.scala:94)
        at org.apache.spark.sql.hive.thriftserver.HiveThriftServer2.main(HiveThriftServer2.scala)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.lang.reflect.Method.invoke(Method.java:497)
        at org.apache.spark.deploy.SparkSubmit$.org$apache$spark$deploy$SparkSubmit$$runMain(SparkSubmit.scala:744)
        at org.apache.spark.deploy.SparkSubmit$.doRunMain$1(SparkSubmit.scala:187)
        at org.apache.spark.deploy.SparkSubmit$.submit(SparkSubmit.scala:212)
        at org.apache.spark.deploy.SparkSubmit$.main(SparkSubmit.scala:126)
        at org.apache.spark.deploy.SparkSubmit.main(SparkSubmit.scala)

        at org.apache.hadoop.security.UserGroupInformation.loginUserFromKeytab(UserGroupInformation.java:962)
        at org.apache.hive.service.auth.HiveAuthFactory.loginFromKeytab(HiveAuthFactory.java:198)
        at org.apache.spark.sql.hive.thriftserver.SparkSQLCLIService.init(SparkSQLCLIService.scala:53)
        ... 20 more
Caused by: javax.security.auth.login.LoginException: Unable to obtain password from user

        at com.sun.security.auth.module.Krb5LoginModule.promptForPass(Krb5LoginModule.java:897)
        at com.sun.security.auth.module.Krb5LoginModule.attemptAuthentication(Krb5LoginModule.java:760)
        at com.sun.security.auth.module.Krb5LoginModule.login(Krb5LoginModule.java:617)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.lang.reflect.Method.invoke(Method.java:497)
        at javax.security.auth.login.LoginContext.invoke(LoginContext.java:755)
        at javax.security.auth.login.LoginContext.access$000(LoginContext.java:195)
        at javax.security.auth.login.LoginContext$4.run(LoginContext.java:682)
        at javax.security.auth.login.LoginContext$4.run(LoginContext.java:680)
        at java.security.AccessController.doPrivileged(Native Method)
        at javax.security.auth.login.LoginContext.invokePriv(LoginContext.java:680)
        at javax.security.auth.login.LoginContext.login(LoginContext.java:587)
        at org.apache.hadoop.security.UserGroupInformation.loginUserFromKeytab(UserGroupInformation.java:953)
        ... 22 more
17/08/10 18:56:00 INFO SparkUI: Stopped Spark web UI at http://10.199.192.22:9018
```
