 #### 1、HiveExternalCatalog 是使用hive的catalog的的一个实现，需要测试一下spark2.1.0 是否使用该方法

###  2、 HiveSharedState 的确是用了hive  external catalog 
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
###  3、在spark 里面list database 其实就是是用HiveExternalCatalog进行操作

 比如 list database

override def listDatabases(): Seq[String] = withClient {
    client.listDatabases("*")
  }

这里面就需要添加过滤条件

### 4、  hive  MetaStoreFilterHook 提供了 metastore的 hook 钩子

在sentry 里面：
     SentryMetaStoreFilterHook 使用了这个方法过滤 metastore



 ### 5、 但是hive metastoreFilterHook 是和HiveAuthzBinding绑定到一起的



### 6、 现在需要把HiveAuthzBinding 调试通，如果没有记错的话是 hive session 没有共享 

    使用HiveAuthzBinding 既可以
    
### 7、hive.sentry.subject.name 的作用？这个参数是怎么传递过去的？
调试代码的时候发现，spark调用hive的时候，没有将hive.sentry.subject.name 传递过去，导致下面问题
SentryHiveMetaStoreClient 类下面两个类返回异常：
```
/**
   * Invoke Hive database filtering that removes the entries which use has no
   * privileges to access
   * 
   * @param dbList
   * @return
   * @throws MetaException
   */
  private List<String> filterDatabases(List<String> dbList)
      throws MetaException {
    try {
      return HiveAuthzBindingHook.filterShowDatabases(getHiveAuthzBinding(),
          dbList, HiveOperation.SHOWDATABASES, getUserName());
    } catch (SemanticException e) {
      throw new MetaException("Error getting DB list " + e.getMessage());
    }
  }

  /**
   * Invoke Hive table filtering that removes the entries which use has no
   * privileges to access
   * 
   * @param dbList
   * @return
   * @throws MetaException
   */
  private List<String> filterTables(String dbName, List<String> tabList)
      throws MetaException {
    try {
      return HiveAuthzBindingHook.filterShowTables(getHiveAuthzBinding(),
          tabList, HiveOperation.SHOWTABLES, getUserName(), dbName);
    } catch (SemanticException e) {
      throw new MetaException("Error getting Table list " + e.getMessage());
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


### hook 资料：http://dharmeshkakadia.github.io/hive-hook/
