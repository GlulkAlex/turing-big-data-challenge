/*
[warn] There may be incompatibilities among your library dependencies; run 'evicted' to see detailed eviction warnings.
[warn] Found version conflict(s) in library dependencies; some are suspected to be binary incompatible:
[warn] 	* io.netty:netty:3.9.9.Final is selected over {3.6.2.Final, 3.7.0.Final}
[warn] 	    +- org.apache.spark:spark-core_2.12:2.4.1             (depends on 3.9.9.Final)
[warn] 	    +- org.apache.zookeeper:zookeeper:3.4.6               (depends on 3.6.2.Final)
[warn] 	    +- org.apache.hadoop:hadoop-hdfs:2.6.5                (depends on 3.6.2.Final)
[warn] 	* com.google.guava:guava:11.0.2 is selected over {12.0.1, 16.0.1}
[warn] 	    +- org.apache.hadoop:hadoop-yarn-client:2.6.5         (depends on 11.0.2)
[warn] 	    +- org.apache.hadoop:hadoop-yarn-api:2.6.5            (depends on 11.0.2)
[warn] 	    +- org.apache.hadoop:hadoop-yarn-common:2.6.5         (depends on 11.0.2)
[warn] 	    +- org.apache.hadoop:hadoop-yarn-server-nodemanager:2.6.5 (depends on 11.0.2)
[warn] 	    +- org.apache.hadoop:hadoop-yarn-server-common:2.6.5  (depends on 11.0.2)
[warn] 	    +- org.apache.hadoop:hadoop-hdfs:2.6.5                (depends on 11.0.2)
[warn] 	    +- org.apache.curator:curator-framework:2.6.0         (depends on 16.0.1)
[warn] 	    +- org.apache.curator:curator-client:2.6.0            (depends on 16.0.1)
[warn] 	    +- org.apache.curator:curator-recipes:2.6.0           (depends on 16.0.1)
[warn] 	    +- org.apache.hadoop:hadoop-common:2.6.5              (depends on 16.0.1)
[warn] 	    +- org.htrace:htrace-core:3.0.4                       (depends on 12.0.1)
*/
//?
scalaVersion := "2.12.8"
/**/
/*
[warn] There may be incompatibilities among your library dependencies; run 'evicted' to see detailed eviction warnings.
[warn] Found version conflict(s) in library dependencies; some are suspected to be binary incompatible:
[warn] 	* io.netty:netty:3.9.9.Final is selected over {3.6.2.Final, 3.7.0.Final}
[warn] 	    +- org.apache.spark:spark-core_2.11:2.4.1             (depends on 3.9.9.Final)
[warn] 	    +- org.apache.zookeeper:zookeeper:3.4.6               (depends on 3.6.2.Final)
[warn] 	    +- org.apache.hadoop:hadoop-hdfs:2.6.5                (depends on 3.6.2.Final)
[warn] 	* com.google.guava:guava:11.0.2 is selected over {12.0.1, 16.0.1}
[warn] 	    +- org.apache.hadoop:hadoop-yarn-client:2.6.5         (depends on 11.0.2)
[warn] 	    +- org.apache.hadoop:hadoop-yarn-api:2.6.5            (depends on 11.0.2)
[warn] 	    +- org.apache.hadoop:hadoop-yarn-common:2.6.5         (depends on 11.0.2)
[warn] 	    +- org.apache.hadoop:hadoop-yarn-server-nodemanager:2.6.5 (depends on 11.0.2)
[warn] 	    +- org.apache.hadoop:hadoop-yarn-server-common:2.6.5  (depends on 11.0.2)
[warn] 	    +- org.apache.hadoop:hadoop-hdfs:2.6.5                (depends on 11.0.2)
[warn] 	    +- org.apache.curator:curator-framework:2.6.0         (depends on 16.0.1)
[warn] 	    +- org.apache.curator:curator-client:2.6.0            (depends on 16.0.1)
[warn] 	    +- org.apache.curator:curator-recipes:2.6.0           (depends on 16.0.1)
[warn] 	    +- org.apache.hadoop:hadoop-common:2.6.5              (depends on 16.0.1)
[warn] 	    +- org.htrace:htrace-core:3.0.4                       (depends on 12.0.1)
*/
//?scalaVersion := "2.11.9"
/**/
// both versions start failin with
// java.lang.ExceptionInInitializerError
// Caused by: com.fasterxml.jackson.databind.JsonMappingException: Incompatible Jackson version: 2.8.11-3
/// <- @fixEd:
name := "simple-spark-deploy"
version := "0.1"

//val sparkVersion = "2.2.0"
val sparkVersion = "2.4.1"
// taken [from](https://github.com/phatak-dev/spark2.0-examples/blob/master/build.sbt) 
resolvers ++= Seq(
  "apache-snapshots" at "http://repository.apache.org/snapshots/"
)
/*
module not found: org.apache.spark#spark-core_2.12;2.2.0
val sparkVersion = "2.2.0"
*/
libraryDependencies ++= Seq (
  "org.apache.spark" %% "spark-core" % sparkVersion
  //?"org.apache.spark" %% "spark-core" % "latest.integration"
)
/**/
// taken [from](https://stackoverflow.com/a/50451139/4623097) 
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-core" % "2.9.5"
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.5"
dependencyOverrides += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.9.5"
/*
// https://mvnrepository.com/artifact/com.fasterxml.jackson.dataformat/jackson-dataformat-cbor
dependencyOverrides += "com.fasterxml.jackson.dataformat" % "jackson-dataformat-cbor" % "2.9.5"

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.3.0"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.3.0"
// https://mvnrepository.com/artifact/org.apache.spark/spark-streaming-kinesis-asl
libraryDependencies += "org.apache.spark" %% "spark-streaming-kinesis-asl" % "2.3.0"
libraryDependencies += "org.apache.spark" %% "spark-streaming" % "2.3.0
*/
libraryDependencies += "com.47deg" %% "github4s" % "0.20.1"

//libraryDependencies += "com.47deg" %% "fetch" % "1.0.0"

libraryDependencies += "com.47deg" % "lambda-test_2.12" % "1.3.1" % "test"
testFrameworks += new TestFramework(
    "com.fortysevendeg.lambdatest.sbtinterface.LambdaFramework")

// for the JVM
//?libraryDependencies += "io.monix" %% "monix" % "3.0.0-RC2"

