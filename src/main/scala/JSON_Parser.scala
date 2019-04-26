package big_data

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
*/
object JSON_Parser {
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

}
