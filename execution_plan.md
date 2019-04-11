1. get each github repo from the source list as zip file  
    1.1. or traverse repo through GitHub API  
    <- implement with Scala  
2. filter `*.py` files  
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
