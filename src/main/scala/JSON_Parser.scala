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
        field_End_Delimiter: Char = ':',//Set( ',', '}' )
        is_DeBug_Mode: Boolean = 1 == 1
    ): ( String, collection.BufferedIterator[Char] ) = if(
        buffered_Field_Iter.isEmpty
    ){
        if(is_DeBug_Mode){println(s"\tfield_Result: ${field_Result}")}
        ( 
            //.stripPrefix("{").trim().stripPrefix("\"").stripSuffix("\"")
            field_Result
                /*.stripPrefix("{")
                .trim()
                .stripMargin('{')
                .trim()
                .stripPrefix("\"")
                .stripSuffix("\"")*/
                .dropWhile( _ != '"' ).tail.takeWhile( _ != '"' ),
            buffered_Field_Iter 
        )
    }else{
        // .head Returns next element of iterator without advancing beyond it.
        val char = buffered_Field_Iter.next()
        
        if( field_End_Delimiter == char ){
            if(is_DeBug_Mode){println(s"\tfield_Result: ${field_Result}")}
            ( 
                field_Result.dropWhile( _ != '"' ).tail.takeWhile( _ != '"' ), 
                buffered_Field_Iter 
            )
        }else{
            get_Field_Name(
                buffered_Field_Iter = buffered_Field_Iter,
                field_Result = field_Result + char
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
        value_End_Delimiters: Set[Char] = Set( ',', '}' ),
        is_DeBug_Mode: Boolean = 1 == 1
    ): ( String, collection.BufferedIterator[Char] ) = if(
        buffered_Value_Iter.isEmpty
    ){
        if(is_DeBug_Mode){println(s"\tvalue_Result: ${value_Result}")}
        ( 
            value_Result
                /// works for `string` fails for `boolean` or `number`
                //?.dropWhile( _ != '"' ).tail.takeWhile( _ != '"' ),
                .trim()
                .stripPrefix("\"")
                .stripSuffix("\""), 
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
                value_Result
                    //?.dropWhile( _ != '"' ).tail.takeWhile( _ != '"' ), 
                    .trim()
                    .stripPrefix("\"")
                    .stripSuffix("\""), 
                buffered_Value_Iter 
            )
        }else{
            get_Field_Value(
                buffered_Value_Iter = buffered_Value_Iter,
                value_Result = value_Result + char,
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
        fields_List: List[String] = List(
            "name",
            "path",
            "size",
            "type",
            "content" ),
        result: File_Props = File_Props(),
        is_DeBug_Mode: Boolean = 1 == 1
    ): File_Props = if(
        fields_List.isEmpty || buffered_Source_Iter.isEmpty
    ){
        result
    }else{
        val ( name: String, _ ) = get_Field_Name(
                buffered_Field_Iter = buffered_Source_Iter
            )
        val ( value: String, _ ) = get_Field_Value(
                buffered_Value_Iter = buffered_Source_Iter
            )
        val field_Name: String = fields_List.head
        if(is_DeBug_Mode){println(s"extracted field name: ${name}")}
        if(is_DeBug_Mode && 1 == 1){println(value)}
        val next_Result = //if( name == field_Name ){
            // and without freaking reflections
            name match {
                case "name" => result.copy( name = value )
                case "path" => result.copy( path = value )
                case "size" => result.copy( size = value.toInt )
                case "type" => result.copy( 
                        `type` = value 
                        //type_Str = value 
                    )
                case "content" => result.copy( content = value )
                case _ => result
        //}else{
        }
        
        file_Props_Json_Parser(
            buffered_Source_Iter = buffered_Source_Iter, 
            fields_List = fields_List.tail,
            result = next_Result,
            is_DeBug_Mode = is_DeBug_Mode
        )
    }
}
