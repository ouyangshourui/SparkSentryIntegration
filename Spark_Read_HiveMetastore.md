 #### 1、HiveExternalCatalog 是使用hive的catalog的的一个实现，需要测试一下spark2.1.0 是否使用该方法

####  2、 HiveSharedState 的确是用了hive  external catalog 
    代码如下：
    override lazy val externalCatalog = new HiveExternalCatalog(metadataHive,sparkContext.hadoopConfiguration)
 所有session是用同一个session。
 ```
/**
 * A class that holds all state shared across sessions in a given
 * [[org.apache.spark.sql.SparkSession]] backed by Hive.
  所有的sessions之间共享的hive 状态，主要是共享一个Hive client 和一个 hive 为基础的catalog
 */
private[hive] class HiveSharedState(override val sparkContext: SparkContext)
  extends SharedState(sparkContext) {

  // TODO: just share the IsolatedClientLoader instead of the client instance itself

  /**
   * A Hive client used to interact with the metastore.
   */
  // This needs to be a lazy val at here because TestHiveSharedState is overriding it.
  lazy val metadataHive: HiveClient = {
    HiveUtils.newClientForMetadata(sparkContext.conf, sparkContext.hadoopConfiguration)
  }

  /**
   * A catalog that interacts with the Hive metastore.
   */
  override lazy val externalCatalog =
    new HiveExternalCatalog(metadataHive, sparkContext.hadoopConfiguration)
}
```
####  3、在spark 里面list database 其实就是是用HiveExternalCatalog进行操作

 比如 list database

override def listDatabases(): Seq[String] = withClient {
    client.listDatabases("*")
  }

这里面就需要添加过滤条件

#### 4、  hive  MetaStoreFilterHook 提供了 metastore的 hook 钩子

在sentry 里面：
     SentryMetaStoreFilterHook 使用了这个方法过滤 metastore



 #### 5、 但是hive metastoreFilterHook 是和HiveAuthzBinding绑定到一起的



#### 6、 现在需要把HiveAuthzBinding 调试通，如果没有记错的话是 hive session 没有共享 

    使用HiveAuthzBinding 既可以
    
#### 7、hive.sentry.subject.name 的作用？这个参数是怎么传递过去的？
调试代码的时候发现，spark调用hive的时候，没有将hive.sentry.subject.name 传递过去，导致下面问题
SentryMetaStoreFilterHook 类下面两个类返回异常：
```
/**
   * Invoke Hive table filtering that removes the entries which use has no
   * privileges to access
   * @param tabList
   * @return
   * @throws MetaException
   */
  private List<String> filterTab(String dbName, List<String> tabList) {
    try {
      return HiveAuthzBindingHook.filterShowTables(getHiveAuthzBinding(),
          tabList, HiveOperation.SHOWTABLES, getUserName(), dbName);
    } catch (Exception e) {
      LOG.warn("Error getting Table list ", e);
      return new ArrayList<String>();
    } finally {
      close();
    }
  }

  private String getUserName() {
    return getConf().get(HiveAuthzConf.HIVE_SENTRY_SUBJECT_NAME);
  }
  
```


可以在HiveAuthzBindingSessionHook.java 中发现
```
 // set user name
    sessionConf.set(HiveAuthzConf.HIVE_SENTRY_SUBJECT_NAME, sessionHookContext.getSessionUser());
```
上面这个类是hivesession的钩子，主要是在hiveserver2 使用的，在spark 调用hive的的时候没有使用该类，我们查看cdh hiverserver的配置参数
```
<property>
    <name>hive.server2.session.hook</name>
    <value>org.apache.sentry.binding.hive.HiveAuthzBindingSessionHook</value>
  </property>
```

#### 8、submit app to a designated yarn queue  inspark thrift-server 

HiveSessionImpl
```
    try {
      // In non-impersonation mode, map scheduler queue to current user
      // if fair scheduler is configured.
      if (! hiveConf.getBoolVar(ConfVars.HIVE_SERVER2_ENABLE_DOAS) &&
        hiveConf.getBoolVar(ConfVars.HIVE_SERVER2_MAP_FAIR_SCHEDULER_QUEUE)) {
        ShimLoader.getHadoopShims().refreshDefaultQueue(hiveConf, username);
      }
    } catch (IOException e) {
      LOG.warn("Error setting scheduler queue: " + e, e);
    }
```
#### 9、怎么在spark 里面根据subject name 和 fair-site.xml 文件获取需要对应的queue name
 
 ```
 Using Scala version 2.11.8 (Java HotSpot(TM) 64-Bit Server VM, Java 1.8.0_60)
Type in expressions to have them evaluated.
Type :help for more information.

scala> val ShimLoaderClass = Class.forName("org.apache.hadoop.hive.shims.ShimLoader")
ShimLoaderClass: Class[_] = class org.apache.hadoop.hive.shims.ShimLoader

scala>       val getHadoopShimsMethod = ShimLoaderClass.getMethod("getHadoopShims")
getHadoopShimsMethod: java.lang.reflect.Method = public static synchronized org.apache.hadoop.hive.shims.HadoopShims org.apache.hadoop.hive.shims.ShimLoader.getHadoopShims()

scala>       val hadoopShims = getHadoopShimsMethod.invoke(null)
hadoopShims: Object = org.apache.hadoop.hive.shims.Hadoop23Shims@64b0ec4a

scala>       val stringclass = Class.forName("java.lang.String")
stringclass: Class[_] = class java.lang.String

scala>       val confclass = Class.forName("org.apache.hadoop.conf.Configuration")
confclass: Class[_] = class org.apache.hadoop.conf.Configuration

scala>       val refreshDefaultQueueMethod = hadoopShims.getClass.
     |         getMethod("refreshDefaultQueue", confclass, stringclass)
refreshDefaultQueueMethod: java.lang.reflect.Method = public void org.apache.hadoop.hive.shims.Hadoop23Shims.refreshDefaultQueue(org.apache.hadoop.conf.Configuration,java.lang.String) throws java.io.IOException

scala>       refreshDefaultQueueMethod.invoke(hadoopShims, sc.hadoopConfiguration , System.getProperty("hive.sentry.subject.name"))
res0: Object = null

scala> sc.hadoopConfiguration.get("mapreduce.job.queuename")
res1: String = root.idc_analysis_group

 ```


#### hook 资料：http://dharmeshkakadia.github.io/hive-hook/



#### 10、  logical plan  Rule  出现了最大迭代次数 
```
17/07/27 15:16:58 WARN HiveSessionState$$anon$1: Max iterations (100) reached for batch Resolution
17/07/27 15:16:58 INFO TableScanOperator: 0 finished. closing... 
17/07/27 15:16:58 INFO SelectOperator: 1 finished. closing... 
17/07/27 15:16:58 INFO LimitOperator: 2 finished. closing... 
17/07/27 15:16:58 INFO ListSinkOperator: 4 finished. closing... 
17/07/27 15:16:58 INFO ListSinkOperator: 4 Close done
17/07/27 15:16:58 INFO LimitOperator: 2 Close done
17/07/27 15:16:58 INFO SelectOperator: 1 Close done
17/07/27 15:16:58 INFO TableScanOperator: 0 Close done
17/07/27 15:16:58 INFO PerfLogger: <PERFLOG method=releaseLocks from=org.apache.hadoop.hive.ql.Driver>
17/07/27 15:16:58 INFO PerfLogger: </PERFLOG method=releaseLocks start=1501139818497 end=1501139818497 duration=0 from=org.apache.hadoop.hive.ql.Driver>
17/07/27 15:16:58 INFO HiveMetastoreCatalog$SentryPermissionCheck: SentryPermissionCheck select  table
17/07/27 15:16:58 INFO HiveClientImpl: Running hiveql 'select * from idc_infrastructure_db.spark_table_test1 limit 1'
17/07/27 15:16:58 INFO HiveClientImpl: *********** runHiveCompile hive.sentry.subject.name:ourui
```


下面代码
```
lazy val batches: Seq[Batch] = Seq(
    Batch("Substitution", fixedPoint,
      CTESubstitution,
      WindowsSubstitution,
      EliminateUnions,
      new SubstituteUnresolvedOrdinals(conf)),
    Batch("Resolution", **fixedPoint**,  // 默认值是100，导致额外添加的规则需要允许100次
      ResolveTableValuedFunctions ::
      ResolveRelations ::
      ResolveReferences ::
      ResolveCreateNamedStruct ::
      ResolveDeserializer ::
      ResolveNewInstance ::
      ResolveUpCast ::
      ResolveGroupingAnalytics ::
      ResolvePivot ::
      ResolveOrdinalInOrderByAndGroupBy ::
      ResolveMissingReferences ::
      ExtractGenerator ::
      ResolveGenerate ::
      ResolveFunctions ::
      ResolveAliases ::
      ResolveSubquery ::
      ResolveWindowOrder ::
      ResolveWindowFrame ::
      ResolveNaturalAndUsingJoin ::
      ExtractWindowExpressions ::
      GlobalAggregates ::
      ResolveAggregateFunctions ::
      TimeWindowing ::
      ResolveInlineTables ::
      TypeCoercion.typeCoercionRules ++
      extendedResolutionRules : _*),  // 这是一个bug，可以提交给社区
      Batch("Nondeterministic", Once,
      PullOutNondeterministic),
    Batch("UDF", Once,
      HandleNullInputsForUDF),
    Batch("FixNullability", Once,
      FixNullability),
    Batch("Cleanup", fixedPoint,
      CleanupAliases)
  )
```

使用了map记住已经检查的权限
```
  /**
    * when spark with sentry, we nend to check premission
    */
  object SentryPermissionCheck extends Rule[LogicalPlan] {
    // avoid multi check read and write permission
    var priMap = new java.util.HashMap[String, Boolean]
    val hiveclient = sparkSession.sharedState.externalCatalog
      .asInstanceOf[HiveExternalCatalog].client
    val sentryEabled = sparkSession.sharedState.sparkContext.conf
                       .get("spark.sentry.enabled", "false").toBoolean

    override def apply(plan: LogicalPlan): LogicalPlan = {
      if (!plan.resolved || plan.analyzed) {
        return plan
      }
      plan transformUp {
        // Write path
        case InsertIntoTable(r: MetastoreRelation, partition, child, overwrite, ifNotExists)
          if (sentryEabled) =>
          val tablename = r.databaseName + "." + r.tableName
          val sql = String.format("insert overwrite table %s select * from %s limit 1"
            , tablename, tablename)
          val v = tablename + "InsertIntoTable"
          if (this.priMap.containsKey(v) && this.priMap.get(v)) {
            InsertIntoTable(r, partition, child, overwrite, ifNotExists)
          } else {
            logInfo("SentryPermissionCheck InsertIntoTable")
            this.priMap.put(v, this.hiveclient.runHiveCompile(sql))
            InsertIntoTable(r, partition, child, overwrite, ifNotExists)
          }

        // Read path
        case relation: MetastoreRelation  if (sentryEabled) =>
          val tablename = relation.databaseName + "." + relation.tableName
          val v = tablename + "read path"
          val sql = String.format("select * from %s limit 1", tablename)
          if (this.priMap.containsKey(v) && this.priMap.get(v)) {
            SubqueryAlias(relation.tableName, relation, None)
          } else {
            logInfo("SentryPermissionCheck read table")
            this.priMap.put(v, this.hiveclient.runHiveCompile(sql))
            SubqueryAlias(relation.tableName, relation, None)
          }
      }
    }
  }
```

#### 11、dataset as table问题
```
scala> val ds =  spark.sql("select * from idc_infrastructure_db.spark_table_test1")
17/07/28 09:26:12 WARN HiveSessionState$$anon$1: Max iterations (100) reached for batch Resolution
ds: org.apache.spark.sql.DataFrame = [id: string, name: string]

scala>     ds.write.saveAsTable("idc_infrastructure_db.spark_table_test3")
org.apache.spark.sql.AnalysisException: Table `idc_infrastructure_db`.`spark_table_test3` already exists.;
  at org.apache.spark.sql.DataFrameWriter.saveAsTable(DataFrameWriter.scala:373)
  at org.apache.spark.sql.DataFrameWriter.saveAsTable(DataFrameWriter.scala:358)
  ... 48 elided
```
