import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import TokeniserTools.ChamlcToken;
import TokeniserTools.ChamlcTokenError;
import ParseTools.ParseNode;
import ParseTools.ParseTree;
import ParseTools.ParseTreeRoot;
import ParseTools.ParseLeaf;

class Parser {
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Not enough args!");
			return;
		}
		try {
			Parser par = new Parser(args[0]);
			var out=new FileWriter(args[1]);
			// Outputs xml
			var t=new ArrayList<ParseNode>();
			try {
				t = par.parse();// .get(0).printAsXML();
				par.close();
			} catch (IOException e){
				//if (par.isEnd())
				//	System.out.println("Failed to close file after hitting EOF!");
				//else
				System.out.println("Failed to read characters from file!");
				e.printStackTrace();
			} catch (ChamlcTokenError e) {//Token Syntax error, or the like
				System.out.println(e.getMessage());
			}finally{
				// Much of this new xml stuff is greatly helped by
				// https://www.tutorialspoint.com/java_xml/java_dom_create_document.htm
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.newDocument();
				
				Element rootElement = t.get(0).getAsXML(doc);
				Attr attr = doc.createAttribute("src");
				attr.setValue(args[0]);
				rootElement.setAttributeNode(attr);

				doc.appendChild(rootElement);

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(out);
				transformer.transform(source, result);
			}
		} catch (FileNotFoundException e) {
			System.out.printf("There is no file by the name %s\n", args[0]);
		} catch (IOException e) {
			System.out.println("Failed to write character to file!");
			e.printStackTrace();
		} catch (ParserConfigurationException | TransformerException e) {
			System.out.println("Unknown error in parser!");
			e.printStackTrace();
		}
	}

	Tokeniser tr;
	/**
	 * Close the input stream.
	 * @throws IOException If an I/O error occurs
	 */
	private void close() throws IOException {
		tr.close();
	}

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

	/**
	 * The one place to do 90% of constructor related stuff
	 */
	private void init() {
		buffer = new ArrayList<>();
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
		var items=new ArrayList<ParseNode>();
		boolean changed=false;
		for (int i=0;i<buffer.size();++i){
			if (matches(i,"whitespace")||
				matches(i,"comment")||
				matches(i,"multiComment")) {
				buffer.remove(i);
				changed=true;
			}
			if (matches(i,"EXPRESSION,subitem,identifier")||
				matches(i,"identifier,subitem,identifier")||
				matches(i,"SUB_ITEM,subitem,identifier")) {
				items.add(buffer.remove(i));
				buffer.remove(i);
				items.add(buffer.remove(i));
				buffer.add(i,new ParseTree("SUB_ITEM",items));
				changed=true;
				items.clear();
			}
			if (matches(i,"EXPRESSION,equals,EXPRESSION,statementSeparator")||
				matches(i,"EXPRESSION,equals,identifier,statementSeparator")||
				matches(i,"identifier,equals,EXPRESSION,statementSeparator")||
				matches(i,"identifier,equals,identifier,statementSeparator")) {
				items.add(buffer.remove(i));
				buffer.remove(i);
				items.add(buffer.remove(i));
				buffer.add(i,new ParseTree("SET_VARIABLE",items));
				changed=true;
				items.clear();
			}
			if (matches(i,"EXPRESSION,overload,EXPRESSION,statementSeparator")||
				matches(i,"EXPRESSION,overload,identifier,statementSeparator")||
				matches(i,"identifier,overload,EXPRESSION,statementSeparator")||
				matches(i,"identifier,overload,identifier,statementSeparator")) {
				items.add(buffer.remove(i));
				buffer.remove(i);
				items.add(buffer.remove(i));//Leave the statement separator be
				buffer.add(i,new ParseTree("OVERLOAD_VARIABLE",items));
				changed=true;
				items.clear();
			}
			if (matches(i,"syntaxExtension")||
				matches(i,"SET_VARIABLE")||
				matches(i,"OVERLOAD_VARIABLE")||
				matches(i,"RETURNVAL")||
				matches(i,"EXPRESSION,statementSeparator")){//Keep the separator
				items.add(buffer.remove(i));
				buffer.add(i,new ParseTree("STATEMENT",items));
				changed=true;
				items.clear();
			}
			if (matches(i,"STATEMENT,statementSeparator,STATEMENT")) {
				items.add(buffer.remove(i));
				buffer.remove(i);
				items.add(buffer.remove(i));
				buffer.add(i,new ParseTreeRoot(items));
				changed=true;
				items.clear();
			}
			if (matches(i,"ROOT,statementSeparator,STATEMENT")) {
				var tree=(ParseTreeRoot)buffer.remove(i);
				buffer.remove(i);
				tree.add(buffer.remove(i));
				buffer.add(i,tree);
				changed=true;
			}
			if (matches(i,"INLINE_FUNCTION")||
				matches(i,"MULTILINE_FUNCTION")) {
				items.add(buffer.remove(i));
				buffer.add(i,new ParseTree("FUNCTION",items));
				changed=true;
				items.clear();
			}
			if (matches(i,"statementSeparator,closeC")||
				matches(i,"statementSeparator,statementSeparator")) {
				buffer.remove(i);
				changed=true;
				items.clear();
			}
			if (matches(i,"EMPTY_PARENS,lambda,STATEMENT")||
				matches(i,"EMPTY_PARENS,lambda,identifier")) {
				buffer.remove(i);
				buffer.remove(i);
				items.add(buffer.remove(i));
				buffer.add(i,new ParseTree("INLINE_FUNCTION",items));
				changed=true;
				items.clear();
			}
			if (matches(i,"ARGUMENT_NAMES,openC,ROOT,closeC")||
				matches(i,"ARGUMENT_NAMES,openC,STATEMENT,closeC")) {
				items.add(buffer.remove(i));
				buffer.remove(i);
				items.add(buffer.remove(i));
				buffer.remove(i);
				buffer.add(i,new ParseTree("MULTILINE_FUNCTION",items));
				changed=true;
				items.clear();
			}
			if (matches(i,"openC,closeC")) {
				var first=buffer.remove(i);
				var second=buffer.remove(i);
				buffer.add(i,new ParseTree("MULTILINE_FUNCTION",second.getRow(),second.getCol(),first.getStart_r(),first.getStart_c()));
				changed=true;
				items.clear();
			}
			if (matches(i,"openC,ROOT,closeC")||
				matches(i,"openC,STATEMENT,closeC")) {
				buffer.remove(i);
				items.add(buffer.remove(i));
				buffer.remove(i);
				buffer.add(i,new ParseTree("MULTILINE_FUNCTION",items));
				changed=true;
				items.clear();
			}
			if (matches(i,"VALUE_LIST,comma,VALUE_LIST")||
				matches(i,"VALUE_LIST,comma,IDENTIFIER_LIST")||// If there are identifiers mixed in, grab them too.
				matches(i,"IDENTIFIER_LIST,comma,VALUE_LIST")||
				matches(i,"VALUE_LIST,comma,EXPRESSION")||//Most of the time, they are just expressions
				matches(i,"EXPRESSION,comma,EXPRESSION")||
				matches(i,"EXPRESSION,comma,identifier")){
				items.add(buffer.remove(i));
				buffer.remove(i);
				items.add(buffer.remove(i));
				buffer.add(i,new ParseTree("VALUE_LIST",items));
				changed=true;
				items.clear();
			}
			if (matches(i,"identifier,comma,identifier")||
				matches(i,"IDENTIFIER_LIST,comma,identifier")){
				items.add(buffer.remove(i));
				buffer.remove(i);
				items.add(buffer.remove(i));
				buffer.add(i,new ParseTree("IDENTIFIER_LIST",items));
				changed=true;
				items.clear();
			}
			if (matches(i,"openP,identifier,closeP")||
				matches(i,"openP,IDENTIFIER_LIST,closeP")){
				buffer.remove(i);
				items.add(buffer.remove(i));
				buffer.remove(i);
				buffer.add(i,new ParseTree("ARGUMENT_NAMES",items));
				changed=true;
				items.clear();
			}
			if (matches(i,"openP,VALUE_LIST,closeP")||
				matches(i,"openP,EXPRESSION,closeP")) {
				buffer.remove(i);
				items.add(buffer.remove(i));
				buffer.remove(i);
				buffer.add(i,new ParseTree("ARGUMENT_VALUES",items));
				changed=true;
				items.clear();
			}
			if (matches(i,"openP,closeP")) {
				var first=buffer.remove(i);
				var second=buffer.remove(i);
				buffer.add(i,new ParseTree("EMPTY_PARENS",second.getRow(),second.getCol(),first.getStart_r(),first.getStart_c()));
			}
			if (matches(i,"EXPRESSION,ARGUMENT_NAMES")||
				matches(i,"identifier,ARGUMENT_NAMES")||
				matches(i,"EXPRESSION,ARGUMENT_VALUES")||
				matches(i,"identifier,ARGUMENT_VALUES")||
				matches(i,"EXPRESSION,EMPTY_PARENS")||
				matches(i,"identifier,EMPTY_PARENS")) {
				items.add(buffer.remove(i));
				items.add(buffer.remove(i));
				buffer.add(i,new ParseTree("CALL",items));
				changed=true;
				items.clear();
			}
			if (matches(i,"return,identifier")||
				matches(i,"return,EXPRESSION")/*||
				matches(i,"return,STATEMENT")*/) {
				buffer.remove(i);
				items.add(buffer.remove(i));
				buffer.add(i,new ParseTree("RETURNVAL",items));
				changed=true;
				items.clear();
			}
			if (matches(i,"import")||
				matches(i,"string")||
				matches(i,"char")||
				matches(i,"number")||
				matches(i,"FUNCTION")||
				matches(i,"ARRAY")||
				matches(i,"CALL")||
				matches(i,"SUB_ITEM")) {
				items.add(buffer.remove(i));
				buffer.add(i,new ParseTree("EXPRESSION",items));
				changed=true;
				items.clear();
			}
			if (matches(i,"openS,closeS")) {
				var first=buffer.remove(i);
				var second=buffer.remove(i);
				buffer.add(i,new ParseTree(
					"ARRAY",
					second.getRow(),
					second.getCol(),
					first.getStart_r(),
					second.getStart_c()));
				changed=true;
				items.clear();
			}
		}
		return changed;
	}

	private boolean matches(int offset,String str) {
		String[] thingsToCheck=str.split(",");
		for (int i=0;i<thingsToCheck.length;++i){
			if((i+offset)>=buffer.size()||
				!buffer.get(i+offset).getName().equals(
				thingsToCheck[i])){
				return false;
			}
		}
		return true;
	}

	/**
	 * Move a token over
	 * 
	 * @throws ChamlcTokenError
	 * @throws IOException
	 */
	private void shift() throws IOException, ChamlcTokenError {
		buffer.add(new ParseLeaf(getNextToken()));
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
		boolean reduced;
		while(isNextTokenReady()&&!hitError) {
			do{
				shift();
				do{
					reduced=reduce();
				}while(reduced);
			}while(isNextTokenReady()&&!hitError);
		}
		if (!hitError&&(buffer.size()==2||buffer.size()==1)&&matches(0,"STATEMENT")) {
			buffer.add(0,new ParseTreeRoot("",buffer.remove(0)));
		}
		// if(!hitError&&buffer.size()==2&&matches(0,"ROOT,statementSeparator")) {
		// 	buffer.remove(1);
		// }
		return buffer;
	}
}