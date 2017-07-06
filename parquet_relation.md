# 问题描述

执行下面代码：
```
val sqlContext = new org.apache.spark.sql.SQLContext(sc) 
 import sqlContext._  
sqlContext.sql("show databases")
val sd =sqlContext.sql("show databases")
sd.collect()
val ss=sqlContext.sql("select * from idc_infrastructure_db.hdfs_meta limit 10")
```
查看anayled plan
```
scala> ss.queryExecution
res4: org.apache.spark.sql.execution.QueryExecution =
== Parsed Logical Plan ==
'GlobalLimit 10
+- 'LocalLimit 10
   +- 'Project [*]
      +- 'UnresolvedRelation `idc_infrastructure_db`.`hdfs_meta`

== Analyzed Logical Plan ==
path: string, repl: int, modification_time: timestamp, accesstime: timestamp, preferredblocksize: int, blockcount: double, filesize: double, nsquota: int, dsquota: int, permission: string, username: string, groupname: string
GlobalLimit 10
+- LocalLimit 10
   +- Project [path#162, repl#163, modification_time#164, accesstime#165, preferredblocksize#166, blockcount#167, filesize#168, nsquota#169, dsquota#170, permission#171, username#172, groupname#173]
      +- SubqueryAlias hdfs_meta
         +- Relation[path#162,repl#163,modification_time#164,accesstime#165,prefe...
 scala> ss.queryExecution.analyzed
res8: org.apache.spark.sql.catalyst.plans.logical.LogicalPlan =
GlobalLimit 10
+- LocalLimit 10
   +- Project [path#162, repl#163, modification_time#164, accesstime#165, preferredblocksize#166, blockcount#167, filesize#168, nsquota#169, dsquota#170, permission#171, username#172, groupname#173]
      +- SubqueryAlias hdfs_meta
         +- Relation[path#162,repl#163,modification_time#164,accesstime#165,preferredblocksize#166,blockcount#167,filesize#168,nsquota#169,dsquota#170,permission#171,username#172,groupname#173] parquet


scala> ss.queryExecution.analyzed
res8: org.apache.spark.sql.catalyst.plans.logical.LogicalPlan =
GlobalLimit 10
+- LocalLimit 10
   +- Project [path#162, repl#163, modification_time#164, accesstime#165, preferredblocksize#166, blockcount#167, filesize#168, nsquota#169, dsquota#170, permission#171, username#172, groupname#173]
      +- SubqueryAlias hdfs_meta
         +- Relation[path#162,repl#163,modification_time#164,accesstime#165,preferredblocksize#166,blockcount#167,filesize#168,nsquota#169,dsquota#170,permission#171,username#172,groupname#173] parquet
         
```    
里面直接关联了parqurt 的relation

- **问题1: spark 2.1.1 源码 怎么样将hive metarelation 转换为parqurt relation的？**
- **问题2: 哪些hive存储格式会直接转换为fs relation？哪些hive存储格式不会转换?**


