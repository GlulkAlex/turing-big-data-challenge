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
  [detailed instructions at](https://www.scala-sbt.org/download.html)

### Setting environment variables
[explained here]()

### unpacking archive:
from `latest.tar.bz2` file  
to destination folder  
or  
### cloning from local repository bundle file  
( and checkout to branch 'master' )  
```sh
$ git clone -b master git_tree_replica.bundle ../local_Repo_Clone
```

### installing dependencies  
automatically from `build.sbt` after:
```sh
$ sbt
```

### How to run:
In order to  
start the ?  

<??? not needed to run the job  
to start `Spark` through the `Scala` shell:  
at folder where `spark-2.4.1-bin-hadoop2.7` was unpacked:  
```sh
$ ./bin/spark-shell --master local[2]
```
( more at `README.md` file in the same directory )
```sh
$ ./sbin/start-master.sh
starting org.apache.spark.deploy.master.Master
```
and after task id done:  
```sh
$ ./sbin/stop-master.sh
stopping org.apache.spark.deploy.master.Master
```
???/>

one option is 
to make a `?`   
```sh
$ 
```
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
`Spark` comes with a `spark-submit` script  
which can be used to submit job to the cluster.  

or use the *?* files  
from `?.tar.gz` archive .

## shutting down server:
inside VM running at `sbt`:  
`Ctrl+C`

## Running tests
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
