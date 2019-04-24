package demodeploy

import github4s.Github
import github4s.Github._
import github4s.jvm.Implicits._
import scalaj.http.HttpResponse
// if you're using ScalaJS, replace occurrences of HttpResponse by SimpleHttpResponse
//import github4s.js.Implicits._
//import fr.hmil.roshttp.response.SimpleHttpResponse

import cats.data.NonEmptyList 
import github4s.free.domain.{ Repository, Content, RepoUrls }


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
*/
object GitHub_Repo_Content 
    extends App {
    import java.util.Base64
    import java.util.Base64.Decoder
    
    
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
    val url = "https://api.github.com/repos/pirate/crypto-trader/branches/master"
    // get JSON from github API
    val github_API_Response_Source = scala.io.Source
        //? required: java.net.URL
        .fromURL(s = url, enc = "UTF8" )
    
    println( github_API_Response_Source.mkString )
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
    val getContents = Github(accessToken)
        .repos
        .getContents(
            //>"47deg", "github4s", "README.md", Some("heads/master")
            // owner,  repository name, path + file name, branch 
            "BugScanTeam", "DNSLog", "dnslog/zoneresolver.py", Some("heads/master")
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
        case Left(e) => println(s"Something went wrong: ${e.getMessage}")
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
                        // decoded just fine from UTF-8 
                        // at https://www.base64decode.org/
                        // js 
                        // var decodedData = window.atob(encodedData); 
                        // decoded the string
                        val decoded_Content: String = new String(
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
                                        .get
                                        .asInstanceOf[String]
                                )
                        )
                        println(s"${name} decoded_Content:\n${decoded_Content}")
                        println( "*" * 80 )
                        println( "Normalized: without empty strings and trailing spaces" )
                        println( 
                            decoded_Content
                                .lines
                                //?.map( _.trim() )
                                .map( _.stripSuffix(" ") )
                                .filter( _ != "" )
                                .mkString("\n") 
                            )
                    }
                case _ => println(s"""Unexpeced Github.repos.getContents():\n${r.result}""") 
            }
        }
    }
}
