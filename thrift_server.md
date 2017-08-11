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
#
