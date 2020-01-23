import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import TokeniserTools.ChamlcToken;
import TokeniserTools.ChamlcTokenError;
import java.util.regex.Pattern;
import java.io.FileWriter;
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

/**
 * Tokenize a file. Made for the Java CHAML Compiler (jchamlc)
 */
class Tokeniser {
	/**
	 * 
	 * @param args {@index 0} The name of the file to tokenize {@index 1} The output
	 *             file. (Will output in xml format)
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Not enough args!");
			return;
		}
		try {
			Tokeniser tr = new Tokeniser(args[0]);
			var out = new FileWriter(args[1]);
			// Outputs xml
			try {
				// Much of this new xml stuff is greatly helped by
				// https://www.tutorialspoint.com/java_xml/java_dom_create_document.htm
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.newDocument();

				Element rootElement = doc.createElement("tokenList");
				doc.appendChild(rootElement);
				Attr attr = doc.createAttribute("src");
				attr.setValue(args[0]);
				rootElement.setAttributeNode(attr);

				while (!tr.isEnd()) {
					ChamlcToken tok = tr.read();
					rootElement.appendChild(tok.getAsXML(doc));
				}

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(out);
				transformer.transform(source, result);
			} catch (IOException e) {
				if (tr.isEnd())
					System.out.println("Failed to close file after hitting EOF!");
				else
					System.out.println("Failed to read or write character to or from file!");
				e.printStackTrace();
			} catch (ChamlcTokenError | ParserConfigurationException | TransformerException e) {
				System.out.println(e.getMessage());
			} finally {
				out.close();
			}
		} catch (FileNotFoundException e) {
			System.out.printf("There is no file by the name %s\n", args[0]);
		} catch (IOException e) {
			System.out.println("Failed to write character to file!");
			e.printStackTrace();
		}
	}

	/**
	 * The fileReader that this uses internally.
	 */
	private FileReader fr;
	/**
	 * Characters that the program needs to get to
	 */
	private StringBuffer backlog;
	/**
	 * Make a {@link Tokeniser} off of a given file location.
	 * @param fileName The name of the file
	 * @throws FileNotFoundException If {@link FileReader} can't find the file
	 */
	public Tokeniser(String fileName) throws FileNotFoundException {
		fr = new FileReader(fileName);
		init();
	}

	public Tokeniser(File file) throws FileNotFoundException {
		fr = new FileReader(file);
		init();
	}

	public Tokeniser(FileDescriptor fd) {
		fr = new FileReader(fd);
		init();
	}

	public Tokeniser(String fileName, Charset charset) throws IOException {
		fr = new FileReader(fileName, charset);
		init();
	}

	public Tokeniser(File file, Charset charset) throws IOException {
		fr = new FileReader(file, charset);
		init();
	}

	/**
	 * One place to set all non-unique variables.
	 */
	private void init() {
		backlog = new StringBuffer(0);
	}

	boolean needsMoreChars = false;

	/**
	 * Convert the current stack to a token, using no regexp.
	 * 
	 * @throws ChamlcTokenError If the input from the file was malformed (EX: a
	 * syntax error)
	 */
	private ChamlcToken stackToTok() throws ChamlcTokenError {
		final int row=this.row,col=this.col;
		String bl=backlog.toString();
		if(bl.length()==0||Pattern.matches("\\s+(.|\\n)",bl)) {
			if(bl.length()>1&&Pattern.matches("a\\s","a"+bl.charAt(bl.length()-1))) {
				needsMoreChars=true;
				throw new ChamlcTokenError("Whitespace needs more chars!",row,col,start_r,start_c);
			}
			return new ChamlcToken("whitespace","",row,col,start_r,start_c);
		}else if(Pattern.matches(";(.|\\n)",bl)) {
			return new ChamlcToken("statementSeparator","",row,col,start_r,start_c);
		}else if(Pattern.matches("=[^<>]",bl)) {
			return new ChamlcToken("equals","",row,col,start_r,start_c);
		}else if(Pattern.matches("~(.|\\n)",bl)) {
			return new ChamlcToken("overload","",row,col,start_r,start_c);
		}else if(Pattern.matches("\\[(.|\\n)",bl)) {
			return new ChamlcToken("openS","",row,col,start_r,start_c);
		}else if(Pattern.matches("\\](.|\\n)",bl)) {
			return new ChamlcToken("closeS","",row,col,start_r,start_c);
		}else if(Pattern.matches("\\((.|\\n)",bl)) {
			return new ChamlcToken("openP","",row,col,start_r,start_c);
		}else if(Pattern.matches("\\)(.|\\n)",bl)) {
			return new ChamlcToken("closeP","",row,col,start_r,start_c);
		}else if(Pattern.matches("\\{(.|\\n)",bl)) {
			return new ChamlcToken("openC","",row,col,start_r,start_c);
		}else if(Pattern.matches("\\}(.|\\n)",bl)) {
			return new ChamlcToken("closeC","",row,col,start_r,start_c);
		}else if(Pattern.matches("=>(.|\\n)",bl)) {
			return new ChamlcToken("lambda","",row,col,start_r,start_c);
		}else if(Pattern.matches("=<(.|\\n)",bl)) {
			return new ChamlcToken("return","",row,col,start_r,start_c);
		}else if(Pattern.matches("\\.(.|\\n)",bl)) {
			return new ChamlcToken("subitem","",row,col,start_r,start_c);
		}else if(Pattern.matches(",(.|\\n)",bl)) {
			return new ChamlcToken("comma","",row,col,start_r,start_c);
		}else if(Pattern.matches("//.*\\n(.|\\n)",bl)) {
			return new ChamlcToken("comment",bl.substring(2,bl.length()-2),row,col,start_r,start_c);
		}else if(Pattern.matches("/\\*(.|\\n)*\\*/(.|\\n)",bl)) {
			return new ChamlcToken("multiComment",bl.substring(2,bl.length()-3),row,col,start_r,start_c);
		}else if(Pattern.matches("/\\*(.|\\n)*",bl)) {
			throw new ChamlcTokenError("Multi-line comments need a closing '*/'!", row, col, start_r, start_c);
		}else if(Pattern.matches("#",bl)) {
			needsMoreChars=true;
			throw new ChamlcTokenError("Unfinished '#' symbol!",row,col,start_r,start_c);
		}else if(Pattern.matches("\"[^\"]*",bl)) {
			needsMoreChars=true;
			throw new ChamlcTokenError("Strings need a closing `\"` character!",row,col,start_r,start_c);
		}else if(Pattern.matches("\".*\"(.|\\n)",bl)) {
			if (bl.length()>3&&bl.charAt(bl.length()-3)=='\\') {
				needsMoreChars=true;
				throw new ChamlcTokenError("Escaped `\"` character found in string! More chars expected!",row,col,start_r,start_c);
			}
			/*var tempBuffer=new StringBuffer();
			String contents=bl.substring(1,bl.length()-2);
			for(int i=0;i<contents.length();++i) {
				if (contents.charAt(i)=='\\') {
					++i;
					switch(contents.charAt(i)) {//TODO: move to separate function for chars?
						case 'b':tempBuffer.append('\b');break;
						case 't':tempBuffer.append('\t');break;
						case 'n':tempBuffer.append('\n');break;
						case 'f':tempBuffer.append('\f');break;
						case 'r':tempBuffer.append('\r');break;
						case '"':tempBuffer.append('"');break;
						case '\'':tempBuffer.append('\'');break;
						case '\\':tempBuffer.append('\\');break;
					}
				}else tempBuffer.append(contents.charAt(i));
			}
			return new ChamlcToken("string",tempBuffer.toString(),row,col,start_r,start_c);*/
			return new ChamlcToken("string",bl.substring(1,bl.length()-2),row,col,start_r,start_c);
		}else if(Pattern.matches("#(\\+|<)(\\[[^]]*)?",bl)) {
			needsMoreChars=true;
			throw new ChamlcTokenError("Syntax Extensions and File Imports need information about what to import contained within a '[' and a ']' symbol!",row,col,start_r,start_c);
		}else if(Pattern.matches("#\\+\\[.*](.|\\n)",bl)) {
			return new ChamlcToken("syntaxExtension",bl.substring(3,bl.length()-2),row,col,start_r,start_c);
		}else if(Pattern.matches("#\\<\\[.*](.|\\n)",bl)) {
			return new ChamlcToken("import",bl.substring(3,bl.length()-2),row,col,start_r,start_c);
		}else if(Pattern.matches("[a-zA-Z_$][a-zA-Z0-9_$]*(.|\\n)",bl)) {
			if(Pattern.matches("a[a-zA-Z0-9_$]","a"+bl.charAt(bl.length()-1))) {
				needsMoreChars=true;
				throw new ChamlcTokenError("Identifier `"+bl.substring(0,bl.length()-1)+"` needs more chars!",row,col,start_r,start_c);
			}
			return new ChamlcToken("identifier",bl.substring(0,bl.length()-1),row,col,start_r,start_c);
		}else if(Pattern.matches("-?[0-9_]+(\\.[0-9_]*)?(.|\\n)",bl)) {
			return new ChamlcToken("number",bl,row,col,start_r,start_c);
		}else if (isCharEnd(bl.charAt(bl.length()-1))) {
			endOfFile=true;
			if (bl.length()>2) {
				hitEarlyEOF=true;
				throw prematureEOF();
			}else return new ChamlcToken("whitespace","",row,col,start_r,start_c);
		}else throw new ChamlcTokenError("Unknown character sequence!\nBacklog:'"+bl+"'",row,col,start_r,start_c);
	}
	private Boolean isCharEnd(char c) {
		return c==(char) 3||//EOF
			c==(char) 0xFFFF;//I don't know what this is...
	}
	private Boolean hitEarlyEOF=false;
	private ChamlcTokenError prematureEOF() {
		return new ChamlcTokenError("Received an EOF too early! Was expecting the rest of a token!\n\nHere's the code (all "+(backlog.length()-1)+" characters!):\n\n```chaml\n"+backlog.toString().substring(0,backlog.length()-1)+"\n```\n",row,col,start_r,start_c);
	}
	//private ChamlcTokenError scopeTooWide() {
	//	return new ChamlcTokenError("Too many characters were included in this scope",row,col,start_r,start_c);
	//}
	private Boolean endOfFile=false;
	public Boolean isEnd() {
		return endOfFile||hitEarlyEOF;
	}
	/**
	 * Clear all but the latest char of the stack
	 */
	//private void eat() {
	//	char temp=backlog.charAt(backlog.length()-1);
	//	backlog.delete(0,backlog.length());
	//	backlog.append(temp);
	//}
	private int row=1,col=1,start_c=1,start_r=1;
	/**
	 * Add another character to the "stack"
	 * @throws IOException If an I/O error occurs
	 */
	private void addAnotherToStack() throws IOException {
		char c=(char)fr.read();
		if (backlog.length()>0&&backlog.charAt(backlog.length()-1)=='\n') {
			col=1;
			row++;
		}else col++;
		backlog.append(c);
	}
	private boolean firstRun=true;
	
	/**
	 * Get the next token.
	 * 
	 * Continuously pushes chars to a "stack" as long as it still is valid. Once it
	 * isn't valid anymore, it then returns the token matched before adding the
	 * invalidator, then clears the stack, instantly appending the invalidator so it
	 * can be used later.
	 * 
	 * TL;DR: It has a lookahead of 1, and doesn't use any regexp.
	 * 
	 * The row and col values will always be close to the first character of the
	 * token.
	 * 
	 * @throws IOException If an I/O error occurs
	 * @throws ChamlcTokenError If the input from the file was malformed (EX: a
	 * syntax error)
	 */
	public ChamlcToken read() throws IOException, ChamlcTokenError {
		this.start_r=row;
		this.start_c=col;
		if (firstRun) {
			addAnotherToStack();
			firstRun=false;
		}
		ChamlcTokenError err=null;
		ChamlcToken tok=null;
		try {
			tok = stackToTok();
		} catch (ChamlcTokenError e) {
			err=e;
		}
		boolean keepLooking=true;
		needsMoreChars=false;
		while (keepLooking) {
			addAnotherToStack();
			ChamlcToken temp=null;
			try {
				temp = stackToTok();
			} catch (ChamlcTokenError e) {
				err=e;
			}
			keepLooking=(needsMoreChars||temp==null)&&!isEnd();
			if (keepLooking) {
				needsMoreChars=false;
			}
			if(temp!=null) {
				tok=temp;
				err=null;
			}
		}
		if (err!=null) throw err;
		//if (hitEarlyEOF) throw prematureEOF();
		//eat();
		backlog.delete(0,backlog.length()-1);
		return tok;
	}
	/**
	 * Close the input stream.
	 * @throws IOException If an I/O error occurs
	 */
	public void close() throws IOException {
		fr.close();
	}
}
