package demo

import com.fortysevendeg.lambdatest._


class Test JSON_Parser extends LambdaTest {
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
    // InputStream inputstream = new FileInputStream("c:\\data\\input-text.txt");
    // ByteArrayInputStream(byte[] buf)
    val ( f_1_N, b_It ) = get_Field_Name( 
        //scala.io.Source.fromString( 
        new scala.io.BufferedSource( 
            //?test_Input 
            //scala.io.Source.fromString( test_Input )
            // required: java.io.InputStream
            new java.io.ByteArrayInputStream(
                // required: Array[Byte]
                test_Input.getBytes()
            )
        )(scala.io.Codec.UTF8).buffered 
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

object Example extends App {
  run("example", new Example)
}
