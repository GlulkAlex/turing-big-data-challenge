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
```sh
$ ./bin/spark-shell --master local[2]
```
```sh
$ ./sbin/start-master.sh
starting org.apache.spark.deploy.master.Master
```
and after task id done:  
```sh
$ ./sbin/stop-master.sh
stopping org.apache.spark.deploy.master.Master
```
one option is 
to make a `build` in the corresponding folder  
```sh
$ 
```
or run ? with ? server:  
```sh
$ sbt
sbt:simple-spark-deploy> run
```
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
