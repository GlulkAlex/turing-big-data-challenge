1. get each github repo from the source list as zip file  
    1.1. or traverse repo through GitHub API  
    <- implement with Scala  
    e.g:
$ head -n2 url_list.csv | tail -n1
https://github.com/bitly/data_hacks
$ curl -L https://api.github.com/repos/bitly/data_hacks/zipball > data_hacks__repo.zip
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
  0     0    0     0    0     0      0      0 --:--:-- --:--:-- --:--:--     0
100 11546    0 11546    0     0   6015      0 --:--:--  0:00:01 --:--:-- 13680
# or
$ curl -L https://api.github.com/repos/bitly/data_hacks/tarball > data_hacks__repo.tar.gz
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
  0     0    0     0    0     0      0      0 --:--:-- --:--:-- --:--:--     0
100  7210    0  7210    0     0   3731      0 --:--:--  0:00:01 --:--:-- 2347k

2. filter `*.py` files  
$ tar -tvf data_hacks__repo.tar.gz
drwxrwxr-x root/root         0 2018-03-14 02:08 bitly-data_hacks-c66693b/
-rw-rw-r-- root/root        11 2018-03-14 02:08 bitly-data_hacks-c66693b/.gitignore
-rw-rw-r-- root/root      3941 2018-03-14 02:08 bitly-data_hacks-c66693b/README.markdown
drwxrwxr-x root/root         0 2018-03-14 02:08 bitly-data_hacks-c66693b/data_hacks/
-rwxrwxr-x root/root      4495 2018-03-14 02:08 bitly-data_hacks-c66693b/data_hacks/bar_chart.py
-rwxrwxr-x root/root     10716 2018-03-14 02:08 bitly-data_hacks-c66693b/data_hacks/histogram.py
-rwxrwxr-x root/root      1717 2018-03-14 02:08 bitly-data_hacks-c66693b/data_hacks/ninety_five_percent.py
-rwxrwxr-x root/root      1656 2018-03-14 02:08 bitly-data_hacks-c66693b/data_hacks/run_for.py
-rwxrwxr-x root/root      2082 2018-03-14 02:08 bitly-data_hacks-c66693b/data_hacks/sample.py
-rwxrwxr-x root/root       850 2018-03-14 02:08 bitly-data_hacks-c66693b/setup.py
gluk-alex@glukalex-desktop:~/Documents/projects/turing.com$ tar -tvf data_hacks__repo.tar.gz | grep .py
-rwxrwxr-x root/root      4495 2018-03-14 02:08 bitly-data_hacks-c66693b/data_hacks/bar_chart.py
-rwxrwxr-x root/root     10716 2018-03-14 02:08 bitly-data_hacks-c66693b/data_hacks/histogram.py
-rwxrwxr-x root/root      1717 2018-03-14 02:08 bitly-data_hacks-c66693b/data_hacks/ninety_five_percent.py
-rwxrwxr-x root/root      1656 2018-03-14 02:08 bitly-data_hacks-c66693b/data_hacks/run_for.py
-rwxrwxr-x root/root      2082 2018-03-14 02:08 bitly-data_hacks-c66693b/data_hacks/sample.py
-rwxrwxr-x root/root       850 2018-03-14 02:08 bitly-data_hacks-c66693b/setup.py
$ tar -tvf data_hacks__repo.tar.gz --wildcards *.py
-rwxrwxr-x root/root      4495 2018-03-14 02:08 bitly-data_hacks-c66693b/data_hacks/bar_chart.py
-rwxrwxr-x root/root     10716 2018-03-14 02:08 bitly-data_hacks-c66693b/data_hacks/histogram.py
-rwxrwxr-x root/root      1717 2018-03-14 02:08 bitly-data_hacks-c66693b/data_hacks/ninety_five_percent.py
-rwxrwxr-x root/root      1656 2018-03-14 02:08 bitly-data_hacks-c66693b/data_hacks/run_for.py
-rwxrwxr-x root/root      2082 2018-03-14 02:08 bitly-data_hacks-c66693b/data_hacks/sample.py
-rwxrwxr-x root/root       850 2018-03-14 02:08 bitly-data_hacks-c66693b/setup.py

3. deploy instance of Apache Spark ( locally or in AWS )  
   to have some parallelization level | abilites  
   or just run | feed input in batches | ranges | parts  
   and accululate | append results in big log  
4. get content of all repositories  
   or limited random population of them ( for better performance )  
   
Optimisations:  
  Given  
  that we aren’t looking for 100% accuracy,  
  can you trade off some accuracy  
  for a much faster method ?

statistics ​only for the Python code​ present.  
Here is the list of items  
that you need to compute  
for each repository:
1. Number of lines of code​  
   [this excludes comments, whitespaces, blank lines].
2. List of ​ external libraries/packages​ used.  
   ? <- setup and requirments if present ?  
   ? or set over used imports ?
3. The ​ Nesting factor for the repository:  
   the Nesting factor  
   is the average depth  
   of a nested `for` loop throughout the code.  
   <- ? how to mark | differentiate `for` scope ?  
      -> is it all to the right   
         under `for ... in ... :`  
         until same level statement or end of file ?  
   Note:  
     You must report  
     the average nesting factor  
     for the ​ entire repository​  
     and not individual files.
4. Code duplication:  
   What percentage of the code is duplicated per file.  
   If the same 4 consecutive lines of code  
   (disregarding blank lines, comments, etc.  
   other non code items)  
   appear in multiple places in a file,  
   all the occurrences  
   except the first occurence  
   are considered to be duplicates.  
   <- normalize strings  
      -> strip none code text  
      -> make hash for every 4 lines slide  
      -> count duplicates 
5. Average number of parameters per function definition in the repository.  
   <- normalize strings  
      -> make def <function name>(): be easy parsible ( at the same string ? )  
      -> combine / extract / count ',' inside () after def  
      -> deal with * and ',' in default values 
6. Average Number of variables defined per line of code in the repository.  
    -> how to distinguish | select ? by assigment '=' ?  
       what about tuples | destructive assigment ? 
    <- case studies | exploratory analyses needed  
    must know | track | store defined variables count per line  
    to compute statistics  

almost any statistics mentioned above could be collected using UDF  
( user defined functions ) or with `aggregate` ?
    
1. An output ( huge ) file `results.json`  
   with the results of the computation  
   ( at least 100_000 lines )  
   that looks like incremental log  
   example:  
```[
{
'repository_url': 'https://github.com/tensorflow/tensorflow',
'number of lines': ​ 59234​ ,
'libraries': ['tensorflow','numpy',...],
'nesting factor': ​ 1.457845​ ,
'code duplication': ​ 23.78955​ ,
'average parameters': ​ 3.456367​ ,
'average variables': ​ 0.03674
}, 
...
]```
