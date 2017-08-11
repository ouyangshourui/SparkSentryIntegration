# kerberos

```
[root@lpsllfdrcw1 spark]# kinit -kt /root/spark/spark/conf/hive_lpsllfdrcw1.keytab hive/lpsllfdrcw1.lfidcwanda.cn@LFDC.WANDA-GROUP.NET
[root@lpsllfdrcw1 spark]# klist
Ticket cache: FILE:/tmp/krb5cc_0
Default principal: hive/lpsllfdrcw1.lfidcwanda.cn@LFDC.WANDA-GROUP.NET

Valid starting       Expires              Service principal
08/11/2017 10:39:18  08/12/2017 10:39:18  krbtgt/LFDC.WANDA-GROUP.NET@LFDC.WANDA-GROUP.NET
        renew until 08/16/2017 10:39:18


sbin/start-thriftserver.sh  --master yarn \
 --hiveconf hive.server2.authentication=KERBEROS \
 --hiveconf hive.server2.authentication.kerberos.principal="hive/lpsllfdrcw1.lfidcwanda.cn@LFDC.WANDA-GROUP.NET" \
 --hiveconf hive.server2.authentication.kerberos.keytab="/root/spark/spark/conf/hive_lpsllfdrcw1.keytab"
 #--hiveconf hive.server2.authentication.kerberos.principal="hive/lpsllfdrcw1.lfidcwanda.cn@LFDC.WANDA-GROUP.NET" \
 #--hiveconf hive.server2.thrift.http.port=10081 \
 
#bin/beeline -u "jdbc:hive2://lpsllfdrcw1.lfidcwanda.cn:10083/;principal=hive/lpsllfdrcw1.lfidcwanda.cn@LFDC.WANDA-GROUP.NET"
```
## beeline error
```
[root@lpsllfdrcw1 spark]# bin/beeline -u "jdbc:hive2://lpsllfdrcw1.lfidcwanda.cn:10083/;principal=hive/lpsllfdrcw1.lfidcwanda.cn@LFDC.WANDA-GROUP.NET"
Connecting to jdbc:hive2://lpsllfdrcw1.lfidcwanda.cn:10083/;principal=hive/lpsllfdrcw1.lfidcwanda.cn@LFDC.WANDA-GROUP.NET
17/08/11 10:13:55 INFO Utils: Supplied authorities: lpsllfdrcw1.lfidcwanda.cn:10083
17/08/11 10:13:55 INFO Utils: Resolved authority: lpsllfdrcw1.lfidcwanda.cn:10083
17/08/11 10:13:56 INFO HiveConnection: Will try to open client transport with JDBC Uri: jdbc:hive2://lpsllfdrcw1.lfidcwanda.cn:10083/;principal=hive/lpsllfdrcw1.lfidcwanda.cn@LFDC.WANDA-GROUP.NET
Error: Could not load shims in class org.apache.hadoop.hive.schshim.FairSchedulerShim (state=,code=0)
Beeline version 1.2.1.spark2 by Apache Hive
```
cp /opt/cloudera/parcels/CDH/jars/hive-shims-scheduler-1.1.0-cdh5.7.1.jar  jars/ 

## also meet following exception
```
Exception in thread "HiveServer2-Handler-Pool: Thread-151" java.lang.NoClassDefFoundError: org/apache/hadoop/yarn/server/resourcemanager/scheduler/fair/AllocationFileLoaderService$Listener
        at java.lang.Class.forName0(Native Method)
        at java.lang.Class.forName(Class.java:264)
        at org.apache.hadoop.hive.shims.ShimLoader.createShim(ShimLoader.java:146)
        at org.apache.hadoop.hive.shims.ShimLoader.getSchedulerShims(ShimLoader.java:133)
        at org.apache.hadoop.hive.shims.Hadoop23Shims.refreshDefaultQueue(Hadoop23Shims.java:296)
        at org.apache.hive.service.cli.session.HiveSessionImpl.<init>(HiveSessionImpl.java:109)
        at org.apache.hive.service.cli.session.SessionManager.openSession(SessionManager.java:251)
        at org.apache.spark.sql.hive.thriftserver.SparkSQLSessionManager.openSession(SparkSQLSessionManager.scala:70)
        at org.apache.hive.service.cli.CLIService.openSession(CLIService.java:194)
        at org.apache.hive.service.cli.thrift.ThriftCLIService.getSessionHandle(ThriftCLIService.java:354)
        at org.apache.hive.service.cli.thrift.ThriftCLIService.OpenSession(ThriftCLIService.java:246)
        at org.apache.hive.service.cli.thrift.TCLIService$Processor$OpenSession.getResult(TCLIService.java:1253)
        at org.apache.hive.service.cli.thrift.TCLIService$Processor$OpenSession.getResult(TCLIService.java:1238)
        at org.apache.thrift.ProcessFunction.process(ProcessFunction.java:39)
        at org.apache.thrift.TBaseProcessor.process(TBaseProcessor.java:39)
        at org.apache.hadoop.hive.thrift.HadoopThriftAuthBridge$Server$TUGIAssumingProcessor.process(HadoopThriftAuthBridge.java:692)
        at org.apache.thrift.server.TThreadPoolServer$WorkerProcess.run(TThreadPoolServer.java:285)
        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
        at java.lang.Thread.run(Thread.java:745)
Caused by: java.lang.ClassNotFoundException: org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.AllocationFileLoaderService$Listener
        at java.net.URLClassLoader.findClass(URLClassLoader.java:381)
        at java.lang.ClassLoader.loadClass(ClassLoader.java:424)
        at sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:331)
        at java.lang.ClassLoader.loadClass(ClassLoader.java:357)
        ... 20 more
```
resove method:
```
cp /opt/cloudera/parcels/CDH/jars/hadoop-yarn-server-resourcemanager-2.6.0-cdh5.7.1.jar  jars/
```
# ldap
```
Ticket cache: FILE:/tmp/krb5cc_0
Default principal: hive/lpsllfdrcw1.lfidcwanda.cn@LFDC.WANDA-GROUP.NET

Valid starting       Expires              Service principal
08/11/2017 10:39:18  08/12/2017 10:39:18  krbtgt/LFDC.WANDA-GROUP.NET@LFDC.WANDA-GROUP.NET
        renew until 08/16/2017 10:39:18
        
        
sbin/start-thriftserver.sh  --master yarn \
 --hiveconf hive.server2.thrift.port=10081 \
 --hiveconf hive.server2.authentication=LDAP \
 --hiveconf hive.server2.authentication.ldap.url="ldap://10.199.192.48:389" \
 --hiveconf hive.server2.authentication.ldap.baseDN="ou=People,dc=IDC,dc=WANDA-GROUP,dc=NET" \
--hiveconf hive.server2.authentication.kerberos.principal="hive/lpsllfdrcw1.lfidcwanda.cn@LFDC.WANDA-GROUP.NET" \
 --hiveconf hive.server2.authentication.kerberos.keytab="/root/spark/spark/conf/hive_lpsllfdrcw1.keytab" 
#bin/beeline -u jdbc:hive2://lpsllfdrcw1.lfidcwanda.cn:10081 -n  ganjianling  -p 123456  -d org.apache.hive.jdbc.HiveDriver        
```
# 没有以单独用户执行


#User impersonation in Apache Spark 1.6 Thrift Server

https://community.hortonworks.com/articles/101418/user-impersonation-in-apache-spark-16-thrift-serve.html
```
org.apache.hadoop.hive.ql.parse.SemanticException: org.apache.sentry.binding.hive.conf.InvalidConfigurationException: hive.server2.enable.doAs can't be set to true in non-testing mode
        at org.apache.sentry.binding.hive.HiveAuthzBindingHook.getHiveBindingWithPrivilegeCache(HiveAuthzBindingHook.java:978)
        at org.apache.sentry.binding.hive.HiveAuthzBindingHook.filterShowDatabases(HiveAuthzBindingHook.java:836)
        at org.apache.sentry.binding.metastore.SentryMetaStoreFilterHook.filterDb(SentryMetaStoreFilterHook.java:131)
        at org.apache.sentry.binding.metastore.SentryMetaStoreFilterHook.filterDatabases(SentryMetaStoreFilterHook.java:59)
        at org.apache.hadoop.hive.metastore.HiveMetaStoreClient.getAllDatabases(HiveMetaStoreClient.java:1031)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.lang.reflect.Method.invoke(Method.java:497)
        at org.apache.hadoop.hive.metastore.RetryingMetaStoreClient.invoke(RetryingMetaStoreClient.java:156)
        at com.sun.proxy.$Proxy24.getAllDatabases(Unknown Source)
        at org.apache.hadoop.hive.ql.metadata.Hive.getAllDatabases(Hive.java:1234)
        at org.apache.hadoop.hive.ql.metadata.Hive.reloadFunctions(Hive.java:174)
org.apache.hadoop.hive.ql.parse.SemanticException: org.apache.sentry.binding.hive.conf.InvalidConfigurationException: hive.server2.enable.
doAs can't be set to true in non-testing mode
17/08/11 14:12:09 ERROR HiveAuthzBindingHook: Can not create HiveAuthzBinding with privilege cache.
17/08/11 14:12:09 WARN SentryMetaStoreFilterHook: Error getting DB list 
org.apache.hadoop.hive.ql.parse.SemanticException: org.apache.sentry.binding.hive.conf.InvalidConfigurationException: hive.server2.enable.
doAs can't be set to true in non-testing mode
        at org.apache.sentry.binding.hive.HiveAuthzBindingHook.getHiveBindingWithPrivilegeCache(HiveAuthzBindingHook.java:978)
        at org.apache.sentry.binding.hive.HiveAuthzBindingHook.filterShowDatabases(HiveAuthzBindingHook.java:836)
        at org.apache.sentry.binding.metastore.SentryMetaStoreFilterHook.filterDb(SentryMetaStoreFilterHook.java:131)
        at org.apache.sentry.binding.metastore.SentryMetaStoreFilterHook.filterDatabases(SentryMetaStoreFilterHook.java:59)
        at org.apache.hadoop.hive.metastore.HiveMetaStoreClient.getAllDatabases(HiveMetaStoreClient.java:1031)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.lang.reflect.Method.invoke(Method.java:497)
        at org.apache.hadoop.hive.metastore.RetryingMetaStoreClient.invoke(RetryingMetaStoreClient.java:156)
        at com.sun.proxy.$Proxy24.getAllDatabases(Unknown Source)
        at org.apache.hadoop.hive.ql.metadata.Hive.getAllDatabases(Hive.java:1234)
        at org.apache.hadoop.hive.ql.metadata.Hive.reloadFunctions(Hive.java:174)
        at org.apache.hadoop.hive.ql.metadata.Hive.<clinit>(Hive.java:166)
        at org.apache.hadoop.hive.ql.session.SessionState.start(SessionState.java:503)
        at org.apache.hadoop.hive.ql.session.SessionState.start(SessionState.java:466)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.lang.reflect.Method.invoke(Method.java:497)
        at org.apache.spark.deploy.yarn.security.HiveCredentialProvider$$anonfun$obtainCredentials$1.apply$mcV$sp(HiveCredentialProvider.scala:98)
        at org.apache.spark.deploy.yarn.security.HiveCredentialProvider$$anonfun$obtainCredentials$1.apply(HiveCredentialProvider.scala:90)
        at org.apache.spark.deploy.yarn.security.HiveCredentialProvider$$anonfun$obtainCredentials$1.apply(HiveCredentialProvider.scala:90)
        at org.apache.spark.deploy.yarn.security.HiveCredentialProvider$$anon$1.run(HiveCredentialProvider.scala:131)
        at java.security.AccessController.doPrivileged(Native Method)
        at javax.security.auth.Subject.doAs(Subject.java:422)
        at org.apache.hadoop.security.UserGroupInformation.doAs(UserGroupInformation.java:1693)
        at org.apache.spark.deploy.yarn.security.HiveCredentialProvider.doAsRealUser(HiveCredentialProvider.scala:130)
        at org.apache.spark.deploy.yarn.security.HiveCredentialProvider.obtainCredentials(HiveCredentialProvider.scala:90)
        at org.apache.spark.deploy.yarn.security.ConfigurableCredentialManager$$anonfun$obtainCredentials$2.apply(ConfigurableCredentialManager.scala:82)
        at org.apache.spark.deploy.yarn.security.ConfigurableCredentialManager$$anonfun$obtainCredentials$2.apply(ConfigurableCredentialManager.scala:80)
        at scala.collection.TraversableLike$$anonfun$flatMap$1.apply(TraversableLike.scala:241)
        at scala.collection.TraversableLike$$anonfun$flatMap$1.apply(TraversableLike.scala:241)
        at scala.collection.Iterator$class.foreach(Iterator.scala:893)
        at scala.collection.AbstractIterator.foreach(Iterator.scala:1336)
        at scala.collection.MapLike$DefaultValuesIterable.foreach(MapLike.scala:206)
        at scala.collection.TraversableLike$class.flatMap(TraversableLike.scala:241)
        at scala.collection.AbstractTraversable.flatMap(Traversable.scala:104)
        at org.apache.spark.deploy.yarn.security.ConfigurableCredentialManager.obtainCredentials(ConfigurableCredentialManager.scala:80)
        at org.apache.spark.deploy.yarn.Client.prepareLocalResources(Client.scala:403)
        at org.apache.spark.deploy.yarn.Client.createContainerLaunchContext(Client.scala:885)
        at org.apache.spark.deploy.yarn.Client.submitApplication(Client.scala:171)
        at org.apache.spark.scheduler.cluster.YarnClientSchedulerBackend.start(YarnClientSchedulerBackend.scala:56)
        at org.apache.spark.scheduler.TaskSchedulerImpl.start(TaskSchedulerImpl.scala:156)
        at org.apache.spark.SparkContext.<init>(SparkContext.scala:509)
        at org.apache.spark.SparkContext$.getOrCreate(SparkContext.scala:2320)
        at org.apache.spark.sql.SparkSession$Builder$$anonfun$6.apply(SparkSession.scala:868)
        at org.apache.spark.sql.SparkSession$Builder$$anonfun$6.apply(SparkSession.scala:860)
        at scala.Option.getOrElse(Option.scala:121)
        at org.apache.spark.sql.SparkSession$Builder.getOrCreate(SparkSession.scala:860)
        at org.apache.spark.sql.hive.thriftserver.SparkSQLEnv$.init(SparkSQLEnv.scala:47)
        at org.apache.spark.sql.hive.thriftserver.HiveThriftServer2$.main(HiveThriftServer2.scala:81)
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
Caused by: org.apache.sentry.binding.hive.conf.InvalidConfigurationException: hive.server2.enable.doAs can't be set to true in non-testing mode
        at org.apache.sentry.binding.hive.authz.HiveAuthzBinding.validateHiveServer2Config(HiveAuthzBinding.java:189)
        at org.apache.sentry.binding.hive.authz.HiveAuthzBinding.validateHiveConfig(HiveAuthzBinding.java:148)
        at org.apache.sentry.binding.hive.authz.HiveAuthzBinding.<init>(HiveAuthzBinding.java:96)
        at org.apache.sentry.binding.hive.HiveAuthzBindingHook.getHiveBindingWithPrivilegeCache(HiveAuthzBindingHook.java:974)
        ... 61 more
```
