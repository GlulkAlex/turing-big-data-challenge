package big_data//demodeploy

//import org.apache.spark.SparkConf
//import org.apache.spark.SparkContext
import org.apache.spark.{ SparkConf, SparkContext }
// (Before Spark 1.3.0, 
// you need to explicitly import org.apache.spark.SparkContext._ 
// to enable essential implicit conversions.)
import org.apache.spark.SparkContext._

import org.apache.spark.rdd.RDD
// object DataFrame is not a member of package org.apache.spark.sql
// needed libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.3.0"
//import org.apache.spark.sql.DataFrame
//import org.apache.spark.sql.SparkSession

import java.net.URL
import java.io.{ File, PrintWriter }

import org.apache.log4j.{ LogManager, Level, PropertyConfigurator }
/*
import org.apache.log4j.{Level, Logger}
import org.apache.spark.internal.Logging


//+* Utility functions for Spark Streaming examples. +/
object StreamingExamples extends Logging {

  //+* Set reasonable logging levels for streaming 
  if the user has not configured log4j. +/
  def setStreamingLogLevels() {
    val log4jInitialized = Logger.getRootLogger.getAllAppenders.hasMoreElements
    
    if (!log4jInitialized) {
      // We first log something 
      // to initialize Spark's default logging, 
      // then we override the logging level.
      logInfo("Setting log level to [WARN] for streaming example." +
        " To override add a custom log4j.properties to the classpath.")
      Logger.getRootLogger.setLevel(Level.WARN)
    }
  }
}
*/
import GitHub_Repo_Content.{ 
    /*repo_Get_File_Content,
    decode_Base64_Content, 
    drop_Empty_Lines_And_Trailing_Spaces_From_Content, 
    get_Repo_Owner_And_Name_From_URL,
    get_Repo_Master_Tree_Root_URL,*/
    get_Repo_Files_Paths_Names_Iterator
}


/**
> show compile:discoveredMainClasses
[info] * demodeploy.DemoDeploy
> runMain demodeploy.DemoDeploy
*/
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
String appName
        // InputStream inStreamLog4j = getClass().getResourceAsStream("/log4j.properties");

        String propFileName = appName + ".log4j.properties";
        File f = new File("./" + propFileName);
        if (f.exists()) {

            try {
                InputStream inStreamLog4j = new FileInputStream(f);
                Properties propertiesLog4j = new Properties();

                propertiesLog4j.load(inStreamLog4j);
                PropertyConfigurator.configure(propertiesLog4j);
            } catch (Exception e) {
                e.printStackTrace();
                BasicConfigurator.configure();
            }
        } else {
            BasicConfigurator.configure();
        }
    */
    /*
    val propertiesName: String = "./src/main/resources/log4j.properties"
    // not found in './src/main/resources' ?
    // <scheme>://<authority><path>?<query>#<fragment>
    val log_Config: URL = ClassLoader.getSystemResource(propertiesName)
    //assertNotNull("missing configuration: " + propertiesName, log_Config)
    // java.lang.AssertionError: assertion failed: configuration file ./src/main/resources/log4j.properties not found
    assert( 
        //log_Config != null, 
        Option(log_Config).nonEmpty, 
        s"configuration file ${propertiesName} not found " 
    )
    */
    // this affects Spark logging ?
    //LogManager.resetConfiguration()
    // log4j:ERROR Could not read configuration file from URL [null]
    // this affects Spark logging when 'configure' fails
    //PropertyConfigurator.configure( log_Config )
    // it works but has no noticible effect 
    //PropertyConfigurator.configure("src/main/resources/log4j.properties")
    /*
    The class org.apache.log4j.Logger is not serializable 
    which implies we cannot use it inside a closure 
    while doing operations on some parts of the Spark API.

    noted 
    how the log object has been marked as @transient 
    which allows the serialization system to ignore the log object.
    */
    @transient lazy val c_Log = org.apache.log4j.LogManager.getLogger("Spark_Closure_Logger")
    val log = LogManager.getRootLogger
    
    //>log.setLevel(Level.WARN)
    //?LogManager.getLogger("DAGScheduler").setLevel(Level.WARN)
    //?LogManager.getLogger("SparkContext").setLevel(Level.WARN)
    
    def words_Count(): Unit = {
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
    
    log.warn("Hello demo")
    
    println("====DEMO DEPLOY====")

    val text = List(
        """Hadoop MapReduce, 
        |a disk-based big data processing engine, 
        |is being replaced 
        |by a new generation 
        |of memory-based processing frameworks, 
        |the most popular of which is Spark."""
            //?.stripLineEnd
            .stripMargin, 
        "Spark supports Scala, Java, Python, and R."
            //.stripLineEnd
            //.stripMargin
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
        textFile("/my/directory"), */
//        textFile("/my/directory/*.txt"), 
//        and textFile("/my/directory/*.gz").
/*    Apart from text files, 
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
    /// @toDo: use url_list.csv to initialize RDD 
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
    val repos_RDD = sc
        .textFile("url_list.csv")
        .filter( _ != "URLs" )
        .map( url => {
            val files_SHA = get_Repo_Files_Paths_Names_Iterator( repo_URL = url )
            //?.toMap
            .map{ case ( file, sha ) => s"""\"${file}\": \"${sha}\"""" }
            .mkString( "[ ", ", ", " ]" )
            // return:
            s"""{ \"repo\": \"${url}\", \"files\": ${files_SHA} },""" 
            } 
        )
    
    val f_writer = new PrintWriter( new File("result.json" ) )
    /// @toDo: fails for more because of requests rate restrictions to GitHub API ?
    val batch_Size: Int = 2//3
    
    f_writer.println("[")
    /// @toDo: ? handle last trailing comma ',' in list items ?
    repos_RDD
        //.collect()
        .take( batch_Size )
        .foreach( 
            f_writer
                .println 
                // or
                //>.write
        )
    f_writer.write("]")
    // clean up 
    f_writer.close()
    
    if( 1 == 0 ){
    // def parallelize[T](seq: Seq[T], numSlices: Int = defaultParallelism)(implicit arg0: ClassTag[T]): RDD[T]
    //  Distribute a local Scala collection to form an RDD.
    val rdd = sc
        .parallelize(text)
    /*
    // returns: RDD of lines of the text file
    val lines = sc.textFile("data.txt")
    // to drop CSV header 
    // def RDD.filter(f: (T) ⇒ Boolean): RDD[T]
    //  Return a new RDD containing only the elements that satisfy a predicate.
    val lineLengths = lines.map(s => s.length)
    // If we also wanted to use lineLengths again later, 
    // we could add:
    //lineLengths.persist()
    // Finally, we run reduce, which is an action.
    val totalLength = lineLengths.reduce((a, b) => a + b)
    */
    val counts/*: Array[(String, Int)]*/ = rdd
        /// @toDo: pass data retrieval 
        /// and statistics extractor(s) function(s) to | in the driver program
        .flatMap(line => line.split(" "))
        //.filter( word => ??? )
        //.map( word => word.trim().stripSuffix(",").stripSuffix(".").stripSuffix(":") )
        .map( word => word
            .dropWhile( _ == '\n' )
            .takeWhile( (c: Char) => !Set( '\n', ',', '.', ':' ).contains( c ) ) 
        )
        //.map(word => {c_Log.info(s"mapping | counting word:'${word.trim()}'");(word.trim(), 1)})
        .map(word => {c_Log.info(s"counting word:'${word}'");(word, 1)})
        /// @toDo: add | implement ?
        // Accumulators are variables 
        // that are only “added” to 
        // through an associative and commutative operation 
        // and can therefore be efficiently supported in parallel. 
        // They can be used 
        // to implement counters (as in MapReduce) or sums. 
        // Spark natively supports accumulators of numeric types, 
        // and programmers can add support for new types.
        .reduceByKey(_ + _)
        // aggregateByKey(zeroValue)(seqOp, combOp, [numPartitions])
        // path
        //.saveAsTextFile( "result.json" )
        //.collect()
    //}
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
    if( 1 == 0 ){ 
    counts
        .collect()
        .foreach( println )
    }
    /// @toDo: check directory existance and remove it ?
    /// @toDo: lame solution @removeIt altogether 
    counts
        .map{ case ( word, count ) => s"""{\"${word}\": ${count}},""" }
        // Write the elements of the dataset 
        // as a text file (or set of text files) 
        // in a given directory 
        // in the local filesystem
        // org.apache.hadoop.mapred.FileAlreadyExistsException: Output directory file:/../projects/turing.com/result.json already exists
        // WARN FileSystem: exception in the cleaner thread but it will continue to run
        .saveAsTextFile( "result.json" )
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
//     def data_Frame_Example(): Unit = {
//     
//         val spark = SparkSession
//             .builder()
//             .appName("Spark SQL data sources example")
//             //?.config("spark.some.config.option", "some-value")
//             .getOrCreate()
//         // For implicit conversions like converting RDDs to DataFrames
//         // Primitive types (Int, String, etc) and Product types (case classes) encoders are
//         // supported by importing this when creating a Dataset.
//         import spark.implicits._

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
scala> sparkDF.select("URLs").head().getAs[String]("URLs")
res35: String = https://github.com/bitly/data_hacks
scala> sparkDF.head().getAs[String]("URLs")
res36: String = https://github.com/bitly/data_hacks

scala> sparkDF.count()
res33: Long = 100000
    */
//         val spark_DF/*: DataFrame*/ = spark
//             // value read is not a member of org.apache.spark.SparkContext
//             .read
//             .format("csv")
//             .option("header", "true")
//             .option("inferSchema", "true")
//             .load("./url_list.csv")
//         
//         // Looks the schema of this DataFrame.
//         spark_DF.printSchema()
//         // Saves countsByAge to S3 in the JSON format.
//         //countsByAge
//         spark_DF
//             .count()
//             .write
//             .format("json")
//             .save(
//                 //"s3a://..."
//                 "result.json"
//             )
//         
//         spark.stop()
//     }

    words_Count()
    
    log.warn("I am done")
  }
}
