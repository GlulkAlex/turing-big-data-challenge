# Data Engineering Challenge
> This is not a final statistics over github repositories  
but mostly an exploratory analysis  
and more like a proof of concept  
of actual viable approach .

## Installation:
### Prerequisites:
* installed `Java`(TM) SE Runtime Environment, at least:
  ```sh
  $ java -version
java version "1.8.0_201"
Java(TM) SE Runtime Environment (build 1.8.0_201-b09)
Java HotSpot(TM) 64-Bit Server VM (build 25.201-b09, mixed mode)
  ```
* installed `sbt` ( The interactive build tool ), e.g.:  
  ```sh
  $ cat ./project/build.properties
  sbt.version=1.2.8
  ```
  [detailed `sbt` installation instructions](https://www.scala-sbt.org/download.html)

### Configuring 
@toDo  

### installing dependencies  
automatically from `build.sbt` after:
```sh
$ sbt
```

### How to run:
[how to start up a Spark cluster  
on Amazon Web Services (AWS)  
using the Flintrock command-line tool](https://heather.miller.am/blog/launching-a-spark-cluster-part-1.html#setting-up-flintrock-and-amazon-web-services)  

### general workflow steps:
1. Develop locally.
2. When ready to deploy, compile, package up `jars` to send to cluster.
3. Copy `jars` to `master` and `worker` nodes using `Flintrock`.
4. Use `spark-submit` script to start job.
5. Check `Spark web UI` to see output (stdout/stderr).  

to run job on local Spark cluster:  
```sh
$ sbt
sbt:simple-spark-deploy> run
```
`Spark` comes with a [`spark-submit` script]("https://spark.apache.org/docs/latest/spark-standalone.html#launching-spark-applications",
)  
which can be used to submit job to the cluster.  

## shutting down server:
inside VM running at `sbt`:  
`Ctrl+C`

## Running tests
( some unit tests ( is | have been ) implemented )
```sh
$ sbt
sbt:simple-spark-deploy> test
```

## Author
**Alex Glukhovtsev**

+ [github/GlulkAlex](https://github.com/GlulkAlex)
+ [twitter/@GlukAlex](https://twitter.com/GlukAlex)

## License
Copyright © 2019 Alex Glukhovtsev  
Released under the MIT license.
