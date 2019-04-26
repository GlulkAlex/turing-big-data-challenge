package big_data
//package demo

import com.fortysevendeg.lambdatest._
import java.util.Base64
import java.util.Base64.Decoder
import JSON_Parser.{ 
    File_Props, get_Field_Name, 
    get_Field_Value, file_Props_Json_Parser, map_File_Props_Json
}
import GitHub_Repo_Content.{ 
    repo_Get_File_Content,
    decode_Base64_Content, 
    drop_Empty_Lines_And_Trailing_Spaces_From_Content, 
    get_Repo_Owner_And_Name_From_URL,
    get_Repo_Master_Tree_Root_URL
}

    
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
    label("Json_Parser Tests") {
        test("Json object field and name extractors test") {
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
        test("Extract file_Props") {
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
        test("Map file_Props") {
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
    label("Content Json Tests", tags = Set( "ignore" ) ) {
        test("get encoded file Content") {
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
        test("decode file Content") {
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
    label("Utils Tests") {
        test("extract Repo_Owner_And_Name_From_URL Test") {
            val url = "https://github.com/BugScanTeam/DNSLog"
            val ( owner, name ) = get_Repo_Owner_And_Name_From_URL( url )
        
            assertEq( owner, "BugScanTeam", "Expected to be equal") +
            assertEq(name, "DNSLog", "Expected to be equal")
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
            val top_9_Repos_List: List[String] = repos_URLs_Iterator.take(9).toList
            
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
        test("get_Repo_Master_Tree_Root_URL Test") {
            assertEq( 
                get_Repo_Master_Tree_Root_URL(), 
                "https://api.github.com/repos/pirate/crypto-trader/git/trees/26e721b5e45fab0b7ba722e56136fef58a696724",
                "Expected to be equal" 
            ) + 
            assertEq( 
                get_Repo_Master_Tree_Root_URL(
                    master_Url = "https://api.github.com/repos/bitly/data_hacks/branches/master"
                ),
                "https://api.github.com/repos/bitly/data_hacks/git/trees/994b441daecddd98c3b313a288c1ae0611e56439",
                "Expected to be equal" 
            )
        }
    } 
}

object Test_JSON_Parser extends App {
  run( "Test-JSON-Parser", new Test_JSON_Parser )
}
