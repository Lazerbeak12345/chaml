import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

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
	private TreeMap<String,ArrayList<ArrayList<String>>> parseLogic;
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
	private final String[][] tempParseLogic={
		{"ROOT"},//Think of ROOT as a "STATEMENT_LIST"
		{"",	"STATEMENT"},
		{"",	"ROOT","semicolon","ROOT"},//This is, in my opinion, an easier way to do a list
		{"WS_OR_COMMENT"},
		{"",	"whitespace"},
		{"",	"comment"},
		{"",	"multiComment"},
		{"",	"WS_OR_COMMENT","WS_OR_COMMENT"},
		{"STATEMENT"},//TODO: make a "CALLABLE" (high priority)
		{"",	"WS_OR_COMMENT","STATEMENT"},//before every statement, or collection of statements, allow WS or COMMENT
		{"",	"identifier","equals","string"},
		{"",	"identifier","equals","char"},
		{"",	"identifier","equals","number"},
		{"",	"identifier","equals","FUNCTION"},
		{"",	"identifier","equals","STATEMENT"},//Two or more vars can share a value
		{"",	"identifier",                      "openP","VALUE_LIST",     "closeP"},
		{"",	"identifier",                      "openP","IDENTIFIER_LIST","closeP"},//A list of identifiers could still be a list of values.
		{"",	"STATEMENT",                       "openP","VALUE_LIST",     "closeP"},//You can call the return of a function
		{"",	"STATEMENT",                       "openP","IDENTIFIER_LIST","closeP"},
		{"",	"STATEMENT","subitem","identifier","openP","VALUE_LIST",     "closeP"},//You can call the subitem of a statement
		{"",	"STATEMENT","subitem","identifier","openP","IDENTIFIER_LIST","closeP"},
		{"VALUE_LIST"},
		{"",	"string"},
		{"",	"char"},
		{"",	"number"},
		{"",	"FUNCTION"},
		{"",	"VALUE_LIST","comma","VALUE_LIST"},
		{"",	"VALUE_LIST","comma","IDENTIFIER_LIST"},//If there are identifiers mixed in, grab them too.
		{"",	"IDENTIFIER_LIST","comma","VALUE_LIST"},
		{"FUNCTION"},
		{"",	"INLINE_FUNCTION"},
		{"",	"MULTILINE_FUNCTION"},
		{"INLINE_FUNCTION"},
		{"",	"openP","closeP",                  "lambda","STATEMENT"},
		{"",	"identifier",                      "lambda","STATEMENT"},
		{"",	"openP","IDENTIFIER_LIST","closeP","lambda","STATEMENT"},
		{"MULTILINE_FUNCTION"},
		{"",	"openC","ROOT","closeC"},
		{"",	"identifier","openC","ROOT",            "closeC"},
		{"",	"identifier","openC","ROOT","semicolon","closeC"},//Optional semicolon
		{"",	"openP","IDENTIFIER_LIST","closeP","openC","ROOT",            "closeC"},
		{"",	"openP","IDENTIFIER_LIST","closeP","openC","ROOT","semicolon","closeC"},
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
		parseLogic=new TreeMap<>();
		//String[] names={"ROOT","STATEMENT_LIST"};
		/*final int NUMBER_OF_NAMES=3;
		for(int i=0;i<NUMBER_OF_NAMES;++i){
			ArrayList<String> tmp=new ArrayList<>();
			String name;
			switch(i) {
				case 0:
					name="ROOT";
					tmp.add("STATEMENT_LIST");
					break;
				case 1:
					name="STATEMENT_LIST";
					tmp.add("STATEMENT");
					tmp.add("STATEMENT_LIST");
					break;
				case 2:
					name="STATEMENT";
					tmp.add("");
					break;
				default:
					name="ERROR IN HANDLING TREE LOGIC";
			}
			parseLogic.put(name,tmp);
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