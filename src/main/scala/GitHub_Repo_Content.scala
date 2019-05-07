package big_data
//package demodeploy

import java.util.Base64
import java.util.Base64.Decoder
    
import github4s.Github
import github4s.Github._
import github4s.jvm.Implicits._
import github4s.free.domain.{ Repository, Content, RepoUrls }

import scalaj.http.HttpResponse
// if you're using ScalaJS, replace occurrences of HttpResponse by SimpleHttpResponse
//import github4s.js.Implicits._
//import fr.hmil.roshttp.response.SimpleHttpResponse

import cats.data.NonEmptyList 

import scala.util.{ Try, Success, Failure }
import scala.io.{ Source, BufferedSource }

import JSON_Parser.{ 
//     File_Props, get_Field_Name, 
//     get_Field_Value, 
//     file_Props_Json_Parser, 
//     map_File_Props_Json,
    get_Current_Tree_Children_Props_Iterator,
    drop_Chars_While_Word_Not_Found,
    take_Until_Char,
    skip_After_Char
}


/**
# get all *.py files in repo with relative path names included 
# e.g. for 'https://github.com/BugScanTeam/DNSLog':
$ curl -#L https://api.github.com/repos/BugScanTeam/DNSLog/tarball | tar tvfz - -C /tmp --wildcards *.py
######################################################################## 100,0%
-rw-rw-r-- root/root         0 2018-11-14 14:07 BugScanTeam-DNSLog-235067b/dnslog/dnslog/__init__.py
...
-rw-rw-r-- root/root      4356 2018-11-14 14:07 BugScanTeam-DNSLog-235067b/dnslog/zoneresolver.py

# path to file on master branch ( not for download but to view content )
|repo url from csv                    |github route|part from tarball
 https://github.com/BugScanTeam/DNSLog/tree/master/dnslog/dnslog/__init__.py

or using path | files exteactor with github api:
# for 'https://github.com/pirate/crypto-trader'
|github api route            |owner |repo name    |branche route  |
 https://api.github.com/repos/pirate/crypto-trader/branches/master
then get tree root url:
...
      "tree": {
        ...,
        "url": "https://api.github.com/repos/pirate/crypto-trader/git/trees/26e721b5e45fab0b7ba722e56136fef58a696724"
      },
...
then got info for recursive tree traversal:
...
    {
      "path": "stubs",
      "mode": "040000",
      "type": "tree", <- next recursion level 
      "sha": "622cb951fb9ddd1d634371c47add145b5471f68c",
      "url": "https://api.github.com/repos/pirate/crypto-trader/git/trees/622cb951fb9ddd1d634371c47add145b5471f68c"
    },
    {
      "path": "symbols.py", <- file name
      "mode": "100644",
      "type": "blob", <- file
      "sha": "248ffb14ea2899491d92f40c4d17586102604efa",
      "size": 6824,
      "url": "https://api.github.com/repos/pirate/crypto-trader/git/blobs/248ffb14ea2899491d92f40c4d17586102604efa"
    }
...

GET /repos/:owner/:repo/contents/:path
"url": "https://api.github.com/repos/octokit/octokit.rb/contents/README.md",
*/
object GitHub_Repo_Content 
    //extends App 
    {
    
    /** 
    'https://github.com/BugScanTeam/DNSLog'
    */
    def get_Repo_Owner_And_Name_From_URL( 
        url: String 
    ): ( String, String ) = {
        val Array( repo_Name, repo_Owner ) = url.split("/").reverse.take(2)
        ( repo_Owner, repo_Name )
    }
    
/*
HTTP 403 is a standard HTTP status code 
communicated to clients by an HTTP server 
to indicate 
that access to the requested (valid) URL by the client 
is Forbidden for some reason. 
The server understood the request, 
but will not fulfill it 
due to client related issues. 
There are a number of sub-status error codes 
that provide a more specific reason 
for responding with the 403 status code.
*/
    /** from response JSON 
    return:
        'repo_Master_Tree_Root_URL' if succeeded 
        [ empty string | Option[ String ] | "" & error message ] when fails ?
    */
    @throws(classOf[java.io.IOException])
    def get_Repo_Master_Tree_Root_URL(
        master_Url: String// = "https://api.github.com/repos/pirate/crypto-trader/branches/master"
    ): Option[ String ] = {
        // scala.io.Codec -> final val UTF8: Codec
        // scala.io.Source.
        //  fromURL(url: URL, enc: String): BufferedSource
        //  creates Source from file with given file: URI
        // fromURL(s: String)(implicit codec: Codec): BufferedSource
        //    same as fromURL(new URL(s))
        // fromURL(s: String, enc: String): BufferedSource
        //  same as fromURL(new URL(s))(Codec(enc))
        // scala.io.BufferedSource.
        //  getLines(): collection.Iterator[String]
        //  Returns an iterator who returns lines (NOT including newline character(s)).
        //val master_Url = "https://api.github.com/repos/pirate/crypto-trader/branches/master"
        
        /// @toDo: @handleThis: with Try ?
        // Unexpected exception: Server returned HTTP response code: 403 for URL: https://api.github.com/repos/bitly/data_hacks/branches/master
/*
  val file_Content = Try(
    scala.io.Source.fromFile(name = f_Name, enc = "utf-8").getLines()
  )
  file_Content match {
    case Success(f_C) => f_C
    //scala> Iterator[String]()
    //res762: Iterator[String] = empty iterator
    case Failure(e) => Iterator[String]()
  }
*/
        // java.net.URL
        // URL(String spec)
        //  Creates a URL object from the String representation.
        // url.openStream()
        // public final InputStream openStream() throws IOException
        // java.io.IOException
        // get JSON from github API
        val github_API_Response_Source: Try[scala.io.BufferedSource] = Try(
        scala.io.Source
            .fromURL(s = master_Url, enc = "UTF8" ) 
        )
        
        /// @toDo: it can be done in one scan without splitting 
        //>println( github_API_Response_Source.mkString )
        /*
        "tree": {
            "sha": "26e721b5e45fab0b7ba722e56136fef58a696724",
            "url": "https://api.github.com/repos/pirate/crypto-trader/git/trees/26e721b5e45fab0b7ba722e56136fef58a696724"
        },
        drop until "tree" field
        then take "sha" or "url" value 
        */
        val repo_Master_Tree_Root_URL: Option[ String ] = 
        github_API_Response_Source match {
            // response content 
            case Success(r_C) => { 
                val after_Tree: BufferedSource = drop_Chars_While_Word_Not_Found(
                    word = "tree",
                    word_Size = 4,
                    chars_Iterator = r_C
                )
                val after_Url: BufferedSource = drop_Chars_While_Word_Not_Found(
                    word = "url",
                    word_Size = 3,
                    chars_Iterator = after_Tree
                )
                // mutate
                skip_After_Char( 
                    stop_At = ':',
                    chars_Iterator = after_Url
                )
                skip_After_Char( 
                    stop_At = '"',
                    chars_Iterator = after_Url
                )
                val url: String = take_Until_Char( 
                    stop_At = '"', 
                    chars_Iterator = after_Url
                )
            //>val url: String = r_C
            //?.getLines()
            // startsWith(String prefix)
            //.dropWhile( _.trim().startsWith("\"tree\"") )
            //>.mkString
    // scala> s.split(",").dropWhile( !_.startsWith("\"tree\"") ).filter( _.endsWith("}") ).head
    // res11: String = "url":"https://api.github.com/repos/pirate/crypto-trader/git/trees/26e721b5e45fab0b7ba722e56136fef58a696724"}
    // scala> s.split(",").dropWhile( !_.startsWith("\"tree\"") ).drop(1 ).head
    // res12: String = "url":"https://api.github.com/repos/pirate/crypto-trader/git/trees/26e721b5e45fab0b7ba722e56136fef58a696724"}
    //scala> s.split(",").dropWhile( !_.startsWith("\"tree\"") ).drop(1 ).head.drop(7).stripSuffix("\"}")
    //res13: String = https://api.github.com/repos/pirate/crypto-trader/git/trees/26e721b5e45fab0b7ba722e56136fef58a696724
            // found   : Array[String]
            //>.split(",")
            //>.dropWhile( !_.startsWith("\"tree\"") )
            //>.drop(1)
            //>.head
            //>.drop(7)
            //>.stripSuffix("\"}")
            
                // clean up ? releasing resource ? 
                //github_API_Response_Source
                r_C
                    .close()
                
                Some( url )
            }
            // type Throwable = java.lang.Throwable
            // String   Throwable.getMessage()
            //  Returns the detail message string of this throwable.
            case Failure(e) => None//Iterator[String]()
        }
            
        println( s"repo_Master_Tree_Root_URL: ${repo_Master_Tree_Root_URL}" )
        
        repo_Master_Tree_Root_URL
    }
    
    /**
scala> example_Iterator().size
res7: Int = 4
    */
    def example_Iterator( 
        start_From: Int = 0,
        stop_At: Int = 3
    ): Iterator[ Int ] = new scala.collection.AbstractIterator[ Int ]{
        private 
        var state_Counter = start_From
        
        def hasNext: Boolean = state_Counter <= stop_At
        
        def next(): Int = if (
            hasNext
        ) { 
            state_Counter += 1
            state_Counter
        } else {
            // java.util.NoSuchElementException: next on empty iterator
            Iterator[ Int ]().next()
        }
    }
    
    /// @toDo: implement 'get_Repo_Files_Paths_Names_Iterator'
    /** 
    (BFS) traverse github api tree 
    by repo link | url 
    and eventually get all the 
    "self": "https://api.github.com/repos/bitly/data_hacks/contents/setup.py?ref=master"
    as tree leafs
    with .contents field encoded text content 
    i.e.
    sollecting blob's sha 
    
    Optionally
    exact folders structure 
    with directories might be recreated 
    or stored along with each file name
    if needed 
    but to get files's git/blobs/(sha).content only sha is actually needed 
    
    @return:
        map file name to git/blobs/(sha)
        api url prefix part might be constructed add added at any time 
    */
    def get_Repo_Files_Paths_Names_Iterator( 
        // constructor parameter
        repo_URL: String,// = "https://github.com/bitly/data_hacks",
        has_Suffix: String = ".py",
        is_DeBug_Mode: Boolean = 1 == 0
    ): Iterator[ ( String, String ) ] = new scala.collection.AbstractIterator[ 
        ( String, String ) 
    ]{
        // root source ?
        //val master_Url = "https://api.github.com/repos/pirate/crypto-trader/branches/master"
        // from master_Url.commit.commit.tree.url: 
        //val tree_Root_Url = "https://api.github.com/repos/pirate/crypto-trader/git/trees/(sha)
        // get 
        /*"tree": [ {
            "path": ".gitignore" | "data_hacks",
            "mode": "100644",
            "type": "blob" | "tree",
            "sha": "9d0b71a3c79d2d3afbfa99269fea4280f5e73344",
            "size": 11,
            "url": "https://api.github.com/repos/bitly/data_hacks/git/blobs/9d0b71a3c79d2d3afbfa99269fea4280f5e73344"
        }, ... ]*/
        // then use 
        //val repo_Git_Blobs_Encoded_File_Content_URL = ""("content")
        // or compose:
        // "url": "https://api.github.com/repos/pirate/crypto-trader/contents/setup.py" + ("?ref=master") // <- query string and ref part is | are optional 
        // if fetch fails ( wrong file path name case ) it might retrun with:
        // GET -> https://api.github.com/repos/pirate/crypto-trader/contents/setup.py
        // { "message": "Not Found", "documentation_url": "https://developer.github.com/v3/repos/contents/#get-contents" }
        // https://api.github.com/repos/pirate/crypto-trader/contents/.gitignore -> Ok
        // response["content"] = "Lm15cHlfY2FjaGUvCnNlY3JldHMucHkKZGF0YS8KbWlzYy8K\n"
        val ( owner, name ) = get_Repo_Owner_And_Name_From_URL( repo_URL )
        val master_Url = s"https://api.github.com/repos/${owner}/${name}/branches/master"
        /// @toDo: handle failure here by using retry strategy ?
        val master_Tree_Root_URL: String = get_Repo_Master_Tree_Root_URL(
            master_Url = master_Url
        ).getOrElse("")
        /// @Done: store | maintain stack of 
        /// get_Current_Tree_Children_Props_Iterator(s)
        /// as hasNext stop condition 
        private 
        //var hasnext = true
        var tree_Children_Iterators_Stack = List(
            get_Current_Tree_Children_Props_Iterator( 
                /// @toDo: handle failure here by using retry strategy ?
                github_API_Response_Buffered_Source = scala.io.Source
                    .fromURL(
                        s = master_Tree_Root_URL, 
                        enc = "UTF8" 
                    )
            )
        )
        
        def hasNext: Boolean = (
            tree_Children_Iterators_Stack.nonEmpty
            && tree_Children_Iterators_Stack.head.hasNext
        )
        
        def next(): ( String, String ) = if (
            hasNext
        ) { 
            // pop head
            val top_Head = tree_Children_Iterators_Stack.head
            //hasnext = false 
            if( top_Head.hasNext ){
                val ( path, type_Str, sha ) = top_Head.next()
                if(is_DeBug_Mode){println(s"extracted path: ${path}, type: ${type_Str}, sha: ${sha}")}
                if( type_Str == "tree" ){
                    if(is_DeBug_Mode){println(s"\tappending ${path} ${type_Str} iterator")}
                    /// @Done: @fixEd this conditional branch
                    // mutate iterator's state 
                    tree_Children_Iterators_Stack = tree_Children_Iterators_Stack.::(
                        get_Current_Tree_Children_Props_Iterator( 
                            github_API_Response_Buffered_Source = scala.io.Source
                                .fromURL(
                                    s = s"https://api.github.com/repos/${owner}/${name}/git/trees/${sha}", 
                                    enc = "UTF8" 
                                )
                        )
                    )
                    next()
                }else{// if( type_Str == "blob" ){
                    /// @toDo: add "*.py" filter ?
                    //"answer" -> "42" 
// scala> "42".endsWith("2")
// res0: Boolean = true
// scala> "42".endsWith("")
// res1: Boolean = true
// scala> "42".endsWith("a")
// res2: Boolean = false
                    if( path.endsWith( has_Suffix ) ){
                        path -> sha
                    }else{
                        next()
                    }
                }
            }else{// top_Head.isEmpty 
                if(is_DeBug_Mode){println(s"top_Head.isEmpty: ${top_Head}")}
                // hasNext guarantees at least head item in current sack
                tree_Children_Iterators_Stack = tree_Children_Iterators_Stack.tail
                next()
            }
        } else {
            if(is_DeBug_Mode){println(s"tree_Children_Iterators_Stack.isEmpty: ${tree_Children_Iterators_Stack}")}
            // Unexpected exception: next on empty iterator
            //!new //?scala.Nothing//?empty.next()
            Iterator[ ( String, String ) ]().next()
        }
    }
    
    /**
    Number of lines of code​ per file 
    ( this excludes: comments, whitespaces, blank lines ).
    => drop: 
        start from '#'
        lines only from '\t', ' ' and '\n'
    /// @whatAbout: docstrings ?
        """
        """

    */
    /**
    aggregate Number of lines of code​ per file 
    as repository total
    */
    /**
    List of​ external libraries/packages​ used.  
    - setup and requirments if present ?  
        not all repos have 'requirements.txt' or 'setup.py'
    or 
    - set over used imports ?
    
    e.g.
    import sys                  +1
    or
    import lib1 as l1, lib2 as l2, lib3, lib4 as l4, lib5
    from decimal import Decimal +1 <- "easy" case because one 'from'
    but this also possible:
    from ..filters import equalizer <- local import with path
        used leading dots 
        to indicate the current and parent packages 
        involved in the relative import
    -> then filter non match(ing) of repo's files | scripts 
    */

    val file_Content_Url = "https://api.github.com/repos/pirate/crypto-trader/contents/symbols.py"
    
    /*
    You can create a personal access token 
    and use it 
    in place of a password 
    when performing Git operations over HTTPS 
    with Git on the command line 
    or the API.
    
    Warning: 
    Treat your tokens like passwords 
    and keep them secret. 
    When working with the API, 
    use tokens as environment variables 
    instead of hardcoding them into your programs.
    */
    // 
    val accessToken = sys.env.get("GITHUB4S_ACCESS_TOKEN")
    //accessToken:'Some(1...c)'
    //println(s"accessToken:'${accessToken}'")
    
    if( 1 == 0 ){
    val getRepo = Github(accessToken)
        .repos.get(
        // the repository coordinates (owner and name of the repository).
            //>"47deg", "github4s"
            //https://github.com/bitly/data_hacks
            "bitly", "data_hacks"
        )
/*
Repository(
    946824, // 0
    data_hacks, // 1
    bitly/data_hacks, // 2
    User( // 3
        251133,bitly,
        https://avatars1.githubusercontent.com/u/251133?v=4,
        https://github.com/bitly,None,None,None,None,None,None,
        Some(https://api.github.com/users/bitly/followers),
        Some(https://api.github.com/users/bitly/following{/other_user}),
        Organization,None,None
    ),
    false, // 4
    Some(Command line utilities for data analysis),
    false, // 5
    RepoUrls( // 6
        https://api.github.com/repos/bitly/data_hacks, // 7 6.0
        https://github.com/bitly/data_hacks, // 8 6.1
        git://github.com/bitly/data_hacks.git,git@github.com:bitly/data_hacks.git, // 9
        https://github.com/bitly/data_hacks.git, // 10 6.2
        https://github.com/bitly/data_hacks, // 11 6.3
        Map( // 6.4
            tags_url -> https://api.github.com/repos/bitly/data_hacks/tags, 
            statuses_url -> https://api.github.com/repos/bitly/data_hacks/statuses/{sha}, 
            blobs_url -> https://api.github.com/repos/bitly/data_hacks/git/blobs{/sha}, 
            git_refs_url -> https://api.github.com/repos/bitly/data_hacks/git/refs{/sha}, 
            issue_events_url -> https://api.github.com/repos/bitly/data_hacks/issues/events{/number}, 
            subscribers_url -> https://api.github.com/repos/bitly/data_hacks/subscribers, 
            releases_url -> https://api.github.com/repos/bitly/data_hacks/releases{/id}, 
            ///>>>
            trees_url -> https://api.github.com/repos/bitly/data_hacks/git/trees{/sha}, 
            ///>>>
            // it gives JSON with
//             "tree": {
//                 "sha": "994b441daecddd98c3b313a288c1ae0611e56439",
//                 "url": "https://api.github.com/repos/bitly/data_hacks/git/trees/994b441daecddd98c3b313a288c1ae0611e56439"
//             },
// that gives in turn:
// {
//   "sha": "994b441daecddd98c3b313a288c1ae0611e56439",
//   "url": "https://api.github.com/repos/bitly/data_hacks/git/trees/994b441daecddd98c3b313a288c1ae0611e56439",
//   "tree": [
//     {
//       "path": ".gitignore",
//       "mode": "100644",
//       "type": "blob",
//       "sha": "9d0b71a3c79d2d3afbfa99269fea4280f5e73344",
//       "size": 11,
//       "url": "https://api.github.com/repos/bitly/data_hacks/git/blobs/9d0b71a3c79d2d3afbfa99269fea4280f5e73344"
//     },
//     {
//       "path": "README.markdown",
//       "mode": "100644",
//       "type": "blob",
//       "sha": "053dae1ad11e6b864a97031430abd4d470c0ea34",
//       "size": 3941,
//       "url": "https://api.github.com/repos/bitly/data_hacks/git/blobs/053dae1ad11e6b864a97031430abd4d470c0ea34"
//     },
//     {
//       "path": "data_hacks",
//       "mode": "040000",
///> recursion 
//       "type": "tree",
//       "sha": "4cf5df18ec0052abf4d561c992f138ac144f8304",
//       "url": "https://api.github.com/repos/bitly/data_hacks/git/trees/4cf5df18ec0052abf4d561c992f138ac144f8304"
//     },
//     {
//       "path": "setup.py",
//       "mode": "100755",
//       "type": "blob",
//       "sha": "36fa158820c95e26d816f8f1bbfd962e1a482df9",
//       "size": 850,
//       "url": "https://api.github.com/repos/bitly/data_hacks/git/blobs/36fa158820c95e26d816f8f1bbfd962e1a482df9"
//     }
//   ],
//   "truncated": false
// }
            branches_url -> https://api.github.com/repos/bitly/data_hacks/branches{/branch}, 
            collaborators_url -> https://api.github.com/repos/bitly/data_hacks/collaborators{/collaborator}, 
            subscription_url -> https://api.github.com/repos/bitly/data_hacks/subscription, 
            languages_url -> https://api.github.com/repos/bitly/data_hacks/languages, 
            commits_url -> https://api.github.com/repos/bitly/data_hacks/commits{/sha}, 
            contents_url -> https://api.github.com/repos/bitly/data_hacks/contents/{+path}, 
            git_tags_url -> https://api.github.com/repos/bitly/data_hacks/git/tags{/sha}, 
            downloads_url -> https://api.github.com/repos/bitly/data_hacks/downloads, 
            milestones_url -> https://api.github.com/repos/bitly/data_hacks/milestones{/number}, 
            compare_url -> https://api.github.com/repos/bitly/data_hacks/compare/{base}...{head}, 
            notifications_url -> https://api.github.com/repos/bitly/data_hacks/notifications{?since,all,participating}, 
            comments_url -> https://api.github.com/repos/bitly/data_hacks/comments{/number}, 
            pulls_url -> https://api.github.com/repos/bitly/data_hacks/pulls{/number}, 
            teams_url -> https://api.github.com/repos/bitly/data_hacks/teams, 
            merges_url -> https://api.github.com/repos/bitly/data_hacks/merges, 
            keys_url -> https://api.github.com/repos/bitly/data_hacks/keys{/key_id}, 
            deployments_url -> https://api.github.com/repos/bitly/data_hacks/deployments, 
            contributors_url -> https://api.github.com/repos/bitly/data_hacks/contributors, 
            forks_url -> https://api.github.com/repos/bitly/data_hacks/forks, 
            hooks_url -> https://api.github.com/repos/bitly/data_hacks/hooks, 
            archive_url -> https://api.github.com/repos/bitly/data_hacks/{archive_format}{/ref}, 
            issues_url -> https://api.github.com/repos/bitly/data_hacks/issues{/number}, 
            assignees_url -> https://api.github.com/repos/bitly/data_hacks/assignees{/user}, 
            events_url -> https://api.github.com/repos/bitly/data_hacks/events, 
            issue_comment_url -> https://api.github.com/repos/bitly/data_hacks/issues/
            comments{/number}, labels_url -> https://api.github.com/repos/bitly/data_hacks/
            labels{/name}, git_commits_url -> https://api.github.com/repos/bitly/data_hacks/git/commits{/sha}, 
            stargazers_url -> https://api.github.com/repos/bitly/data_hacks/stargazers
        )
    ),
    2010-09-28T22:09:22Z,
    2019-04-18T14:04:24Z,
    2018-03-13T21:08:37Z,
    Some(
        http://github.com/bitly/data_hacks
    ),
    Some(Python),
    RepoStatus(
        50,1840,1840,182,18,Some(18),Some(1840),
        Some(182),Some(128),true,true,true,false
    ),
    Some(
        User(
            251133,bitly,
            https://avatars1.githubusercontent.com/u/251133?v=4,
            https://github.com/bitly,None,None,None,None,None,None,
            Some(
                https://api.github.com/users/bitly/followers
            ),
            Some(
                https://api.github.com/users/bitly/following{/other_user}
            ),
            Organization,None,None
        )
    )
)
*/
    getRepo.exec[cats.Id, HttpResponse[String]]() match {
        case Left(e) => println(s"Something went wrong: ${e.getMessage}")
        case Right(r) => r.result match {
            case repository: Repository => {
                println("""Github.repos.get( owner: "bitly", repository: "data_hacks" )""")
                //>println(r.result)
                    val RepoUrls(
                        url: String,
                        html_url: String,
                        git_url: String,
                        ssh_url: String,
                        clone_url: String,
                        svn_url: String,
                        otherUrls: Map[String, String]
                    ) = repository.urls
                    println(
                        s"""branches_url:
                        |${
                            otherUrls.getOrElse( 
                                "branches_url", "key not found" )
                        }""".stripMargin
                    )
                }
            case _ => println(s"""Unexpeced Github.repos.get():\n${r.result}""") 
        }
    }
    }
    
    /** from Base64 encoded string */
    def decode_Base64_Content( content: String ): String = {
        // decoded just fine from UTF-8 
        // at https://www.base64decode.org/
        // js 
        // var decodedData = window.atob(encodedData); 
        // decoded the string
        //val decoded_Content: String = 
        new String(
            // java.util.Base64.Decoder
            Base64
                                /*
public static Base64.Decoder getDecoder()
    Returns a Base64.Decoder 
    that decodes using the Basic type base64 encoding scheme.
                                */
                //?.getDecoder
                .getMimeDecoder
                // java.lang.IllegalArgumentException: Illegal base64 character a
                                /*
public byte[] decode(String src)
Decodes a Base64 encoded String 
into a newly-allocated byte array 
using the Base64 encoding scheme.
An invocation of this method 
has exactly the same effect 
as invoking 
decode(src.getBytes(StandardCharsets.ISO_8859_1))
                                */
                .decode(
                    // java.lang.ClassCastException: scala.Some cannot be cast to java.lang.String
                    content
                        // for Option[String]
//                         .get
//                         .asInstanceOf[String]
                )
        )
        
        //decoded_Content
    }
    
    /** Normalized: without empty strings and trailing spaces */
    def drop_Empty_Lines_And_Trailing_Spaces_From_Content( 
        decoded_Content: String 
    ): String = {
        //println( "*" * 80 )
        decoded_Content
            .lines
            //?.map( _.trim() )
            .map( _.stripSuffix(" ") )
            .filter( _ != "" )
            .mkString("\n") 
    }
    
    /*
    Get contents
    This method returns 
    the contents of a file or directory 
    in a repository.

    You can get contents using getContents, 
    it takes as arguments:
        - the repository coordinates (owner and name of the repository).
        - path: The content path.
        - ref: The name of the commit/branch/tag. 
            Default: the repository’s default branch (usually master).
    To get contents: 
    */
    def repo_Get_File_Content( 
        owner: String = "BugScanTeam",
        repository_Name: String = "DNSLog",
        path_File_Name: String = "dnslog/zoneresolver.py",
        branch: Option[ String ] = Some("heads/master")
        //1 == 0 
    ): String = {
        val getContents = Github(accessToken)
            .repos
            .getContents(
                //>"47deg", "github4s", "README.md", Some("heads/master")
                // owner,  repository name, path + file name, branch 
                //"BugScanTeam", "DNSLog", "dnslog/zoneresolver.py", Some("heads/master")
                owner, repository_Name, path_File_Name, branch
            )
    /*
    case class Content(
    `type`: String,
    encoding: Option[String],
    target: Option[String],
    submodule_git_url: Option[String],
    size: Int,
    name: String,
    path: String,
    content: Option[String],
    sha: String,
    url: String,
    git_url: String,
    html_url: String,
    download_url: Option[String])
    
    NonEmptyList(
        Content(
            file,
            Some(base64),
            None,None,
            2543,
            README.md,
            README.md,
            Some(Clt...HlyaWdodCk=),
            46fdc37e7122d5084c796a34e383a77b74d2d6a4,
            https://api.github.com/repos/47deg/github4s/contents/README.md?ref=heads/master,
            https://api.github.com/repos/47deg/github4s/git/blobs/46fdc37e7122d5084c796a34e383a77b74d2d6a4,
            https://github.com/47deg/github4s/blob/heads/master/README.md,
            Some(https://raw.githubusercontent.com/47deg/github4s/heads/master/README.md)
        )
    )
    */
        getContents.exec[ cats.Id, HttpResponse[String] ]() match {
            case Left(e) => {
                //println(s"Something went wrong: ${e.getMessage}") 
                e.getMessage
            }
            // The result on the right 
            // is the corresponding NonEmptyList[Content].
            case Right(r) => {
                // return ?
                r.result match {
                /*
constructor cannot be instantiated to expected type;
[error]  found   : github4s.free.domain.Content
[error]  required: cats.data.NonEmptyList[github4s.free.domain.Content]
                */
                    // final case class NonEmptyList[+A](head: A, tail: List[A]) extends Product with Serializable
                    case NonEmptyList( content_Container @ Content(
                        // to turn the ( lower cased ) pattern into a stable identifier pattern
                        // work around, here for reserved word with `backticks`
                        // with scalaVersion := "2.11.9" -> get:
                        //? Pattern variables must start with a lower-case letter. (SLS 8.1.1.)
                        //`type`: String,
                        type_Str: String,
                        encoding: Option[String],
                        target: Option[String],
                        submodule_git_url: Option[String],
                        size: Int,
                        name: String,
                        path: String,
                        content: Option[String],
                        sha: String,
                        url: String,
                        git_url: String,
                        html_url: String,
                        download_url: Option[String] 
                    ), _ ) => if( content.nonEmpty ){
                        //println(s"${name} decoded_Content:\n${decoded_Content}")
                        content.get
                    }else{""}
                    case _ => {
                        println(s"""Unexpeced Github.repos.getContents():\n${r.result}""") 
                        r.result.toString
                    }
                }
            }
        }
    }
}
