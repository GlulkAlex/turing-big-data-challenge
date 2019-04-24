package demodeploy

import github4s.Github
import github4s.Github._
import github4s.jvm.Implicits._
import scalaj.http.HttpResponse
// if you're using ScalaJS, replace occurrences of HttpResponse by SimpleHttpResponse
//import github4s.js.Implicits._
//import fr.hmil.roshttp.response.SimpleHttpResponse

import cats.data.NonEmptyList 
import github4s.free.domain.{ Repository, Content }


object GitHub_Repo_Content 
    extends App {
    import java.util.Base64
    import java.util.Base64.Decoder
    
    
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
            "47deg", "github4s", "README.md", Some("heads/master")
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
                    }
                case _ => println(r.result) 
            }
        }
    }
}
