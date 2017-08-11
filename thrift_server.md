# kerberos
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
# 
# 
#
