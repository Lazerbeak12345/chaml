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
	 * A datastructure shaped like this:
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
	 * The one place to do 90% of constructor related stuff
	 */
	private void init() {
		stack=new ArrayList<>();
		parseLogic=new TreeMap<>();
		//String[] names={"root","statementList"};
		/*final int NUMBER_OF_NAMES=3;
		for(int i=0;i<NUMBER_OF_NAMES;++i){
			ArrayList<String> tmp=new ArrayList<>();
			String name;
			switch(i) {
				case 0:
					name="root";
					tmp.add("statementList");
					break;
				case 1:
					name="statementList";
					tmp.add("statement");
					tmp.add("statementList");
					break;
				case 2:
					name="statement";
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