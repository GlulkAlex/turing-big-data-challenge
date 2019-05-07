package big_data

import scala.io.BufferedSource


/**
it has to try to parse (valid) json 
or fail miserably in the process
by building Map like this
Map(
 root -> [empty or with list children] or {empty or with object children}
)
// recursive (type) definition:
[] -> array | list container 
  direct children are scalars: null | boolean | integer | float | strings
  or other containers;
{} -> object container
  direct children are named | has "<field name>" string 
  then after ':' delimiter any scalar or container;

Example:
list:
[ <- start of top most container
  true, <- add item to current container, probably with type check 
  42,
  "Ok",
  start nested container and make it current container 
  |close and add nested container item and return to parent as current container 
  ||
  vv
  [], <- get ready for the next item
     start key
     |          end key         add ( key -> value ) pair to current container 
     |          |               |
     v          v               v
  { "nested list": [ 22.5, null ] } <- close nested container
]
// parent probably redundant | unnecessary 
// because tree traversal has to be implemented anyway 
// what about elements keys | ids ?
will be: Map( root -> parent: None, (type: list,) value: List[
//  children list: (	
    parent: root, value: true,
    parent: root, value: 42,
    parent: root, value: "Ok",
    parent: root, value: List[],
    parent: root, value: Map{ 
	"nested list" -> List[ 22.5, null ] 
    }
//  )
] )
object:
{ 
  "nested list": [ 22.5, null ], 
  "integer": -1,
  "nested object": {}, // <- handle: allow or forbid trailin ',' commas ?
}
will be: Map( "root" -> Map { 
  "nested list" -> [ 22.5, null ], 
  "integer" -> -1,
  "nested object" -> Map{}, 
}
)

[from:](https://github.com/47deg/LambdaTestCapture#example-json)
Json objects are represented as
    Map[String,Any] 
and Json arrays are represented as 
    List[Any]
*/
object JSON_Parser {
    /** 
    main operation is to 
    compare current_Buffer with 
    word prefix 
    until both are identical 
    so it has to be fast 
    not just linear scan 
    
    something similar DNA strings matching dynamic algorithms 
    example:
      "abc"
          "abc"
                    "abc"
    "dfagkgabgklbctyoabcdfg"
                     ___
    */
    @scala.annotation.tailrec
    def drop_Chars_While_Word_Not_Found( 
        // must support fast [append | push right] and fast [tail | pop left]
        // or pop-push in one step | operation 
        // or word sized rotating collection - get last -> drop first 
// scala> val sb = new StringBuilder(capacity = 3)
// sb: StringBuilder =
// scala> sb.append('a')
// res5: StringBuilder = a
// scala> sb.append('b')
// res6: StringBuilder = ab
// scala> sb.append('c')
// res7: StringBuilder = abc
// scala> sb.append('d')
// res8: StringBuilder = abcd
// scala> sb.tail
// res9: StringBuilder = bcd
// scala> sb.head
// res10: Char = a
// scala> sb == "abcd"
// res11: Boolean = false
// scala> sb.result() == "abcd"
// res12: Boolean = true
        // keeping it just for debugging | checking of correctness 
        current_Buffer: String = "", 
        buffer_Size: Int = 0,
        word: String,// = "tree",
        word_Size: Int,// = 4
        last_Matched_Char_Index: Int = -1,
        // mutated state 
        chars_Iterator: scala.io.BufferedSource
    ): BufferedSource = if( 
        //current_Buffer == word 
        buffer_Size == word_Size
        || chars_Iterator.isEmpty 
    ){
        chars_Iterator
    }else{
        if( chars_Iterator.hasNext ){
            val current_Char: Char = chars_Iterator.next()
            // char java.lang.String.charAt(int index)
            //  Returns the char value at the specified index.
            val word_Next_Char: Char = word.charAt(last_Matched_Char_Index + 1)
            
            val (
                next_Buffer,
                next_Buffer_Size,
                next_Matched_Char_Index
            ) = if( current_Char == word_Next_Char ){
                (
                    current_Buffer + current_Char,
                    buffer_Size + 1,
                    last_Matched_Char_Index + 1
                )
            }else{
                // reset 
                ( "", 0, -1 )
            }
            
            drop_Chars_While_Word_Not_Found( 
                current_Buffer = next_Buffer, 
                buffer_Size = next_Buffer_Size,
                word = word,
                word_Size = word_Size,
                last_Matched_Char_Index = next_Matched_Char_Index,
                chars_Iterator = chars_Iterator
            )
        }else{
            // empty
            chars_Iterator
        }
    }
    /**
    */
    case class File_Props(
        name: String = "",//"symbols.py",
        path: String = "",//"symbols.py",
        size: Int = 0,//6824,
        //>file_Type: String,//"file",
        // parser stops here ? like never finds the match ?
        // no it stops after first url 
        `type`: String = "",
        // fails to compile 
        //!type_: String = "",
        //type_Str: String = "",
        content: String = "",
        //encoding: String//"base64",
    )
    // scala.io.BufferedSource
    // buffered: collection.BufferedIterator[Char]
    //  Creates a buffered iterator from this iterator.
    //val repo_Master_Tree_Root_URL: String = github_API_Response_Source
    /** assuming field names do not contain ':' 
    */
    @scala.annotation.tailrec
    def get_Field_Name(
        buffered_Field_Iter: collection.BufferedIterator[Char],
        field_Result: String = "",
        //?field_End_Delimiter: Char = ':',//Set( ',', '}' )
        field_Quote: Char = '"',
        formatters: Set[Char] = Set( '{', ' ', '\t', '\n' ),
        is_DeBug_Mode: Boolean = 1 == 0
    ): ( String, collection.BufferedIterator[Char] ) = if(
        buffered_Field_Iter.isEmpty
    ){
        if(is_DeBug_Mode){println(s"\t\tbuffered_Field_Iter.isEmpty, field name: `${field_Result}` returned")}
        ( 
            //.stripPrefix("{").trim().stripPrefix("\"").stripSuffix("\"")
            field_Result,
                /*.stripPrefix("{")
                .trim()
                .stripMargin('{')
                .trim()
                .stripPrefix("\"")
                .stripSuffix("\"")*/
                //.dropWhile( _ != '"' ).tail.takeWhile( _ != '"' ),
            buffered_Field_Iter 
        )
    }else{
        // .head Returns next element of iterator without advancing beyond it.
        val char = buffered_Field_Iter.next()
        if(is_DeBug_Mode){println(s"\tchar: '${char}', field_Result: `${field_Result}`")}
        
        if( 
            //?field_End_Delimiter == char 
            char == field_Quote && field_Result.nonEmpty//length > 0
        ){
            if(is_DeBug_Mode){println(s"\t\tfield name: `${field_Result}` extracted")}
            ( 
                field_Result,//?.dropWhile( _ != '"' ).tail.takeWhile( _ != '"' ), 
                buffered_Field_Iter 
            )
        }else{
            val next_Field_Result = if( 
                formatters.contains( char ) 
            ){
                if(is_DeBug_Mode){println(s"""\t\tchar: '${char}' in formatters: """// + raw"""${formatters.mkString("[","|","]")}"""
                )}
                field_Result 
            }else if(char == field_Quote){
                if(is_DeBug_Mode){println(s"\t\tdropping opening quote: '${char}'")}
                field_Result 
            }else{
                if(is_DeBug_Mode){println(s"\t\t${field_Result} + ${char}")}
                field_Result + char
            }
            
            get_Field_Name(
                buffered_Field_Iter = buffered_Field_Iter,
                field_Result = next_Field_Result,
                is_DeBug_Mode = is_DeBug_Mode
            )
        }
    }
    
    /** assuming field values do not contain ',' and '}' 
    /// @toDo: it fails on "size": 6824 because of numerical value 
    /// that is not enclosed in quotes ?
    */
    @scala.annotation.tailrec
    def get_Field_Value(
        buffered_Value_Iter: collection.BufferedIterator[Char],
        value_Result: String = "",
        value_Start_Delimiter: Char = ':',
        value_End_Delimiters: Set[Char] = Set( ',', '}' ),
        // '\n' is valid part of string values like base64
        formatters: Set[Char] = Set( 
            ' ', '\t'//, '\n' 
        ),
        field_Quote: Char = '"',
        // ANSI escape sequences: backslash, \ the "Escape character"
        //escaped_Symbol
        backslash: Char = '\\',
        is_DeBug_Mode: Boolean = 1 == 0
    ): ( String, collection.BufferedIterator[Char] ) = if(
        buffered_Value_Iter.isEmpty
    ){
        if(is_DeBug_Mode){println(s"\tbuffered_Value_Iter.isEmpty returning value_Result: ${value_Result}")}
        ( 
            value_Result,
                /// works for `string` fails for `boolean` or `number`
                //?.dropWhile( _ != '"' ).tail.takeWhile( _ != '"' ),
                /*.trim()
                .stripPrefix("\"")
                .stripSuffix("\""),*/ 
            buffered_Value_Iter 
        )
    }else{
        val char = buffered_Value_Iter.next()
        /// @toDo: refactor conditional to base case ?
        if( value_Result.nonEmpty && value_End_Delimiters.contains( char ) ){
            if(is_DeBug_Mode){println(s"\tvalue_Result: ${value_Result}")}
            ( 
// extracted field name: sha
// [error] (run-main-a) java.lang.UnsupportedOperationException: empty.tail
                value_Result,
                    //?.dropWhile( _ != '"' ).tail.takeWhile( _ != '"' ), 
                    /*.trim()
                    .stripPrefix("\"")
                    .stripSuffix("\""),*/
                buffered_Value_Iter 
            )
        }else{
            val next_Value_Result = if( 
                formatters.contains( char ) 
            ){
                if(is_DeBug_Mode){println(s"""\t\tchar: '${char}' in formatters: """// + raw"""${formatters.mkString("[","|","]")}"""
                )}
                value_Result
            }else if(char == value_Start_Delimiter){
                if(is_DeBug_Mode){println(s"\t\tdropping leading: '${value_Start_Delimiter}'")}
                value_Result
            }else if(char == field_Quote){
                if(is_DeBug_Mode){println(s"\t\tdropping quote: '${char}'")}
                value_Result
            /*}else if(char == escaped_Symbol){
                if(is_DeBug_Mode){println(s"\t\t${value_Result} + '\''")}
                value_Result + "\\"*/
            }else if(
                char == backslash 
                // Returns next element of iterator without advancing beyond it.
                && buffered_Value_Iter.head == 'n'
            ){
                // consume next 'n'
                buffered_Value_Iter.next()
                if(is_DeBug_Mode){println(s"\t\t${value_Result} + line feed")}
                value_Result + "\n"
            }else{
                if(is_DeBug_Mode){println(s"\t\t${value_Result} + ${char}")}
                value_Result + char
            }
            
            get_Field_Value(
                buffered_Value_Iter = buffered_Value_Iter,
                value_Result = next_Value_Result,
                is_DeBug_Mode = is_DeBug_Mode
            )
        }
    }
    /**
          |             |
    "name": "symbols.py",
    after '{' | ','
    until ':' is field name
    then until ','| '}' field's content 
    */
    @scala.annotation.tailrec
    def file_Props_Json_Parser(
        //url_Source: scala.io.BufferedSource
        buffered_Source_Iter: collection.BufferedIterator[Char], 
        /*fields_List: List[String] = List(
            "name",
            "path",
            "size",
            "type",
            "content" 
        ),*/
        //extracted_Fields_Count: Int = 0,
        //fields_Total: Int = 5,
        result: File_Props = File_Props(),
        is_DeBug_Mode: Boolean = 1 == 0
    ): File_Props = if(
        // it stops right after the last fields_List item is poped
        // assuming nonEmpty field value ? 
        // ( not true in general as empty string is perfectly valid )
        //?fields_List.isEmpty
        //extracted_Fields_Count > 5 || 
        buffered_Source_Iter.isEmpty
    ){
        result
    }else{
        val ( name: String, _ ) = get_Field_Name(
                buffered_Field_Iter = buffered_Source_Iter,
                is_DeBug_Mode = is_DeBug_Mode
            )
        val ( value: String, _ ) = get_Field_Value(
                buffered_Value_Iter = buffered_Source_Iter,
                is_DeBug_Mode = is_DeBug_Mode
            )
        //>val field_Name: String = fields_List.head
        if(is_DeBug_Mode){println(s"extracted field name: ${name}")}
        if(is_DeBug_Mode && 1 == 1){println(value)}
        val //( 
            next_Result: File_Props =//, 
            //next_Extracted_Fields_Count: Int 
        //) = //if( name == field_Name ){
            // and without pesky reflections ?
            // but very hardcoded
            name match {
                case "name" => //(
                    result.copy( name = value )//, extracted_Fields_Count + 1)
                case "path" => //(
                    result.copy( path = value )//, extracted_Fields_Count + 1)
                case "size" => //(
                    result.copy( size = value.toInt )//, extracted_Fields_Count + 1)
                case "type" => //( 
                    result.copy( 
                        `type` = value 
                        //type_Str = value 
                    )//, extracted_Fields_Count + 1)
                case "content" => //( 
                    result.copy( content = value )//, extracted_Fields_Count + 1)
                case _ => //( 
                    result//, extracted_Fields_Count )
        //}else{
        }
        
        if( name == "content" ){
            next_Result
        }else{
        file_Props_Json_Parser(
            buffered_Source_Iter = buffered_Source_Iter, 
            //fields_List = fields_List.tail,
            //extracted_Fields_Count = next_Extracted_Fields_Count,
            result = next_Result,
            is_DeBug_Mode = is_DeBug_Mode
        )
        }
    }
    
    @scala.annotation.tailrec
    def map_File_Props_Json(
        buffered_Source_Iter: collection.BufferedIterator[Char], 
        result: Map[ String, String ] = Map(),
        is_DeBug_Mode: Boolean = 1 == 0
    ): Map[ String, String ] = if(
        buffered_Source_Iter.isEmpty
    ){
        result
    }else{
        val ( name: String, _ ) = get_Field_Name(
                buffered_Field_Iter = buffered_Source_Iter,
                is_DeBug_Mode = is_DeBug_Mode
            )
        val ( value: String, _ ) = get_Field_Value(
                buffered_Value_Iter = buffered_Source_Iter,
                is_DeBug_Mode = is_DeBug_Mode
            )
        if(is_DeBug_Mode){println(s"extracted field name: ${name}")}
        if(is_DeBug_Mode && 1 == 1){println(value)}
        val next_Result: Map[ String, String ] = result + ( name -> value )
        
        if( name == "content" ){
            next_Result
        }else{
            map_File_Props_Json(
                buffered_Source_Iter = buffered_Source_Iter, 
                result = next_Result,
                is_DeBug_Mode = is_DeBug_Mode
            )
        }
    }

    trait Char_Result
    object Start_From extends Char_Result
    object No_Next extends Char_Result
    case class Current_Char( c: Char ) extends Char_Result
    /** 
    ' ' -> default start from 
    '\t' -> flag indicates end of chars stream
    */
    @scala.annotation.tailrec
    def skip_After_Char( 
        c: Char = ' ', 
        stop_At: Char = ':',
        // mutated state 
        chars_Iterator: scala.io.BufferedSource
    ): Char = if( 
        c == stop_At 
    ){
        c
    }else{
        if( chars_Iterator.hasNext ){
            val current_Char = chars_Iterator.next()
            
            skip_After_Char( 
                c = current_Char, 
                // whatch it ! if passing comething different from default 
                stop_At = stop_At,
                chars_Iterator = chars_Iterator
            )
        }else{
            '\t'
        }
    }
    
    trait Field_Value_Result
    object Empty_Value extends Field_Value_Result
    object Not_Found extends Field_Value_Result
    case class Value_Accum( a: String ) extends Field_Value_Result
    
    /** 
    drop | consume | discard 'stop_At' or not ? 
    
    variants:
    - take while not found 'stop_At' ( included )
    - take until not found 'stop_At' ( excluded )
    */
    @scala.annotation.tailrec
    def take_Until_Char( 
        result: String = "", 
        c: Char = ' ', 
        stop_At: Char = ':', 
        // mutated state 
        chars_Iterator: scala.io.BufferedSource
    ): String = if( 
        c == stop_At 
    ){
        result
    }else{
        if( chars_Iterator.hasNext ){
            val current_Char: Char = chars_Iterator.next()
            val next_Result: String = if( current_Char == stop_At ){
                result
            }else{
                result + current_Char
            }
            
            take_Until_Char( 
                result = next_Result,
                c = current_Char,
                // whatch it ! if passing comething different from default 
                stop_At = stop_At,
                chars_Iterator = chars_Iterator
            )
        }else{
            ""
        }
    }
    
    /**
    extract:
        path, type, sha
    from JSON:
        { ...
     drop until [ <- initialization start | list items start tag | event 
        "tree": [ // children 
 item start { tag | event 
            { 
     drop until " <- initialization end | item fist field start 
                      : key -> value separator tag | event 
           drop until : <- path extractor start
             drop until "
                        take until " <- path extractor end 
                "path": ".gitignore",
           drop until : <- type extractor start
                     drop until ,
                "mode": "100644",
           drop until :
             drop until "
                  take until " <- type extractor end 
                "type": "blob",
          drop until : <- sha extractor start
            drop until "
                                                     take until " <- sha extractor end 
                "sha": "9d0b71a3c79d2d3afbfa99269fea4280f5e73344",
                for files only:
                    "size": 11,
                "url": "https://api.github.com/repos/bitly/data_hacks/git/blobs/..."
  item end  } tag | event if no ',' ater then list end as well | too
 drop until } <- skip to next item if any left 
    check if , or not <- hasNext item check 
            }, 
stop iterator at] <- list items stop event | flag | mark | tag
            ... ] 
        ... }
        
    so it can be combined from:
    list extractor -> item extractor -> fields and values extractor 
    */
    def get_Current_Tree_Children_Props_Iterator( 
        /// @toDo: pass scala.io.BufferedSource inside instead ?
        /// for better testing ?
        // constructor parameter
        //tree_URL: String 
        github_API_Response_Buffered_Source: scala.io.BufferedSource
    ): Iterator[ ( String, String, String ) ] = new scala.collection
        .AbstractIterator[ ( String, String, String ) ]{
        // get JSON from github API
//         val github_API_Response_Buffered_Source: scala.io.BufferedSource = 
//         scala.io.Source
//             .fromURL(s = tree_URL, enc = "UTF8" )
        // initialize: get to the first tree item 
        val response_Chars_Iterator/*: Iterator[Char]*/ = github_API_Response_Buffered_Source
            // to use .head lookup
            //?.buffered 
            // Reuse: After calling this method, one should discard the iterator it was called on, and use only the iterator that was returned.
            //.dropWhile( _ != '[' )
            //.dropWhile( _ != '"' )
            //.drop(1)
        private 
        var hasnext = response_Chars_Iterator.hasNext
        
        //@scala.annotation.tailrec
        def get_Path_Value/*( path_Val: String = "" )*/: String = {
            // mutate : Iterator[Char]
            //response_Chars_Iterator
                //.dropWhile( _ != ':' )
                //.dropWhile( _ != '"' )
                //.drop(1)
                //.takeWhile( _ != '"' )
            skip_After_Char( stop_At = ':', chars_Iterator = response_Chars_Iterator )
            skip_After_Char( stop_At = '"', chars_Iterator = response_Chars_Iterator )
            
            if( response_Chars_Iterator.hasNext ){
                //?response_Chars_Iterator.next()
                
                take_Until_Char( stop_At = '"', chars_Iterator = response_Chars_Iterator )
            }else{
                ""
            }
        }
        def get_Path_Type_Value: String = {
            // mutate : Iterator[Char]
            /*response_Chars_Iterator
                .dropWhile( _ != ':' )
                .dropWhile( _ != ':' )
                .dropWhile( _ != '"' )
                .drop(1)
                .takeWhile( _ != '"' )*/
            skip_After_Char( stop_At = ':', chars_Iterator = response_Chars_Iterator )
            
            get_Path_Value//()
        }
        def get_Path_SHA_Value: String = {
            // mutate : Iterator[Char]
            /*response_Chars_Iterator
                .dropWhile( _ != ':' )
                .dropWhile( _ != '"' )
                .drop(1)
                .takeWhile( _ != '"' )*/
            get_Path_Value//()
        }
        /**
        // advance to next entry 
        cases if next (discarding white spaces) after '}' is ','
            -> has next item
        else if ']'
            -> end of items list (Done)
        */
        def skip_To_Next_Item: Unit = {
            // mutate : Iterator[Char]
            /*response_Chars_Iterator
                .dropWhile( _ != '}' )
                // expected to stop on '"' || c != ']'
                .dropWhile( (c:Char) => c != '"' && c != ']' )*/
            skip_After_Char( stop_At = '}', chars_Iterator = response_Chars_Iterator )
        }
        
//         var path = ""
//         var path_Type = ""
//         var path_SHA = ""

        // or ends at ']'
        def hasNext: Boolean = hasnext
        
        def next(): ( String, String, String ) = if (
            hasnext
        ) { 
            //val ( current_Path, current_Path_Type, current_Path_SHA ) = ( path, path_Type, path_SHA )
            val current_Path = get_Path_Value//path
            val current_Path_Type = get_Path_Type_Value//path_Type
            val current_Path_SHA = get_Path_SHA_Value//path_SHA
            // reset
//             path = ""
//             path_Type = ""
//             path_SHA = ""
            skip_To_Next_Item
            if( response_Chars_Iterator.hasNext ){
                val c = response_Chars_Iterator.next()
                if( c != ',' ){ hasnext = false }
            }else{
                hasnext = false
            }
            
            ( current_Path, current_Path_Type, current_Path_SHA ) 
        } else {
            github_API_Response_Buffered_Source.close()
            Iterator[ ( String, String, String ) ]().next()
        }
        
        // initialization
        skip_After_Char( stop_At = '[', chars_Iterator = response_Chars_Iterator )
        skip_After_Char( stop_At = '"', chars_Iterator = response_Chars_Iterator )
    }
    
}
