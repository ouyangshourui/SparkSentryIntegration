
# 1、yarn summit queue 问题(已经解决)

下面是可以的
```
bin/spark-submit --class com.wanda.sentry.test.TestOnYarnOld \
                       --conf spark.yarn.queue=root.idc_analysis_group \
                        --master yarn-cluster \
                         ../test/ppcs.streaming-1.0-SNAPSHOT-jar-with-dependencies.jar 
```
但是使用  --queue 是不可以的

bin/spark-submit --class com.wanda.sentry.test.TestOnYarnOld --queue root.idc_analysis_group --master yarn-cluster ../test/ppcs.streaming-1.0-SNAPSHOT-jar-with-dependencies.jar

是可以的

# 2、save as rdd 到 使用 sentry url 权限问题


# 3、basic  test
