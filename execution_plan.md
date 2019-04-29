1. get each github repo  
   from the source list  
   ? as zip file ?  
   Assumption: text files of intrest with code  
   are relativelly small  
   about tenth(s) to fewer hundredths kilobytes in size  
   or less then 1 Mb ( megabyte )  
   so they can be processed in memory as whole but not by chunks of data  
   
    1.1. or traverse repo through GitHub API  
    <- implement with Scala  
    e.g: ( to drop CSV header )
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
unfiltered archive content ( folders and files ):  
short form:
$ tar -tf data_hacks__repo.tar.gz
bitly-data_hacks-c66693b/
bitly-data_hacks-c66693b/.gitignore
bitly-data_hacks-c66693b/README.markdown
bitly-data_hacks-c66693b/data_hacks/
bitly-data_hacks-c66693b/data_hacks/bar_chart.py
bitly-data_hacks-c66693b/data_hacks/histogram.py
bitly-data_hacks-c66693b/data_hacks/ninety_five_percent.py
bitly-data_hacks-c66693b/data_hacks/run_for.py
bitly-data_hacks-c66693b/data_hacks/sample.py
bitly-data_hacks-c66693b/setup.py
$ tar -tvf data_hacks__repo.tar.gz
or
$ tar --list --verbose --file=data_hacks__repo.tar.gz
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
filtered archive content ( folders and files ):  
$ tar -tvf data_hacks__repo.tar.gz | grep .py
-rwxrwxr-x root/root      4495 2018-03-14 02:08 bitly-data_hacks-c66693b/data_hacks/bar_chart.py
-rwxrwxr-x root/root     10716 2018-03-14 02:08 bitly-data_hacks-c66693b/data_hacks/histogram.py
-rwxrwxr-x root/root      1717 2018-03-14 02:08 bitly-data_hacks-c66693b/data_hacks/ninety_five_percent.py
-rwxrwxr-x root/root      1656 2018-03-14 02:08 bitly-data_hacks-c66693b/data_hacks/run_for.py
-rwxrwxr-x root/root      2082 2018-03-14 02:08 bitly-data_hacks-c66693b/data_hacks/sample.py
-rwxrwxr-x root/root       850 2018-03-14 02:08 bitly-data_hacks-c66693b/setup.py
or:  
$ tar -tvf data_hacks__repo.tar.gz --wildcards *.py
-rwxrwxr-x root/root      4495 2018-03-14 02:08 bitly-data_hacks-c66693b/data_hacks/bar_chart.py
-rwxrwxr-x root/root     10716 2018-03-14 02:08 bitly-data_hacks-c66693b/data_hacks/histogram.py
-rwxrwxr-x root/root      1717 2018-03-14 02:08 bitly-data_hacks-c66693b/data_hacks/ninety_five_percent.py
-rwxrwxr-x root/root      1656 2018-03-14 02:08 bitly-data_hacks-c66693b/data_hacks/run_for.py
-rwxrwxr-x root/root      2082 2018-03-14 02:08 bitly-data_hacks-c66693b/data_hacks/sample.py
-rwxrwxr-x root/root       850 2018-03-14 02:08 bitly-data_hacks-c66693b/setup.py
or pipe it:  
$ curl -L https://api.github.com/repos/kevinburke/hamms/tarball | tar tvfz - -C /tmp
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
  0     0    0     0    0     0      0      0 --:--:-- --:--:-- --:--:--     0
100 10906  100 10906    0     0   5954      0  0:00:01  0:00:01 --:--:-- 10.4M
drwxrwxr-x root/root         0 2017-01-05 07:00 kevinburke-hamms-4e6b5b2/
-rw-rw-r-- root/root         5 2017-01-05 07:00 kevinburke-hamms-4e6b5b2/.gitignore
-rw-rw-r-- root/root       143 2017-01-05 07:00 kevinburke-hamms-4e6b5b2/.travis.yml
-rw-rw-r-- root/root      1078 2017-01-05 07:00 kevinburke-hamms-4e6b5b2/LICENSE
-rw-rw-r-- root/root        80 2017-01-05 07:00 kevinburke-hamms-4e6b5b2/MANIFEST
-rw-rw-r-- root/root       433 2017-01-05 07:00 kevinburke-hamms-4e6b5b2/Makefile
-rw-rw-r-- root/root      6673 2017-01-05 07:00 kevinburke-hamms-4e6b5b2/README.md
drwxrwxr-x root/root         0 2017-01-05 07:00 kevinburke-hamms-4e6b5b2/hamms/
-rw-rw-r-- root/root     20314 2017-01-05 07:00 kevinburke-hamms-4e6b5b2/hamms/__init__.py
-rw-rw-r-- root/root        63 2017-01-05 07:00 kevinburke-hamms-4e6b5b2/hamms/__main__.py
-rw-rw-r-- root/root      1654 2017-01-05 07:00 kevinburke-hamms-4e6b5b2/hamms/morse.py
-rw-rw-r-- root/root        22 2017-01-05 07:00 kevinburke-hamms-4e6b5b2/requirements.txt
-rw-rw-r-- root/root        40 2017-01-05 07:00 kevinburke-hamms-4e6b5b2/setup.cfg
-rw-rw-r-- root/root       471 2017-01-05 07:00 kevinburke-hamms-4e6b5b2/setup.py
-rw-rw-r-- root/root        21 2017-01-05 07:00 kevinburke-hamms-4e6b5b2/test-requirements.txt
drwxrwxr-x root/root         0 2017-01-05 07:00 kevinburke-hamms-4e6b5b2/tests/
-rw-rw-r-- root/root      8999 2017-01-05 07:00 kevinburke-hamms-4e6b5b2/tests/test_endpoints.py
-rw-rw-r-- root/root       917 2017-01-05 07:00 kevinburke-hamms-4e6b5b2/tests/test_hamms_server.py
-rw-rw-r-- root/root       423 2017-01-05 07:00 kevinburke-hamms-4e6b5b2/tests/test_utils.py
-rw-rw-r-- root/root       337 2017-01-05 07:00 kevinburke-hamms-4e6b5b2/thread.py
or  
$ curl -#L https://api.github.com/repos/BugScanTeam/DNSLog/tarball | tar tvfz - -C /tmp --wildcards *.py
######################################################################## 100,0%
-rw-rw-r-- root/root         0 2018-11-14 14:07 BugScanTeam-DNSLog-235067b/dnslog/dnslog/__init__.py
-rw-rw-r-- root/root      3200 2018-11-14 14:07 BugScanTeam-DNSLog-235067b/dnslog/dnslog/settings.py
-rw-rw-r-- root/root       970 2018-11-14 14:07 BugScanTeam-DNSLog-235067b/dnslog/dnslog/urls.py
-rw-rw-r-- root/root       411 2018-11-14 14:07 BugScanTeam-DNSLog-235067b/dnslog/dnslog/wsgi.py
-rw-rw-r-- root/root         0 2018-11-14 14:07 BugScanTeam-DNSLog-235067b/dnslog/logview/__init__.py
-rw-rw-r-- root/root        66 2018-11-14 14:07 BugScanTeam-DNSLog-235067b/dnslog/logview/admin.py
-rw-rw-r-- root/root      1929 2018-11-14 14:07 BugScanTeam-DNSLog-235067b/dnslog/logview/migrations/0001_initial.py
-rw-rw-r-- root/root       733 2018-11-14 14:07 BugScanTeam-DNSLog-235067b/dnslog/logview/migrations/0002_auto_20151231_1826.py
-rw-rw-r-- root/root       436 2018-11-14 14:07 BugScanTeam-DNSLog-235067b/dnslog/logview/migrations/0003_auto_20151231_1829.py
-rw-rw-r-- root/root       472 2018-11-14 14:07 BugScanTeam-DNSLog-235067b/dnslog/logview/migrations/0004_user_udomain.py
-rw-rw-r-- root/root       649 2018-11-14 14:07 BugScanTeam-DNSLog-235067b/dnslog/logview/migrations/0005_auto_20151231_1851.py
-rw-rw-r-- root/root       590 2018-11-14 14:07 BugScanTeam-DNSLog-235067b/dnslog/logview/migrations/0006_auto_20151231_1909.py
-rw-rw-r-- root/root       731 2018-11-14 14:07 BugScanTeam-DNSLog-235067b/dnslog/logview/migrations/0007_auto_20160312_0151.py
-rw-rw-r-- root/root         0 2018-11-14 14:07 BugScanTeam-DNSLog-235067b/dnslog/logview/migrations/__init__.py
-rw-rw-r-- root/root      1506 2018-11-14 14:07 BugScanTeam-DNSLog-235067b/dnslog/logview/models.py
-rw-rw-r-- root/root        63 2018-11-14 14:07 BugScanTeam-DNSLog-235067b/dnslog/logview/tests.py
-rw-rw-r-- root/root      5385 2018-11-14 14:07 BugScanTeam-DNSLog-235067b/dnslog/logview/views.py
-rw-rw-r-- root/root       464 2018-11-14 14:07 BugScanTeam-DNSLog-235067b/dnslog/manage.py
-rw-rw-r-- root/root      4356 2018-11-14 14:07 BugScanTeam-DNSLog-235067b/dnslog/zoneresolver.py

```
url: https://api.github.com/repos/bitly/data_hacks/downloads  
[
  {
    "url": "https://api.github.com/repos/bitly/data_hacks/downloads/57985",
    "id": 57985,
    "html_url": "https://github.com/downloads/bitly/data_hacks/data_hacks-0.2.tar.gz",
    "name": "data_hacks-0.2.tar.gz",
    "description": "v 0.2",
    "created_at": "2010-10-21T01:27:01Z",
    "size": 4096,
    "download_count": 9699,
    "content_type": "application/octet-stream"
  },
  {
    "url": "https://api.github.com/repos/bitly/data_hacks/downloads/57964",
    "id": 57964,
    "html_url": "https://github.com/downloads/bitly/data_hacks/data_hacks-0.1.tar.gz",
    "name": "data_hacks-0.1.tar.gz",
    "description": "v 0.1",
    "created_at": "2010-10-20T23:02:50Z",
    "size": 4096,
    "download_count": 6097,
    "content_type": "application/octet-stream"
  }
]
```

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
