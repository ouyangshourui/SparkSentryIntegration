


# 1、spark on yarn 上传配置代码
```
/**
   * Create an archive with the config files for distribution.
   *
   * These will be used by AM and executors. The files are zipped and added to the job as an
   * archive, so that YARN will explode it when distributing to AM and executors. This directory
   * is then added to the classpath of AM and executor process, just to make sure that everybody
   * is using the same default config.
   *
   * This follows the order of precedence set by the startup scripts, in which HADOOP_CONF_DIR
   * shows up in the classpath before YARN_CONF_DIR.
   *
   * Currently this makes a shallow copy of the conf directory. If there are cases where a
   * Hadoop config directory contains subdirectories, this code will have to be fixed.
   *
   * The archive also contains some Spark configuration. Namely, it saves the contents of
   * SparkConf in a file to be loaded by the AM process.
   */
  private def createConfArchive(): File = {
    val hadoopConfFiles = new HashMap[String, File]()

    // Uploading $SPARK_CONF_DIR/log4j.properties file to the distributed cache to make sure that
    // the executors will use the latest configurations instead of the default values. This is
    // required when user changes log4j.properties directly to set the log configurations. If
    // configuration file is provided through --files then executors will be taking configurations
    // from --files instead of $SPARK_CONF_DIR/log4j.properties.

    // Also uploading metrics.properties to distributed cache if exists in classpath.
    // If user specify this file using --files then executors will use the one
    // from --files instead.
    for { prop <- Seq("log4j.properties", "metrics.properties")
          url <- Option(Utils.getContextOrSparkClassLoader.getResource(prop))
          if url.getProtocol == "file" } {
      hadoopConfFiles(prop) = new File(url.getPath)
    }

    Seq("HADOOP_CONF_DIR", "YARN_CONF_DIR").foreach { envKey =>
      sys.env.get(envKey).foreach { path =>
        val dir = new File(path)
        if (dir.isDirectory()) {
          val files = dir.listFiles()
          if (files == null) {
            logWarning("Failed to list files under directory " + dir)
          } else {
            files.foreach { file =>
              if (file.isFile && !hadoopConfFiles.contains(file.getName())) {
                hadoopConfFiles(file.getName()) = file
              }
            }
          }
        }
      }
    }

    val confArchive = File.createTempFile(LOCALIZED_CONF_DIR, ".zip",
      new File(Utils.getLocalDir(sparkConf)))
    val confStream = new ZipOutputStream(new FileOutputStream(confArchive))

    try {
      confStream.setLevel(0)
      hadoopConfFiles.foreach { case (name, file) =>
        if (file.canRead()) {
          confStream.putNextEntry(new ZipEntry(name))
          Files.copy(file, confStream)
          confStream.closeEntry()
        }
      }

      // Save Spark configuration to a file in the archive.
      val props = new Properties()
      sparkConf.getAll.foreach { case (k, v) => props.setProperty(k, v) }
      // Override spark.yarn.key to point to the location in distributed cache which will be used
      // by AM.
      Option(amKeytabFileName).foreach { k => props.setProperty(KEYTAB.key, k) }
      confStream.putNextEntry(new ZipEntry(SPARK_CONF_FILE))
      val writer = new OutputStreamWriter(confStream, StandardCharsets.UTF_8)
      props.store(writer, "Spark configuration.")
      writer.flush()
      confStream.closeEntry()
    } finally {
      confStream.close()
    }
    confArchive
  }
```

# 2、sentry 配置文件上传方案
-  放到"HADOOP_CONF_DIR", "YARN_CONF_DIR"目录下面，缺点是会，spark自己维护这个目录
-  放到spark/conf下面，和"log4j.properties", "metrics.properties"一起上传

