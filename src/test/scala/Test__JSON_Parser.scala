package big_data
//package demo

import com.fortysevendeg.lambdatest._

import java.util.Base64
import java.util.Base64.Decoder

import scala.io.BufferedSource

import JSON_Parser.{ 
    File_Props, get_Field_Name, 
    get_Field_Value, 
    file_Props_Json_Parser, 
    map_File_Props_Json,
    get_Current_Tree_Children_Props_Iterator,
    drop_Chars_While_Word_Not_Found,
    take_Until_Char,
    skip_After_Char
}
import GitHub_Repo_Content.{ 
    repo_Get_File_Content,
    decode_Base64_Content, 
    drop_Empty_Lines_And_Trailing_Spaces_From_Content, 
    get_Repo_Owner_And_Name_From_URL,
    get_Repo_Master_Tree_Root_URL,
    get_Repo_Files_Paths_Names_Iterator
}

/** 
> show test:definedTestNames 
*/
class Test_JSON_Parser extends LambdaTest {

    //val file_Content_Url = "https://api.github.com/repos/pirate/crypto-trader/contents/symbols.py"
    //# prettified ( actual respons is without spaces and lines ):
    // prettified
    // def scala.io.Source.fromFile(file: File, enc: String): BufferedSource
    // def fromFile(name: String, enc: String): BufferedSource
    val test_Input_1_Path_Name = "./src/test/resources/test_input_1.json"
    val test_Input_1 = scala.io.Source.fromFile(
        // required: java.io.File
        //file 
        name = "./src/test/resources/test_input_1.json", 
        enc = "UTF8" 
    )
    /*
    val test_Input_BS = new scala.io.BufferedSource( 
            //?test_Input 
            //scala.io.Source.fromString( test_Input )
            // required: java.io.InputStream
            new java.io.ByteArrayInputStream(
                // required: Array[Byte]
                test_Input.getBytes()
            )
        )(scala.io.Codec.UTF8)
    val test_Input_SBS = scala.io.Source
            .fromString( test_Input )
    */
    val test_Input_2_Path_Name = "./src/test/resources/test_input_2.json"
    // unprettified
    val test_Input_2 = scala.io.Source.fromFile(
        name = test_Input_2_Path_Name, 
        enc = "UTF8" 
    )
    /*
    println( "unprettified test_Input:" )
    // def filterNot(p: (Char) ⇒ Boolean): collection.Iterator[Char]
    println( test_Input.filter( (c: Char) => c != '\n' && c != ' ' ) )
    */

    val act = /*label("Initial Tests") {
        test("Eq test") {
        assertEq(2 + 1, 3, "Int eq test")
        }
    } +
    label("Simple Tests") {
        test("Assert Test") {
        assertEq(1, 2, "Bad Int eq test") +
        assert(3 == 5 - 2, "should work")
        }
    } + */
    label(
        "Json_Parser Tests"//, tags = Set( "SKIP" , "ignore" ) 
    ) {
        test(
            "drop_Chars_While_Word_Not_Found test"//, tags = Set( "SKIP" , "ignore" )
        ) {
            val actual_Result: BufferedSource = drop_Chars_While_Word_Not_Found(
                word = "tree",
                word_Size = 4,
                chars_Iterator = scala.io.Source.fromFile(
                    name = "./src/test/resources/response_to_repos_owner_repo_branches_master_url.json", 
                    enc = "UTF8" 
                )//.buffered
            )
            
            assert(actual_Result.nonEmpty, "Expected to true") + 
            assertEq(
                actual_Result
                    .take(7)
// scala> Set( 'a', 'b', 'c' )('a')
// res4: Boolean = true
// scala> Set( 'a', 'b', 'c' )('d')
// res5: Boolean = false
// scala> Set( 'a', 'b', 'c' ).contains('d')
// res6: Boolean = false
                    .filterNot( Set( '\n', '\t', ' ' )( _ ) )
                    .mkString,
                    //?.toString(), 
                //?"\": {\n", 
                "\":{",
                "Expected to be equal"
            )
        } + 
        test(
            "extract url with drop_Chars_While_Word_Not_Found test"//, tags = Set( "SKIP" , "ignore" )
        ) {
            val after_Tree: BufferedSource = drop_Chars_While_Word_Not_Found(
                word = "tree",
                word_Size = 4,
                chars_Iterator = scala.io.Source.fromFile(
                    name = "./src/test/resources/response_to_repos_owner_repo_branches_master_url.json", 
                    enc = "UTF8" 
                )//.buffered
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
            val actual_Result: String = take_Until_Char( 
                stop_At = '"', 
                chars_Iterator = after_Url
            )
            
            assertEq(
                actual_Result,
                "https://api.github.com/repos/pirate/crypto-trader/git/trees/26e721b5e45fab0b7ba722e56136fef58a696724",
                "Expected to be equal"
            )
        } + 
        test(
            "Json object field and name extractors test"//, tags = Set( "SKIP" , "ignore" )
        ) {
            println( "#" * 80 )
            println( "test_Input_1:" )
            println( test_Input_1 )
            println( "#" * 80 )
            // def scala.io.Source.createBufferedSource(
            //  inputStream: InputStream, bufferSize: Int = DefaultBufSize, reset: () ⇒ Source = null, close: () ⇒ Unit = null)(implicit codec: Codec): BufferedSource
            //  Reads data from inputStream with a buffered reader, 
            //  using the encoding in implicit parameter codec.
            
            // InputStream inputstream = new FileInputStream("c:\\data\\input-text.txt");
            // ByteArrayInputStream(byte[] buf)
            val ( f_1_N, b_It ) = get_Field_Name( 
                //>test_Input_BS
                //>test_Input_SBS
                //>
                test_Input_1
                //>test_Input_2
                    .buffered,
                is_DeBug_Mode = 1 == 0
            )
            //println( s"f_1_N: `${f_1_N}`" )
            val ( f_1_V, _ ) = get_Field_Value( b_It, is_DeBug_Mode = 1 == 0 )
            //println( s"f_1_V: `${f_1_V}`" )
            assertEq(f_1_N, "name", "Expected to be equal") + 
            assertEq(f_1_V, "symbols.py", "Expected to be equal")
        } + 
        test(
            "Extract file_Props"//, tags = Set( "SKIP" , "ignore" ) 
        ) {
            println( "Parsing test_Input:" )
            //println( test_Input )
            println( "Results:" )
            val extracted_Props = file_Props_Json_Parser(
                scala.io.Source.fromFile(
                    name = "./src/test/resources/test_input_1.json", 
                    enc = "UTF8" 
                ).buffered,
                is_DeBug_Mode = 1 == 0
            )
            //println( s"extracted_Props: ${extracted_Props}" )
            val expected_Result = File_Props(
                name= "symbols.py",
                path = "symbols.py",
                size = 6824,
                `type` = "file",
                content = "IiIiCkN1cnJlbmN5IHR5cGVzIGFzIGRlZmluZWQgaGVyZToKICAgIGh0dHBz\n...ICAgICAgICAgICAgICAgICAgICAgCicnJywKfQo=\n"
            )
            
            assertEq(extracted_Props, expected_Result, "Expected to be equal")
        } + 
        test(
            "Map file_Props"//, tags = Set( "SKIP" , "ignore" ) 
        ) {
            val extracted_Props = map_File_Props_Json(
                scala.io.Source.fromFile(
                    name = "./src/test/resources/test_input_1.json", 
                    enc = "UTF8" 
                ).buffered,
                is_DeBug_Mode = 1 == 0
            )
            //println( s"extracted_Props: ${extracted_Props}" )
            val expected_Result = Map(
                "name" -> "symbols.py",
                "path" -> "symbols.py",
                "size" -> "6824",
                "type" -> "file",
                "content" -> "IiIiCkN1cnJlbmN5IHR5cGVzIGFzIGRlZmluZWQgaGVyZToKICAgIGh0dHBz\n...ICAgICAgICAgICAgICAgICAgICAgCicnJywKfQo=\n"
            )
            
            assertEq(
                extracted_Props("name"), expected_Result("name"), 
                "Expected to be equal"
            ) + 
            assertEq(
                extracted_Props("content"), expected_Result("content"), 
                "Expected to be equal"
            )
        } 
    } + 
    label(
        "Content Json Tests"//, tags = Set( "ignore" ) 
    ) {
        test(
            "get encoded file Content", tags = Set( "ignore" )
        ) {
            val encoded_File_Content: String = repo_Get_File_Content( 
                owner = "pirate",//"BugScanTeam",
                repository_Name = "crypto-trader",//"DNSLog",
                path_File_Name = "symbols.py",//"dnslog/zoneresolver.py",
                branch = Some("heads/master")
                //1 == 0 
            )
            val file_Content_Url = "https://api.github.com/repos/pirate/crypto-trader/contents/symbols.py"
            val extracted_Props = map_File_Props_Json(
                scala.io.Source
                    .fromURL(s = file_Content_Url, enc = "UTF8" )
                    .buffered,
                is_DeBug_Mode = 1 == 0
            )
            val content_Base64: String = extracted_Props("content")
            
            assertEq(content_Base64, encoded_File_Content, "Expected to be equal")
        } + 
        test(
            "decode file Content", tags = Set( "ignore" )
        ) {
            val file_Content_Url = "https://api.github.com/repos/pirate/crypto-trader/contents/symbols.py"
            val extracted_Props = map_File_Props_Json(
                scala.io.Source
                    .fromURL(s = file_Content_Url, enc = "UTF8" )
                    .buffered,
                is_DeBug_Mode = 1 == 0
            )
            println( "extracted_Props:" )
            println( extracted_Props )
            println( "*#" * 40 )
            val decoded_Content: String = /*new String(
                Base64
                    .getMimeDecoder
                    // Unexpected exception: Input byte array has wrong 4-byte ending unit
                    .decode( extracted_Props("content") )
            )*/
                decode_Base64_Content( extracted_Props("content") )
            println( "Normalized file content: without empty strings and trailing spaces:" )
            println(
                drop_Empty_Lines_And_Trailing_Spaces_From_Content( decoded_Content ) )
            println( "#*" * 40 )
            val expected_Result = Map(
                "lines" -> 208,
                "size" -> "6824",
                "content" -> "Currency types as defined here:"
            )
            
            assertEq(
                decoded_Content
                    .lines
                    .toList
                    .size, expected_Result("lines"), 
                "Expected to be equal"
            ) + 
            assertEq(
                decoded_Content
                    //.take(80), 
                    .linesIterator.drop(1).next(),
                    expected_Result("content"), 
                "Expected to be equal"
            )
        }
    } + // get_Repo_Owner_And_Name_From_URL
    label(
        "Utils Tests"//, tags = Set( "SKIP" , "ignore" ) 
    ) {
        test("extract Repo_Owner_And_Name_From_URL Test") {
            val url = "https://github.com/BugScanTeam/DNSLog"
            val ( owner, name ) = get_Repo_Owner_And_Name_From_URL( url )
        
            assertEq( owner, "BugScanTeam", "Expected to be equal") +
            assertEq( name, "DNSLog", "Expected to be equal")
            //assert(3 == 5 - 2, "should work")
        } + 
        test("get Repos urls From CSV file Test") {
            val repos_URLs_BufferedSource: scala.io.BufferedSource = scala.io.Source
                .fromFile(
                    name = "url_list.csv", 
                    enc = "UTF8" 
                )
            // def getLines(): collection.Iterator[String]
            //  Returns an iterator 
            //  who returns lines (NOT including newline character(s)).
            val repos_URLs_Iterator: collection.Iterator[String] = repos_URLs_BufferedSource
                .getLines()
            val csv_Header: String = repos_URLs_Iterator.next()
            val top_9_Repos_List: List[String] = repos_URLs_Iterator
                .take(9)
                .toList
            
            assertEq( 
                csv_Header, 
                "URLs", 
                "Expected to be equal"
            ) +
            assertEq( 
                top_9_Repos_List.head, 
                "https://github.com/bitly/data_hacks", 
                "Expected to be equal"
            ) +
            assertEq( 
                top_9_Repos_List.last, 
                "https://github.com/Cisco-Talos/ROPMEMU", 
                "Expected to be equal" 
            )
        } + 
        test(
            "get_Repo_Master_Tree_Root_URL Test", tags = Set( "SKIP" , "ignore" ) 
        ) {
            assertEq( 
                get_Repo_Master_Tree_Root_URL(
                    master_Url = "https://api.github.com/repos/pirate/crypto-trader/branches/master"
                ).get, 
                "https://api.github.com/repos/pirate/crypto-trader/git/trees/26e721b5e45fab0b7ba722e56136fef58a696724",
                "Expected to be equal" 
            ) + 
            assertEq( 
                get_Repo_Master_Tree_Root_URL(
                    master_Url = "https://api.github.com/repos/bitly/data_hacks/branches/master"
                ).get,
                "https://api.github.com/repos/bitly/data_hacks/git/trees/994b441daecddd98c3b313a288c1ae0611e56439",
                "Expected to be equal" 
            )
        } + 
        test(
            "get_Current_Tree_Children_Props_Iterator Test"//, 
            //tags = Set( "SKIP" , "ignore" ) 
        ) {
            val trees_BufferedSource: scala.io.BufferedSource = scala.io.Source
                .fromFile(
                    name = "./src/test/resources/repo_git_trees_url_response.json", 
                    enc = "UTF8" 
                )
            val tree_URL = "https://api.github.com/repos/bitly/data_hacks/git/trees/994b441daecddd98c3b313a288c1ae0611e56439"
            val trees_Items_Iter = get_Current_Tree_Children_Props_Iterator( 
                //tree_URL = "https://api.github.com/repos/bitly/data_hacks/git/trees/994b441daecddd98c3b313a288c1ae0611e56439"
                github_API_Response_Buffered_Source = //>
                    trees_BufferedSource
                    //>scala.io.Source.fromURL(s = tree_URL, enc = "UTF8" )
            )
            
            assertEq( 
                trees_Items_Iter.next(), 
                //( "path", "type", "sha" ),
                ( ".gitignore", "blob", "9d0b71a3c79d2d3afbfa99269fea4280f5e73344" ),
                "Expected to be equal" 
            ) + 
            assertEq( 
                // Unexpected exception: next on empty iterator
                trees_Items_Iter.next(), 
                ( "README.markdown", "blob", "053dae1ad11e6b864a97031430abd4d470c0ea34" ),
                "Expected to be equal" 
            ) + 
            assertEq( 
                trees_Items_Iter.next(), 
                ( "data_hacks", "tree", "4cf5df18ec0052abf4d561c992f138ac144f8304" ),
                "Expected to be equal" 
            ) + 
            assertEq( 
                trees_Items_Iter.next(), 
                ( "setup.py", "blob", "36fa158820c95e26d816f8f1bbfd962e1a482df9" ),
                "Expected to be equal" 
            ) + 
            assertEq( 
                trees_Items_Iter.hasNext, 
                false,
                "Expected to be equal" 
            ) 
        } + 
        test(
            "get_Repo_Files_Paths_Names_Iterator Test", tags = Set( "SKIP" , "ignore" ) 
        ) {
            val ( repo_Files_Iter, repo_Files_Iter_Copy ) = get_Repo_Files_Paths_Names_Iterator(
                repo_URL = "https://github.com/bitly/data_hacks"
            ).duplicate
            /*
$ tar -tf data_hacks__repo.tar.gz
bitly-data_hacks-c66693b/
bitly-data_hacks-c66693b/.gitignore                         1
bitly-data_hacks-c66693b/README.markdown                    2
bitly-data_hacks-c66693b/data_hacks/
bitly-data_hacks-c66693b/data_hacks/bar_chart.py            3
bitly-data_hacks-c66693b/data_hacks/histogram.py            4
bitly-data_hacks-c66693b/data_hacks/ninety_five_percent.py  5
bitly-data_hacks-c66693b/data_hacks/run_for.py              6
bitly-data_hacks-c66693b/data_hacks/sample.py               7
bitly-data_hacks-c66693b/setup.py                           8

extracted path: .gitignore, type: blob, sha: 9d0b71a3c79d2d3afbfa99269fea4280f5e73344
extracted path: README.markdown, type: blob, sha: 053dae1ad11e6b864a97031430abd4d470c0ea34
extracted path: data_hacks, type: tree, sha: 4cf5df18ec0052abf4d561c992f138ac144f8304
	appending data_hacks tree iterator
extracted path: bar_chart.py, type: blob, sha: c68af8b0a36dd63b4f1db715ecb7fc70117f3d5a
extracted path: histogram.py, type: blob, sha: 3d16cc8ea06336ccdfd5f59ead8ba606f35b9fbe
extracted path: ninety_five_percent.py, type: blob, sha: 141022706121b2f8154daf8151524be51ab803fd
extracted path: run_for.py, type: blob, sha: a8ea21fd805750461c4e376a19f1dc73aa4a0572
extracted path: sample.py, type: blob, sha: c3296ab48412690b593dae188ced2f6be2ff0caa
top_Head.isEmpty: <iterator>
extracted path: setup.py, type: blob, sha: 36fa158820c95e26d816f8f1bbfd962e1a482df9
top_Head.isEmpty: <iterator>
tree_Children_Iterators_Stack.isEmpty: List()
            */
            val expected = Map(
                "bar_chart.py" -> "1",
                "histogram.py" -> "2",
                "ninety_five_percent.py" -> "3",
                "run_for.py" -> "5",
                "sample.py" -> "6",
                "setup.py" -> "7"
            )
// scala> Map("1"->1,"2"->2).keys
// res0: Iterable[String] = Set(1, 2)
// scala> Map("1"->1,"2"->2).keys == Map("2"->2,"1"->1).keys
// res1: Boolean = true
// scala> Map("1"->1,"2"->2).keys == Map("2"->2,"3"->3).keys
// res2: Boolean = false
            
            /*assertEq( 
                repo_Files_Iter.next(), 
                ("setup.py", "26e721b5e45fab0b7ba722e56136fef58a696724"),
                "Expected to be equal" 
            ) + */
            assertEq( 
                repo_Files_Iter.hasNext, 
                true,
                "Expected to be equal" 
            ) + 
            assertEq( 
                // TraversableOnce ?
                repo_Files_Iter_Copy
                    //.toMap
                    .size, 
                expected.size,
                "Expected to be equal" 
            ) + 
            assertEq( 
                repo_Files_Iter
                    //>.take(8)
                    //.take(8)
                    .toMap
                    .keys, 
                expected.keys,
                "Expected to be equal" 
            )// + 
        }
    } 
}

object Test_JSON_Parser extends App {
  run( "Test-JSON-Parser", new Test_JSON_Parser )
}
