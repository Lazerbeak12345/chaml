import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import TokeniserTools.ChamlcToken;
import TokeniserTools.ChamlcTokenError;
import ParseTools.ParseNode;
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
	private int[][] parseTransforms;
	private int[] parseNames;
	/**
	 * The one place to do 90% of constructor related stuff
	 */
	private void init() {
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
			{"ROOT"},// Think of ROOT as a "STATEMENT_LIST"
			{"",	"STATEMENT"},
			{"",	"ROOT","statementSeparator","STATEMENT" },
			{"WS_OR_COMMENT"},
			{"",	"whitespace"},
			{"",	"comment"},
			{"",	"multiComment"},
			{"",	"WS_OR_COMMENT","WS_OR_COMMENT"},
			{"STATEMENT"},
			{"",	"import"},
			{"",	"syntaxExtension"},
			{"",	"WS_OR_COMMENT","STATEMENT"},// before every statement, or collection of statements, allow WS or COMMENT
			{"",	"identifier","equals","EXPRESSION" },
			//{"", "identifier","equals","STATEMENT"},//Two or more vars can share a value
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
			{"",	"VALUE_LIST","comma","IDENTIFIER_LIST"},// If there are identifiers mixed in, grab them too.
			{"",	"IDENTIFIER_LIST","comma","VALUE_LIST"},
			{"FUNCTION"},
			{"",	"INLINE_FUNCTION"},
			{"",	"MULTILINE_FUNCTION"},
			{"FUNCTION_CALL"},
			{"",	"openP","VALUE_LIST","closeP"},
			{"",	"openP","IDENTIFIER_LIST","closeP"}, // A list of identifiers could still be a list of values. (conflict with MULTILINE FUNCTION[1])
			{"INLINE_FUNCTION"},
			{"",	"openP","closeP","lambda","STATEMENT"},
			{"",	"identifier","lambda","STATEMENT" },
			{"",	"openP","IDENTIFIER_LIST","closeP","lambda","STATEMENT"},
			{"MULTILINE_FUNCTION"},
			{"",	"openC","ROOT","closeC"},
			{"",	"identifier","MULTILINE_FUNCTION_BODY"},
			{"",	"openP","IDENTIFIER_LIST","closeP","MULTILINE_FUNCTION_BODY"},
			{"MULTILINE_FUNCTION_BODY"},
			{"",	"openC","ROOT","closeC"},
			{"",	"openC","ROOT","semicolon","closeC"},// Optional semicolon
			{"IDENTIFIER_LIST"},// A list of _only_ identifiers (>=1)
			{"",	"identifier"}, // Will require LA(1)
			{"",	"IDENTIFIER_LIST","comma","identifier"},
			{"","IDENTIFIER_LIST","comma","IDENTIFIER_LIST"},
		};
		stack = new ArrayList<>();
		/** the name of this reduction */
		int reductionName = -1,
		/** The number of rules found so far */
			ruleCount=0;
		//iterate through each row
		for (int i = 0; i < parseLogic.length; ++i) {
			/** The current row */
			String[] row = parseLogic[i];
			if (row.length < 1)//If it's empty, skip it
				continue;
			else if (row.length == 1)//if it is just one item, it's a name
				//transform that name to a parse node number
				reductionName = ParseNode.nameToInt(row[0]);
			else {//otherwise, it's a transform to the last name
				/** The new row */
				int[] newR = {};
				//iterate on each item in the current row, ignoring the first item (an empty string)
				for (int j = 1; j < row.length; ++j) {
					/* transform the name at the given index in this row, `j` 
					into an int, and insert it into `newR` at that position
					minus 1, to offset the empty string beginning each line */
					newR[j-1] = ParseNode.nameToInt(row[j]);
				}
				parseTransforms[ruleCount] = newR;
				parseNames[ruleCount] = reductionName;
				++ruleCount;
			}
		}
	}

	/**
	 * Get the next token.
	 * 
	 * Intentionally @Override-able
	 * 
	 * @return The next token.
	 * @throws ChamlcTokenError
	 * @throws IOException
	 */
	public ChamlcToken getNextToken() throws IOException, ChamlcTokenError {
		return tr.read();
	}
	public boolean isNextTokenReady() {
		return !tr.isEnd();
	}
	private ArrayList<ParseNode> stack;
	/**
	 * Return true when a reduction is made, false if nothing changed
	 */
	public boolean reduce(){
		return false;
		/*if (hitError) return true;
		int largest=-1;
		//*

		//iterate over positions in the "stack" (starting at 0)
			//iterate over transforms
				//iterate over items in transform
					//if this transform item does not match
						//go to next transform
				//if (largest==-1) or (the length of this transform is greater than the length of the longest thus far)
					//set the longest to be this one
			//if longest is -1, continue
			//iterate over items in transform in reverse
				//remove last item from stack, and add it to the front of an array list
			//make tree node of reduction name with stack item, adding to end of stack
			//exit

		/*String reductionName="ERROR";
		for(int i=0;i<parseLogic.length;++i) {
			String[] row=parseLogic[i];
			if(row.length<1)
				continue;
			else if(row.length==1)
				reductionName=row[0];
			else if(stack.size()>=row.length-1){
				for(int amountFromEnd=0;amountFromEnd<row.length;++amountFromEnd){
					int matchCount=0,
						rowPos=(row.length-1)-amountFromEnd,
						stackPos=(stack.size()-1)-amountFromEnd;
					if(stackPos<0) continue;
					if(row[
						rowPos
						].equals(stack.get(
							stackPos
							).getName()))
						matchCount++;
					else if(matchCount>0&&matchCount==row.length-1
						&&(largest==-1||matchCount>parseLogic[largest].length-1))
							largest=i;
				}
			}
		}
		if(largest!=-1) {
			//Make list of nodes to add to tree
			var nodes=new ArrayList<ParseNode>();
			int count=parseLogic[largest].length-1;//Don't count the first object
			for(int i=0;i<=count;++i){
				nodes.add(stack.remove(stack.size()-1));
			}
			stack.add(new ParseTree(reductionName,nodes));
			return true;
		}else return false;//*/
	}
	
	/**
	 * Move a token over
	 * 
	 * @throws ChamlcTokenError
	 * @throws IOException
	 */
	private void shift() throws IOException, ChamlcTokenError {
		var n=new ParseLeaf(getNextToken());
		hitError=n.getNumber()<0;
		stack.add(n);
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
		return stack;
	}
}