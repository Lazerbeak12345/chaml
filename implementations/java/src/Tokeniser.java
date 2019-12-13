import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;

import TokeniserTools.ChamlcToken;
/**
 * Tokenize a file. Made for the Java CHAML Compiler (jchamlc)
 */
class Tokeniser{
	public static void main(String[] args) {
		if (args.length<1) {
			System.out.println("Not enough args!");
			return;
		}
		try{
			Tokeniser tr=new Tokeniser(args[0]);
			//Outputs xml
			boolean hitErrorToken=false;
			try {
				System.out.printf("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<tokenList src='%s'>\n",args[0]);
				while(!hitErrorToken) {
					ChamlcToken tok = tr.read();
					if (!tr.isEnd()) tok.printAsXML();
					hitErrorToken=tok.isErrorCode();
				}
				System.out.println("</tokenList>");
				tr.close();
			} catch (IOException e) {
				if (hitErrorToken) System.out.println("Failed to close file after hitting Error token!");
				else if (tr.isEnd()) System.out.println("Failed to close file after hitting EOF!");
				else System.out.println("Failed to read character from file!");
			}
		}catch(FileNotFoundException e) {
			System.out.printf("There is no file by the name %s\n",args[0]);
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
	public Tokeniser(String fileName) throws FileNotFoundException {
		fr=new FileReader(fileName);
		init();
	}
	public Tokeniser(File file) throws FileNotFoundException {
		fr=new FileReader(file);
		init();
	}
	public Tokeniser(FileDescriptor fd) {
		fr=new FileReader(fd);
		init();
	}
	public Tokeniser(String fileName, Charset charset) throws IOException {
		fr=new FileReader(fileName,charset);
		init();
	}
	public Tokeniser(File file, Charset charset) throws IOException {
		fr=new FileReader(file, charset);
		init();
	}
	/**
	 * One place to set all non-unique variables.
	 */
	private void init() {
		backlog=new StringBuffer(0);
	}
	boolean needsMoreChars=false;
	/**
	 * Convert the current stack to a token, using no regexp.
	 */
	private ChamlcToken stackToTok() {
		final int row=this.row,col=this.col,len=backlog.length();
		if (len==0) return new ChamlcToken(-1, "No chars in stack!",row,col);
		final ChamlcToken scopeTooWide=new ChamlcToken(-1, "The scope for this token was too wide!",row,col);
		final char current=backlog.charAt(0);
		StringBuffer tempBuffer;
		switch(current) {
			case '#'://TODO: because the [] could be escaping anything, I need to make this into a loop-style
				if (len==1) {
					needsMoreChars=true;
					return new ChamlcToken(-1, "This would be a shabang or a pre-processor directive but it needs more chars",row,col);
				}
				if (backlog.charAt(1)=='!') {
					if (row!=1||col!=1) return new ChamlcToken(-1,"out of place shabang!",row,col);
				}else{
					if (len>3) return scopeTooWide;
					if (len==2) {
						needsMoreChars=true;
						return new ChamlcToken(-1, "pre-processor directive needs one more character", row, col);
					}
					switch(backlog.charAt(1)){
						case '<'://#<[
							return new ChamlcToken("import","",row,col);
						case '+'://#+[
							return new ChamlcToken("syntaxExtension","",row,col);
					}
					return new ChamlcToken(-1, "Stray # character!",row,col);
				}
			case '/':
				if (len==1) {
					needsMoreChars=true;
					return new ChamlcToken(-1, "This would be a comment, but it needs more indicators.",row,col);
				}
				switch(backlog.charAt(1)) {
					case '!':
						if (current!='#') return new ChamlcToken(-1, "Stray ! character!",row,col);
					case '/':
						tempBuffer=new StringBuffer();
						for (int i=2;i<len;++i) {
							char posC=backlog.charAt(i);
							if (isCharEnd(posC)) return prematureEOF();
							if (posC=='\n'&&i+1<len) {//if there is a character after the \n
								return scopeTooWide;
							}
							if (i<len-1)tempBuffer.append(posC);
						}
						return new ChamlcToken("comment",tempBuffer.toString(),row,col);
					case'*':
						tempBuffer=new StringBuffer();
						for (int i=2;i<len;++i) {
							char posC=backlog.charAt(i);
							if (isCharEnd(posC)) return prematureEOF();
							if (posC=='*'&&i+1<len&&
								backlog.charAt(i+1)=='/'&&i+2<len
								) {//if there is a character after the * and the /
								return scopeTooWide;
							}
							if (i<len-2) tempBuffer.append(posC);
						}
						return new ChamlcToken("multiComment",tempBuffer.toString(),row,col);
					default: 
						if (current=='/') return new ChamlcToken(-1, "Stray / character!",row,col);
						else return new ChamlcToken(-1, "Stray # character!",row,col);
				}
			case '"':
				tempBuffer=new StringBuffer();
				if (len>=2&&backlog.charAt(1)=='"') {
					if (len==2) return new ChamlcToken("string","",row,col);//empty string
					else return scopeTooWide;
				}
				if (len>1)tempBuffer.append(backlog.charAt(1));
				for (int i=2;i<len;++i) {
					char posC=backlog.charAt(i);
					if (isCharEnd(posC)) return prematureEOF();
					if (posC=='"'&&i+1<len&&
						backlog.charAt(i-1)!='\\') {//if there is a character after the " and it isn't escaped
						return scopeTooWide;
					}
					if (i<len-1)tempBuffer.append(posC);
				}
				return new ChamlcToken("string",tempBuffer.toString(),row,col);
			case '\'':
				ChamlcToken wouldBeChar=new ChamlcToken(-1, "This would be a char, but it's missing things.",row,col);
				if (len<=2) return wouldBeChar;
				if (backlog.charAt(2)=='\'') {
					return new ChamlcToken("char",Character.toString(backlog.charAt(1)),row,col);
				}
				return scopeTooWide;
			case '{':
				if (len==1) return new ChamlcToken("openC","",row,col);
				else return scopeTooWide;
			case '}':
				if (len==1) return new ChamlcToken("closeC","",row,col);
				else return scopeTooWide;
			case '(':
				if (len==1) return new ChamlcToken("openP","",row,col);
				else return scopeTooWide;
			case ')':
				if (len==1) return new ChamlcToken("closeP","",row,col);
				else return scopeTooWide;
			case '[':
				if (len==1) return new ChamlcToken("openS","",row,col);
				else return scopeTooWide;
			case ']':
				if (len==1) return new ChamlcToken("closeS","",row,col);
				else return scopeTooWide;
			case ' ':case '\t':case '\n':case '\r':
				for (int i=0;i<len;i++) {//no need to check for EOF here
					char posC=backlog.charAt(i);
					if (posC!=' '&&
						posC!='\t'&&
						posC!='\n'&&
						posC!='\r') return new ChamlcToken(-1,"Wasn't entirely whitespace!",row,col);
				}
				return new ChamlcToken("whitespace","",row,col);
			case '~':
				if (len==1) return new ChamlcToken("overload","",row,col);
				else return scopeTooWide;
			case '=':
				if (len>2) {
					return scopeTooWide;
				}
				if (len<2) {
					return new ChamlcToken("equals","",row,col);
				}
				switch(backlog.charAt(1)) {
					case '>':return new ChamlcToken("lambda","",row,col);
					case '<':return new ChamlcToken("return","",row,col);
					default:return new ChamlcToken(-1,"Stray '"+backlog.charAt(1)+"' character following '='!",row,col);
				}
			case ';':
				if (len==1) return new ChamlcToken("statementSeparator","",row,col);
				else return scopeTooWide;
			case ',':
				if (len==1) return new ChamlcToken("comma","",row,col);
				else return scopeTooWide;
			case '.':
				if (len==1) return new ChamlcToken("subitem","",row,col);
				else return scopeTooWide;
			case '0':case '1':case '2':case '3':case '4':case '5':case '6':
			case '7':case '8':case '9':
			case '-'://Also allow for negative numbers
				tempBuffer=new StringBuffer();
				for (int i=0;i<len;++i) {
					char posC=backlog.charAt(i);
					if (isCharEnd(posC)) return prematureEOF();
					if (!Character.isDigit(posC)&&
						!(i==0&&current=='-')) {
						return scopeTooWide;
					}
					tempBuffer.append(posC);
				}
				return new ChamlcToken("number",tempBuffer.toString(),row,col);
			case 'a':case 'b':case 'c':case 'd':case 'e':case 'f':case 'g':
			case 'h':case 'i':case 'j':case 'k':case 'l':case 'm':case 'n':
			case 'o':case 'p':case 'q':case 'r':case 's':case 't':case 'u':
			case 'v':case 'w':case 'x':case 'y':case 'z':
			case 'A':case 'B':case 'C':case 'D':case 'E':case 'F':case 'G':
			case 'H':case 'I':case 'J':case 'K':case 'L':case 'M':case 'N':
			case 'O':case 'P':case 'Q':case 'R':case 'S':case 'T':case 'U':
			case 'V':case 'W':case 'X':case 'Y':case 'Z':
			case '$':case '_':/*case '~':case '`':case '|':case '\\':case ':':
			case '?':case '!':case '@':case '%':case '^':case '&':case '*':*/
				tempBuffer=new StringBuffer();
				for (int i=0;i<len;++i) {
					char posC=backlog.charAt(i);
					if (isCharEnd(posC)) return prematureEOF();
					if ((!Character.isLetterOrDigit(posC))&&
						posC!='$'&&posC!='_') {
						return scopeTooWide;
					}
					tempBuffer.append(posC);
				}
				return new ChamlcToken("identifier",tempBuffer.toString(),row,col);
			default:
				if (isCharEnd(current)){
					endOfFile=true;
					return new ChamlcToken(-1,"End of file!",row,col);
				}
				return new ChamlcToken(-1, "Unhandled special case in tokeniser "+backlog.toString(),row,col);
		}
	}
	private Boolean isCharEnd(char c) {
		return c==(char) 3||//EOF
			c==(char) 0xFFFF;//I don't know what this is...
	}
	private Boolean hitEarlyEOF=false;
	private ChamlcToken prematureEOF() {
		hitEarlyEOF=true;
		return new ChamlcToken(-1, "Received an EOF too early! Was expecting the rest of a token!",row,col);
	}
	private Boolean endOfFile=false;
	public Boolean isEnd() {
		return endOfFile;
	}
	/**
	 * Clear all but the latest char of the stack
	 */
	private void eat() {
		char temp=backlog.charAt(backlog.length()-1);
		backlog=new StringBuffer(0);
		backlog.append(temp);
	}
	private int row=1,col=1;
	private void addAnotherToStack() throws IOException {
		char c=(char)fr.read();
		if (c=='\n') {
			col=1;
			row++;
		}else col++;
		backlog.append(c);
	}
	private boolean firstRun=true;
	/**
	 * Get the next token.
	 * 
	 * Continuously pushes chars to a "stack" as long as it still is valid. Once
	 * it isn't valid anymore, it then returns the token matched before adding
	 * the invalidator, then clears the stack, instantly appending the
	 * invalidator so it can be used later.
	 * 
	 * TL;DR: It has a lookahead of 1, and doesn't use any regexp.
	 * 
	 * The row and col values will always be close to the first character of the
	 * token.
	 */
	public ChamlcToken read() throws IOException{
		if (firstRun) {
			addAnotherToStack();
			firstRun=false;
		}
		ChamlcToken tok=stackToTok();
		boolean keepLooking=true;
		needsMoreChars=false;
		while (keepLooking) {
			addAnotherToStack();
			ChamlcToken temp=stackToTok();
			keepLooking=needsMoreChars||!(temp.isErrorCode()||endOfFile);
			if (keepLooking) {
				tok=temp;
				needsMoreChars=false;
			}
		}
		eat();
		if (hitEarlyEOF) return prematureEOF();
		return tok;
	}
	public void close() throws IOException {
		fr.close();
	}
}
