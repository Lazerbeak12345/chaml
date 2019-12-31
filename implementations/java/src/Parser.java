import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import TokeniserTools.ChamlcToken;
import TokeniserTools.ChamlcTokenError;
import ParseTools.ParseNode;
import ParseTools.ParseTree;
import ParseTools.ParseTreeRoot;
import ParseTools.ParseLeaf;

class Parser {
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Not enough args!");
			return;
		}
		try {
			Parser par = new Parser(args[0]);
			var t = par.parse();// .get(0).printAsXML();
			System.out.printf("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<parseNodes src='%s'>\n", args[0]);
			/*
			 * if(t.size()!=1) {
			 * System.out.printf("<parseError leftoverCount=\"%d\">",t.size()); for(int
			 * i=0;i<t.size();++i){ t.get(i).printAsXML(); System.out.println(); }
			 * System.out.print("</parseError>"); }else
			 */
			new ParseTreeRoot(args[0], t).printAsXML();
			// t.get(t.size()-1).printAsXML();
			System.out.println("</parseNodes>");
		} catch (FileNotFoundException e) {
			System.out.printf("A file of the name \"%s\" could not be found!", args[0]);
		} catch (IOException e) {
			System.out.println("Failed to read characters from given file!");
		} catch (ChamlcTokenError e) {
			System.out.println(e.getMessage());
		}
	}

	Tokeniser tr;

	public Parser(String fileName) throws FileNotFoundException {
		tr = new Tokeniser(fileName);
		init();
	}

	public Parser(File file) throws FileNotFoundException {
		tr = new Tokeniser(file);
		init();
	}

	public Parser(FileDescriptor fd) {
		tr = new Tokeniser(fd);
		init();
	}

	public Parser(String fileName, Charset charset) throws IOException {
		tr = new Tokeniser(fileName, charset);
		init();
	}

	public Parser(File file, Charset charset) throws IOException {
		tr = new Tokeniser(file, charset);
		init();
	}
	private ArrayList<ArrayList<Number>> parseTransforms;
	private ArrayList<Number> parseNames;
	/**
	 * The one place to do 90% of constructor related stuff
	 */
	private void init() {
		buffer = new ArrayList<>();
		/**
		 * An easy to modify parseLogic. follows this JSON structure:
		 * 
		 * [
		 *   ["name"],
		 *   ["",  "first","possible"],
		 *   ["",  "second"],
		 *   ["another"],
		 *   ["",  "thing"]
		 * ]
		 */
		final String[][] parseLogic = {
			{"WS_OR_COMMENT"},
			{"",	"whitespace"},
			{"",	"comment"},
			{"",	"multiComment"},
			{"",	"WS_OR_COMMENT","WS_OR_COMMENT"},
			{"IDENTIFIER_LIST"},// A list of _only_ identifiers (>=1)
			{"",	"identifier"}, // Will require LA(1)
			{"",	"IDENTIFIER_LIST","comma","identifier"},
			{"","IDENTIFIER_LIST","comma","IDENTIFIER_LIST"},
			{"STATEMENT"},//Like C, do this to get the reference when it hasn't been defined.
			{"INLINE_FUNCTION"},
			{"",	"openP","closeP","lambda","STATEMENT"},
			{"",	"identifier","lambda","STATEMENT" },
			{"",	"openP","IDENTIFIER_LIST","closeP","lambda","STATEMENT"},
			{"ROOT"},//Root _must_ be defined, or else it is a parse error
			{"MULTILINE_FUNCTION_BODY"},
			{"",	"openC","ROOT","closeC"},
			{"",	"openC","ROOT","statementSeparator","closeC"},// Optional semicolon
			{"MULTILINE_FUNCTION"},
			{"",	"openC","ROOT","closeC"},
			{"",	"identifier","MULTILINE_FUNCTION_BODY"},
			{"",	"openP","IDENTIFIER_LIST","closeP","MULTILINE_FUNCTION_BODY"},
			{"FUNCTION"},
			{"",	"INLINE_FUNCTION"},
			{"",	"MULTILINE_FUNCTION"},
			{"EXPRESSION"},
			{"VALUE_LIST"},
			{"",	"EXPRESSION"},
			{"",	"VALUE_LIST","comma","VALUE_LIST"},
			{"",	"VALUE_LIST","comma","IDENTIFIER_LIST"},// If there are identifiers mixed in, grab them too.
			{"",	"IDENTIFIER_LIST","comma","VALUE_LIST"},
			{"FUNCTION_CALL"},
			{"",	"openP","VALUE_LIST","closeP"},
			{"",	"openP","IDENTIFIER_LIST","closeP"}, // A list of identifiers could still be a list of values. (conflict with MULTILINE FUNCTION[1])
			{"EXPRESSION"},
			{"",	"string"},
			{"",	"char"},
			{"",	"char"},
			{"",	"number"},
			{"",	"FUNCTION"},
			{"",	"EXPRESSION","subitem","identifier"},
			{"",	"EXPRESSION","FUNCTION_CALL"},
			{"",	"identifier","FUNCTION_CALL"},
			{"STATEMENT"},
			{"",	"import"},
			{"",	"syntaxExtension"},
			{"",	"WS_OR_COMMENT","STATEMENT"},// before every statement, or collection of statements, allow WS or COMMENT
			{"",	"identifier","equals","EXPRESSION" },
			//{"", "identifier","equals","STATEMENT"},//Two or more vars can share a value
			{"ROOT"},// Think of ROOT as a "STATEMENT_LIST"
			{"",	"STATEMENT"},
			{"",	"ROOT","statementSeparator","STATEMENT" },
		};
		parseTransforms=new ArrayList<>();
		parseNames=new ArrayList<>();
		/** the name of this reduction */
		int reductionName = -1;
		ParseNode.nodes=new ArrayList<>();
		ParseNode.nodes.add("_leaf_");
		//iterate through each row
		for (int i = 0; i < parseLogic.length; ++i) {
			/** The current row */
			String[] row = parseLogic[i];
			if (row.length < 1)//If it's empty, skip it
				continue;
			else if (row.length == 1){//if it is just one item, it's a name
				//transform that name to a parse node number
				reductionName = ParseNode.nameToInt(row[0]);
				if(reductionName<0) {
					ParseNode.nodes.add(row[0]);//Add this as a possible node type
					reductionName = ParseNode.nameToInt(row[0]);
				}
			}else {//otherwise, it's a transform to the last name
				/** The new row */
				var newR = new ArrayList<Number>();
				//iterate on each item in the current row, ignoring the first item (an empty string)
				for (int j = 1; j < row.length; ++j) {
					/* transform the name at the given index in this row, `j` 
					into an int, and insert it into `newR` at that position
					minus 1, to offset the empty string beginning each line */
					//newR.set(j-1,ParseNode.nameToInt(row[j]));
					int num=ParseNode.nameToInt(row[j]);
					if (num<0) throw new Error("Error encountered while converting parseLogic!\nRow num: "+i+" Col num: "+j+"\nSection:"+ParseNode.intToName(reductionName)+" Node:"+row[j]);
					newR.add(num);
				}
				/*As we are always adding to both at the same time, they are 
				always going to be the same length*/
				parseTransforms.add(newR);
				parseNames.add(reductionName);
			}
		}
	}

	/**
	 * Get the next token.
	 * 
	 * Intentionally @Override-able
	 * 
	 * @return The next token.
	 * @throws ChamlcTokenError If the input from the file was malformed (EX: a
	 * syntax error)
	 * @throws IOException If an I/O error occurs
	 */
	public ChamlcToken getNextToken() throws IOException, ChamlcTokenError {
		return tr.read();
	}
	public boolean isNextTokenReady() {
		return !tr.isEnd();
	}
	private ArrayList<ParseNode> buffer;
	/**
	 * @return true when a reduction is made, false if nothing changed
	 */
	public boolean reduce(){
		int largest=-1;
		for(int bufferI=0;bufferI<buffer.size();++bufferI){
			for(int parseTransformsI=0;parseTransformsI<parseTransforms.size();++parseTransformsI){
				var transform=parseTransforms.get(parseTransformsI);
				boolean doesTransformMatch=true;
				for(int transformI=0;transformI<transform.size();++transformI) {
					if (buffer.get(bufferI+transformI).getNumber()!=
					transform.get(transformI).intValue()){
						doesTransformMatch=false;
						break;
					}
				}
				if (!doesTransformMatch) break;
				if (largest==-1||transform.size()>parseTransforms.get(largest).size())
					largest=parseTransformsI;
			}
			if (largest==-1) continue;
			var transform=parseTransforms.get(largest);
			var temp=new ArrayList<ParseNode>();
			for(int transformI=0;transformI<transform.size();++transformI)
				temp.add(buffer.remove(bufferI+transformI));
			buffer.add(new ParseTree(ParseNode.intToName(parseNames.get(largest).intValue()),temp));
			return true;
		}
		return false;
	}
	
	/**
	 * Move a token over
	 * 
	 * @throws ChamlcTokenError
	 * @throws IOException
	 */
	private void shift() throws IOException, ChamlcTokenError {
		var c=getNextToken();
		var n=new ParseLeaf(c);
		hitError=n.getNumber()<0;
		buffer.add(n);
	}
	boolean hitError=false;
	
	/**
	 * Actually run the parser, completely
	 * 
	 * @return
	 * @throws ChamlcTokenError
	 * @throws IOException
	 */
	public ArrayList<ParseNode> parse() throws IOException, ChamlcTokenError {
		while(isNextTokenReady()&&!hitError) {
			do{
				shift();
			}while(!reduce()&&isNextTokenReady()&&!hitError);
		}
		return buffer;
	}
}