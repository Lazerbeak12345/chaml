import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import TokeniserTools.ChamlcToken;
import TokeniserTools.ChamlcTokenError;
import java.util.regex.*;
import java.io.FileWriter;

/**
 * Tokenize a file. Made for the Java CHAML Compiler (jchamlc)
 */
class Tokeniser {
	/**
	 * 
	 * @param args {@index 0} The name of the file to tokenize
	 * {@index 1} The output file. (Will output in xml format)
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Not enough args!");
			return;
		}
		try {
			Tokeniser tr = new Tokeniser(args[0]);
			// Outputs xml
			try {
				var out=new FileWriter(args[1]);
				//System.out.printf("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<tokenList src='%s'>\n", args[0]);
				while (!tr.isEnd()) {
					ChamlcToken tok = tr.read();
					out.append(tok.getAsXMLString());
				}
				//System.out.println("</tokenList>");
				tr.close();
				out.close();
			} catch (IOException e) {
				if (tr.isEnd())
					System.out.println("Failed to close file after hitting EOF!");
				else
					System.out.println("Failed to read or write character to or from file!");
			} catch (ChamlcTokenError e) {
				System.out.println(e.getMessage());
			}
		} catch (FileNotFoundException e) {
			System.out.printf("There is no file by the name %s\n", args[0]);
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
	 * @param start_c
	 * @param start_r
	 * @throws ChamlcTokenError
	 */
	private ChamlcToken stackToTok() throws ChamlcTokenError {
		final int row=this.row,col=this.col;
		if(Pattern.matches("#+",backlog.toString())) {
			return new ChamlcToken("comment",grabUntil(2,"\n"),row,col,start_r,start_c);
		}
		throw new ChamlcTokenError("Unknown character sequence!\nBacklog:'"+backlog.toString()+"'",row,col,start_r,start_c);
	}
	
	private String grabUntil(int offset,String s) throws ChamlcTokenError {
		var tempBuffer=new StringBuffer();
		int len=backlog.length();
		for (int i=offset;i<len;++i) {
			char posC=backlog.charAt(i);
			if (isCharEnd(posC)) throw prematureEOF();
			//if (posC==']'&&i+1<len) {
			//	throw scopeTooWide();
			//}
			boolean scopeTooWide=i+1>=len;
			for(int j=0;j<s.length()&&j<=i&&!scopeTooWide;++j) {
				scopeTooWide=backlog.charAt(j)==backlog.charAt(i);
			}
			if (scopeTooWide) throw scopeTooWide();
			if (i<len-1) tempBuffer.append(posC);
		}
		return tempBuffer.toString();
	}

	private Boolean isCharEnd(char c) {
		return c==(char) 3||//EOF
			c==(char) 0xFFFF;//I don't know what this is...
	}
	private Boolean hitEarlyEOF=false;
	private ChamlcTokenError prematureEOF() {
		return new ChamlcTokenError("Received an EOF too early! Was expecting the rest of a token!",row,col,start_r,start_c);
	}
	private ChamlcTokenError scopeTooWide() {
		return new ChamlcTokenError("Too many characters were included in this scope",row,col,start_r,start_c);
	}
	private Boolean endOfFile=false;
	public Boolean isEnd() {
		return endOfFile||hitEarlyEOF;
	}
	/**
	 * Clear all but the latest char of the stack
	 */
	private void eat() {
		char temp=backlog.charAt(backlog.length()-1);
		backlog.delete(0,backlog.length());
		backlog.append(temp);
	}
	private int row=1,col=1,start_c=1,start_r=1;
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
	 * @throws IOException
	 * @throws ChamlcTokenError
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
			keepLooking=needsMoreChars||!(err!=null||endOfFile);
			if (keepLooking) {
				tok=temp;
				needsMoreChars=false;
			}
			//System.out.print("<!--");temp.printAsXML();System.out.print("-->");
		}
		eat();
		if (hitEarlyEOF) throw prematureEOF();
		if (err!=null) throw err;
		return tok;
	}
	public void close() throws IOException {
		fr.close();
	}
}
