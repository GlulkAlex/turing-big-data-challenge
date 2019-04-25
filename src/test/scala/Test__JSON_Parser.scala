package big_data
//package demo

import com.fortysevendeg.lambdatest._
import JSON_Parser.{ 
    File_Props, get_Field_Name, get_Field_Value, file_Props_Json_Parser }

    
class Test_JSON_Parser extends LambdaTest {
    // https://api.github.com/repos/pirate/crypto-trader/contents/symbols.py
//# prettified ( actual respons is without spaces and lines ):
val test_Input = """{
  "name": "symbols.py",
  "path": "symbols.py",
  "sha": "248ffb14ea2899491d92f40c4d17586102604efa",
  "size": 6824,
  "url": "https://api.github.com/repos/pirate/crypto-trader/contents/symbols.py?ref=master",
  "html_url": "https://github.com/pirate/crypto-trader/blob/master/symbols.py",
  "git_url": "https://api.github.com/repos/pirate/crypto-trader/git/blobs/248ffb14ea2899491d92f40c4d17586102604efa",
  "download_url": "https://raw.githubusercontent.com/pirate/crypto-trader/master/symbols.py",
  "type": "file",
  "content": "IiIiCkN1cnJlbmN5IHR5cGVzIGFzIGRlZmluZWQgaGVyZToKICAgIGh0dHBz\n...ICAgICAgICAgICAgICAgICAgICAgCicnJywKfQo=\n",
  "encoding": "base64",
  "_links": {
    "self": "https://api.github.com/repos/pirate/crypto-trader/contents/symbols.py?ref=master",
    "git": "https://api.github.com/repos/pirate/crypto-trader/git/blobs/248ffb14ea2899491d92f40c4d17586102604efa",
    "html": "https://github.com/pirate/crypto-trader/blob/master/symbols.py"
  }
}"""
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
    // prettified
    // def scala.io.Source.fromFile(file: File, enc: String): BufferedSource
    // def fromFile(name: String, enc: String): BufferedSource
    val test_Input_1 = scala.io.Source.fromFile(
        // required: java.io.File
        //file 
        name = "./src/test/resources/test_input_1.json", 
        enc = "UTF8" 
    )
    // unprettified
    val test_Input_2 = scala.io.Source.fromFile(
        name = "./src/test/resources/test_input_2.json", 
        enc = "UTF8" 
    )
    println( "unprettified test_Input:" )
    println( test_Input.filter( (c: Char) => c != '\n' && c != ' ' ) )
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
        //>test_Input_1
        test_Input_2
            .buffered
    )
    println( s"f_1_N: `${f_1_N}`" )
    val ( f_1_V, _ ) = get_Field_Value( b_It )
    println( s"f_1_V: `${f_1_V}`" )
    println( "Parsing test_Input:" )
    println( test_Input )
    println( "Results:" )
    println( 
        file_Props_Json_Parser(
            new scala.io.BufferedSource( 
            new java.io.ByteArrayInputStream( test_Input.getBytes() )
        )(scala.io.Codec.UTF8).buffered ) )

  val act = label("Initial Tests") {
    test("Eq test") {
      assertEq(2 + 1, 3, "Int eq test")
    }
  } +
  label("Simple Tests") {
    test("Assert Test") {
      assertEq(1, 2, "Bad Int eq test") +
      assert(3 == 5 - 2, "should work")
    }
  }
}

object Test_JSON_Parser extends App {
  run( "Test-JSON-Parser", new Test_JSON_Parser )
}
