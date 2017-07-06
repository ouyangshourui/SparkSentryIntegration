# 1、问题描述

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

#  2、spark 2.1.1 源码 怎么样将hive metarelation 转换为parqurt relation的？

为了分析这个问题，我看可以看一下hive sql analyed  环节所有的rules
```
  /**
   * An analyzer that uses the Hive metastore.
   */
  override lazy val analyzer: Analyzer = {
    new Analyzer(catalog, conf) {
      override val extendedResolutionRules =
        catalog.ParquetConversions ::
        catalog.OrcConversions ::
        AnalyzeCreateTable(sparkSession) ::
        PreprocessTableInsertion(conf) ::
        DataSourceAnalysis(conf) ::
        (if (conf.runSQLonFile) new ResolveDataSource(sparkSession) :: Nil else Nil)

      override val extendedCheckRules = Seq(PreWriteCheck(conf, catalog))
    }
  }
```
可以重点关注：
- catalog.ParquetConversions 
- catalog.OrcConversions

## 2.1 ParquetConversions  
```
/**
   * When scanning or writing to non-partitioned Metastore Parquet tables, convert them to Parquet
   * data source relations for better performance.
   */
  object ParquetConversions extends Rule[LogicalPlan] {
    // 判断是否需要转换metastore 为parquet
    private def shouldConvertMetastoreParquet(relation: MetastoreRelation): Boolean = {
      relation.tableDesc.getSerdeClassName.toLowerCase.contains("parquet") &&
      //主要是看relation 里面的table描述 反序列化后是否包含parquet
       sessionState.convertMetastoreParquet
    }
    //  将hive metastoreRelation 转换为新的relation
    private def convertToParquetRelation(relation: MetastoreRelation): LogicalRelation = {
      val defaultSource = new ParquetFileFormat()
      val fileFormatClass = classOf[ParquetFileFormat]

      val mergeSchema = sessionState.convertMetastoreParquetWithSchemaMerging
      val options = Map(ParquetOptions.MERGE_SCHEMA -> mergeSchema.toString)

      convertToLogicalRelation(relation, options, defaultSource, fileFormatClass, "parquet")
    }

    override def apply(plan: LogicalPlan): LogicalPlan = {
      if (!plan.resolved || plan.analyzed) {
        return plan
      }

      plan transformUp {
        // Write path 写数据路径
        case InsertIntoTable(r: MetastoreRelation, partition, child, overwrite, ifNotExists)
          // Inserting into partitioned table is not supported in Parquet data source (yet).
          if !r.hiveQlTable.isPartitioned && shouldConvertMetastoreParquet(r) =>
          InsertIntoTable(convertToParquetRelation(r), partition, child, overwrite, ifNotExists)

        // Read path 读数据路径
        case relation: MetastoreRelation if shouldConvertMetastoreParquet(relation) =>
          val parquetRelation = convertToParquetRelation(relation)
          SubqueryAlias(relation.tableName, parquetRelation, None)
      }
    }
  }

```
## 2.2 OrcConversions 
和ParquertConversions类似

# 3.哪些hive存储格式会直接转换为fs relation？哪些hive存储格式不会转换?
Parquert 和ORC 为转换为普通文件 relation，其他的走hive metastore relation。
下面我们测试一下 hive 存储格式为text的格式的analyzed logicplan
```
hive> show create table tmp.zsl_15to20;
OK
CREATE TABLE `zsl_15to20`(
  `mobnum` string)
ROW FORMAT SERDE 
  'org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe' 
STORED AS INPUTFORMAT 
  'org.apache.hadoop.mapred.TextInputFormat' 
OUTPUTFORMAT 
  'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'
LOCATION
  'hdfs://nn-idc/user/hive/warehouse/tmp.db/zsl_15to20'
TBLPROPERTIES (
  'COLUMN_STATS_ACCURATE'='true', 
  'numFiles'='1', 
  'numRows'='25', 
  'rawDataSize'='275', 
  'totalSize'='285', 
  'transient_lastDdlTime'='1499227424')
Time taken: 0.068 seconds, Fetched: 17 row(s)
```
在spark shell 里面查看
```
val sqlContext = new org.apache.spark.sql.SQLContext(sc) 
 import sqlContext._  
sqlContext.sql("show databases")
val sd =sqlContext.sql("show databases")
sd.collect()
val ss=sqlContext.sql("select * from tmp.zsl_15to20 limit 1")
 ss.queryExecution.optimizedPlan
ss.queryExecution.analyzed

res3: org.apache.spark.sql.catalyst.plans.logical.LogicalPlan =
GlobalLimit 1
+- LocalLimit 1
   +- Project [mobnum#8]
      +- MetastoreRelation tmp, zsl_15to20

scala> ss.queryExecution
== Parsed Logical Plan ==
'GlobalLimit 1
+- 'LocalLimit 1
   +- 'Project [*]
      +- 'UnresolvedRelation `tmp`.`zsl_15to20`

== Analyzed Logical Plan ==
mobnum: string
GlobalLimit 1
+- LocalLimit 1
   +- Project [mobnum#8]
      +- MetastoreRelation tmp, zsl_15to20

== Optimized Logical Plan ==
GlobalLimit 1
+- LocalLimit 1
   +- MetastoreRelation tmp, zsl_15to20

== Physical Plan ==
CollectLimit 1
+- HiveTableScan [mobnum#8], MetastoreRelation tmp, zsl_15to20


```

