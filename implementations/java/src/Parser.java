import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
//import java.util.HashMap;

import ParseTools.*;
import TokeniserTools.ChamlcToken;

class Parser {
	public static void main(String[] args) {
		if (args.length<1) {
			System.out.println("Not enough args!");
			return;
		}
		try {
			Parser par = new Parser(args[0]);
			par.parse();
		} catch (FileNotFoundException e) {
			System.out.printf("A file of the name \"%s\" could not be found!",args[0]);
		} catch (IOException e) {
			System.out.println("Failed to read characters from given file!");
		}
	}
	Tokeniser tr;
	public Parser(String fileName) throws FileNotFoundException {
		tr=new Tokeniser(fileName);
		init();
	}
	public Parser(File file) throws FileNotFoundException {
		tr=new Tokeniser(file);
		init();
	}
	public Parser(FileDescriptor fd) {
		tr=new Tokeniser(fd);
		init();
	}
	public Parser(String fileName, Charset charset) throws IOException {
		tr=new Tokeniser(fileName,charset);
		init();
	}
	public Parser(File file, Charset charset) throws IOException {
		tr=new Tokeniser(file, charset);
		init();
	}
	/**
	 * A datastructure shaped like this JSON:
	 * 
	 * {
	 * 	"name":[
	 * 		["first","possible"],
	 * 		["second"],
	 * 	],
	 *  "another":[
	 * 		["thing"]
	 * 	],
	 * }
	 */
	//private HashMap<String,ArrayList<ArrayList<String>>> parseLogic;
	/**
	 * An easy to modify parseLogic. follows this JSON structure:
	 * 
	 * [
	 * 	["name"],
	 * 	["","first","possible"],
	 * 	["","second"],
	 * 	["another"],
	 * 	["","thing"]
	 * ]
	 */
	//private final String[][] tempParseLogic={
	private final String[][] parseLogic={
		{"ROOT"},//Think of ROOT as a "STATEMENT_LIST"
		{"",	"STATEMENT"},
		{"",	"ROOT","semicolon","ROOT"},//This is, in my opinion, an easier way to do a list
		{"WS_OR_COMMENT"},
		{"",	"whitespace"},
		{"",	"comment"},
		{"",	"multiComment"},
		{"",	"WS_OR_COMMENT","WS_OR_COMMENT"},
		{"STATEMENT"},
		{"",	"WS_OR_COMMENT","STATEMENT"},//before every statement, or collection of statements, allow WS or COMMENT
		{"",	"identifier","equals","EXPRESSION"},
		//{"",	"identifier","equals","STATEMENT"},//Two or more vars can share a value
		{"EXPRESSION"},
		{"",	"string"},
		{"",	"char"},
		{"",	"char"},
		{"",	"number"},
		{"",	"FUNCTION"},
		{"",	"EXPRESSION","subitem","identifier"},
		{"",	"EXPRESSION","FUNCTION_CALL"},
		{"",	"identifier","FUNCTION_CALL"},
		{"VALUE_LIST"},
		{"",	"EXPRESSION"},
		{"",	"VALUE_LIST","comma","VALUE_LIST"},
		{"",	"VALUE_LIST","comma","IDENTIFIER_LIST"},//If there are identifiers mixed in, grab them too.
		{"",	"IDENTIFIER_LIST","comma","VALUE_LIST"},
		{"FUNCTION"},
		{"",	"INLINE_FUNCTION"},
		{"",	"MULTILINE_FUNCTION"},
		{"FUNCTION_CALL"},
		{"",	"openP","VALUE_LIST",     "closeP"},
		{"",	"openP","IDENTIFIER_LIST","closeP"},//A list of identifiers could still be a list of values. (conflict with MULTILINE FUNCTION[1])
		{"INLINE_FUNCTION"},
		{"",	"openP","closeP",                  "lambda","STATEMENT"},
		{"",	"identifier",                      "lambda","STATEMENT"},
		{"",	"openP","IDENTIFIER_LIST","closeP","lambda","STATEMENT"},
		{"MULTILINE_FUNCTION"},
		{"",	"openC","ROOT","closeC"},
		{"",	"identifier","MULTILINE_FUNCTION_BODY"},
		{"",	"openP","IDENTIFIER_LIST","closeP","MULTILINE_FUNCTION_BODY"},
		{"MULTILINE_FUNCTION_BODY"},
		{"",	"openC","ROOT",            "closeC"},
		{"",	"openC","ROOT","semicolon","closeC"},//Optional semicolon
		{"IDENTIFIER_LIST"},//A list of _only_ identifiers (>=1)
		{"",	"identifier"},//Will require LA(1)
		{"",	"IDENTIFIER_LIST","comma","identifier"},
		{"",	"IDENTIFIER_LIST","comma","IDENTIFIER_LIST"},
	};
	/**
	 * The one place to do 90% of constructor related stuff
	 */
	private void init() {
		stack=new ArrayList<>();
		/*parseLogic=new HashMap<>();//TODO: It's probably actually easier to just use exactly what I have
		var currentName=new String();
		ArrayList<ArrayList<String>> value;
		for (int i=0;i<tempParseLogic.length;++i) {
			String[] row=tempParseLogic[i];
			if (row.length==1) {
				if (currentName!="") {
					//TODO: Write data to table
				}
				currentName=row[0];
				value=new ArrayList<>();
			}else if(row[0]=="") {
				var temp=new ArrayList<String>();
				//TODO: Add this row, excluding the first item, to `value`
			}
		}*/
	}
	/**
	 * Get the next token.
	 * 
	 * Intentionally @Override-able
	 * @return The next token.
	 * @throws IOException
	 */
	public ChamlcToken getNextToken() throws IOException {
		return tr.read();
	}
	private ArrayList<ParseNode> stack;
	/**
	 * Return true when a reduction is made, false if nothing changed
	 */
	public boolean reduce(){
		//number largest
		//Iterate over items in parseLogic
			//If the length is greater than 1
				//Iterate over items in this item in reverse
					//match count=0
					//if this item.name matches the same position in the stack
						//increase match count by 1
					//else if match count is greater than zero and equal to item.len-1
						//if match count is greater than parseLogic[largest].size()-1
							//largest = index of parselogic
		//if largest 
		//get name of reduction
		//make tree node of name
		//add all matched nodes (parseLogic[largest].size()-2) to tree
		//remove matched nodes from stack
		//insert tree into stack
		return true;//TODO: Dummy function
	}
	/**
	 * Move a token over
	 * @throws IOException
	 */
	private void shift() throws IOException {
		stack.add(new ParseLeaf(getNextToken()));
	}
	/**
	 * Actually run the parser, completely
	 * @return
	 * @throws IOException
	 */
	public ParseTreeRoot parse() throws IOException {
		ParseTreeRoot root=new ParseTreeRoot("");
		shift();
		shift();
		root.add(stack.get(0));
		root.add(stack.get(1));
		root.printAsXML();
		return root;
	}
}