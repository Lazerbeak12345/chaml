import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileWriter;
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
				out.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<parseNodes src='"+args[0]+"'>\n");
				out.append(new ParseTreeRoot(args[0],t).getAsXML());
				out.append("</parseNodes>");
				out.close();
			}
		} catch (FileNotFoundException e) {
			System.out.printf("There is no file by the name %s\n", args[0]);
		} catch (IOException e) {
			System.out.println("Failed to write character to file!");
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
		for (int i=0;i<buffer.size();++i){
			/*if (matches("syntaxExtension")) {
				items.add(buffer.remove(i));
				buffer.add(i,new ParseTree("STATEMENT",items));
				return true;
			}*/
			if (matches(i,"comment")) {
				buffer.remove(i);
				buffer.add(
					i,
					new ParseTree("WS_OR_COMMENT",items));
				return true;
			}
		}
		return false;
	}

	private boolean matches(int offset,String str) {
		String[] thingsToCheck=str.split(",");
		for (
			int i=0;
			i<thingsToCheck.length && i+offset<buffer.size();
			++i
		) if (buffer.get(i+offset).getName()!=thingsToCheck[i]) return false;
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
		while(isNextTokenReady()&&!hitError) {
			do{
				shift();
			}while(!reduce()&&isNextTokenReady()&&!hitError);
		}
		return buffer;
	}
}