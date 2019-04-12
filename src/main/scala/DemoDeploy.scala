package demodeploy

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._

import org.apache.spark.rdd.RDD


object DemoDeploy {
    /*
    sbt:simple-spark-deploy> run
    [info] Running demodeploy.DemoDeploy 
    Using Spark's default log4j profile: org/apache/spark/log4j-defaults.properties
    */
  def main(args: Array[String]) {
    /*
    // $ ./bin/spark-shell --master local[2]
    Setting default log level to "WARN".
    To adjust logging level 
    use sc.setLogLevel(newLevel). 
    For SparkR, use setLogLevel(newLevel).
    */
    /* 
    $ ./sbin/start-master.sh
starting org.apache.spark.deploy.master.Master, logging to /home/gluk-alex/Documents/projects/turing.com/spark-2.4.1-bin-hadoop2.7/logs/spark-gluk-alex-org.apache.spark.deploy.master.Master-1-glukalex-desktop.out

    http://localhost:8080/
    Spark Master at spark://glukalex-desktop:7077
    
    $ ./sbin/stop-master.sh
stopping org.apache.spark.deploy.master.Master
    */
    val conf = new SparkConf()
        .setAppName("DemoDeploy")
        /*
        `master` is a Spark, Mesos or YARN cluster URL, 
        or a special “local” string 
        to run in local mode. 
        In practice, 
        when running on a cluster, 
        you will not want to hardcode `master` in the program, 
        but rather 
        launch the application with `spark-submit` 
        and receive it there. 
        However, 
        for local testing 
        and unit tests, 
        you can pass “local” to run Spark in-process.
        */
        //.setMaster(master)
        .setMaster("local")
    // org.apache.spark.SparkException: A master URL must be set in your configuration
    val sc = new SparkContext(conf)
    // newLevel
    //sc.setLogLevel("WARN")
    println("====DEMO DEPLOY====")

    val text = List(
        "Hadoop MapReduce, a disk-based big data processing engine, is being replaced by a new generation of memory-based processing frameworks, the most popular of which is Spark.", "Spark supports Scala, Java, Python, and R."
    )
    /*
    Text file RDDs can be created 
    using SparkContext’s textFile method. 
    This method takes an URI for the file 
    (either a local path on the machine, 
    or a hdfs://, s3a://, etc URI) 
    and reads it 
    as a collection of lines. 
    Here is an example invocation:

scala> val distFile = sc.textFile("data.txt")
distFile: org.apache.spark.rdd.RDD[String] = data.txt MapPartitionsRDD[10] at textFile at <console>:26

    Note:
    - All of Spark’s file-based input methods, 
        including textFile, 
        support running on directories, compressed files, and wildcards as well. 
        For example, 
        you can use 
        textFile("/my/directory"), 
        textFile("/my/directory/*.txt"), 
        and textFile("/my/directory/*.gz").
    Apart from text files, 
    Spark’s Scala API also supports several other data formats:

    - SparkContext.wholeTextFiles 
        lets you read a directory 
        containing multiple small text files, 
        and returns each of them as (filename, content) pairs. 
        This is in contrast with textFile, 
        which would return one record per line in each file. 
        Partitioning is determined by data locality 
        which, in some cases, 
        may result in too few partitions. 
        For those cases, 
        wholeTextFiles provides an optional second argument 
        for controlling the minimal number of partitions.
    - RDD.saveAsObjectFile and SparkContext.objectFile support 
        saving an RDD in a simple format 
        consisting of serialized Java objects. 
        While this is not as efficient 
        as specialized formats like Avro, 
        it offers an easy way to save any RDD.
    */
    /*
    Transformations:
    ---
    pipe(command, [envVars])
        Pipe each partition of the RDD 
        through a shell command, 
        e.g. a Perl or bash script. 
        RDD elements are written to the process's stdin 
        and lines output to its stdout 
        are returned as an RDD of strings.
    */
    /*
    scala> val distFile_RDD = sc.textFile("../url_list.csv")
distFile_RDD: org.apache.spark.rdd.RDD[String] = ../url_list.csv MapPartitionsRDD[1] at textFile at <console>:24
    // Actions:
    ---
scala> distFile_RDD.take(1)
res16: Array[String] = Array(URLs)

scala> distFile.collect()
res19: Array[String] = Array(URLs, https://github.com/bitly/data_hacks, https...
scala> distFile.take(2)
res21: Array[String] = Array(URLs, https://github.com/bitly/data_hacks)
scala> distFile.take(2).drop(1)
res22: Array[String] = Array(https://github.com/bitly/data_hacks)
scala> distFile.take(2).drop(1)(0)
res23: String = https://github.com/bitly/data_hacks
scala> distFile.count()
res24: Long = 100001
    */
    val rdd = sc.parallelize(text)
    /*
    val lines = sc.textFile("data.txt")
    val lineLengths = lines.map(s => s.length)
    // If we also wanted to use lineLengths again later, 
    // we could add:
    //lineLengths.persist()
    // Finally, we run reduce, which is an action.
    val totalLength = lineLengths.reduce((a, b) => a + b)
    */
    val counts = rdd
        .flatMap(line => line.split(" "))
        .map(word => (word, 1))
        .reduceByKey(_ + _)
        .collect()
    /*
    To print all elements on the driver, 
    one can use the collect() method 
    to first bring the RDD to the driver node 
    thus: 
        rdd.collect().foreach(println). 
    This can cause the driver 
    to run out of memory, though, 
    because collect() fetches the entire RDD to a single machine; 
    if you only need to print a few elements of the RDD, 
    a safer approach is to use the take(): 
        rdd.take(100).foreach(println).
    */
    counts foreach println
    /*
    val sparkDF = spark
        .read
        .format("csv")
        .option("header", "true")
        .option("inferSchema", "true")
        .load("../url_list.csv")
        //.load("/FileStore/tables/state_income-9f7c5.csv")
    //sparkDF: org.apache.spark.sql.DataFrame = [URLs: string]
scala> val urls = sparkDF("URLs")
urls: org.apache.spark.sql.Column = URLs
scala> sparkDF.collect()
res25: Array[org.apache.spark.sql.Row] = Array([https://github.com/bitly/data_hacks], ...
scala> sparkDF.collect().head
res26: org.apache.spark.sql.Row = [https://github.com/bitly/data_hacks]
scala> sparkDF.first()
res27: org.apache.spark.sql.Row = [https://github.com/bitly/data_hacks]
scala> sparkDF.head(3)
res28: Array[org.apache.spark.sql.Row] = Array([https://github.com/bitly/data_hacks], [https://github.com/kevinburke/hamms], [https://github.com/BugScanTeam/DNSLog])
scala> sparkDF.show(3)
+--------------------+
|                URLs|
+--------------------+
|https://github.co...|
|https://github.co...|
|https://github.co...|
+--------------------+
only showing top 3 rows

scala> sparkDF.select("URLs")
res30: org.apache.spark.sql.DataFrame = [URLs: string]
scala> sparkDF.select("URLs").show(3)
+--------------------+
|                URLs|
+--------------------+
|https://github.co...|
|https://github.co...|
|https://github.co...|
+--------------------+
only showing top 3 rows

scala> sparkDF.select("URLs").head()
res32: org.apache.spark.sql.Row = [https://github.com/bitly/data_hacks]
scala> sparkDF.select("URLs").head().getString(0)
res34: String = https://github.com/bitly/data_hacks
scala> sparkDF.count()
res33: Long = 100000
    */
    
    /*
    Spark is friendly to unit testing 
    with any popular unit test framework. 
    Simply create a SparkContext in your test 
    with the master URL set to `local`, 
    run your operations, 
    and then 
    call SparkContext.stop() 
    to tear it down. 
    Make sure 
    you stop the context 
    within a finally block 
    or the test framework’s tearDown method, 
    as Spark does not support two contexts running concurrently 
    in the same program.
    */
    sc.stop()
  }
}
