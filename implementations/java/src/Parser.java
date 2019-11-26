import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import ParseTools.*;

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
	ArrayList<ParseNode> backlog,stack;
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
	 * The one place to do 90% of constructor related stuff
	 */
	private void init() {
		
	}
	public void parse() throws IOException {
		ParseLeaf leaf=new ParseLeaf(tr.read());
		leaf.printAsXML();
		ParseTree tree=new ParseTree(leaf);
		tree.printAsXML();
	}
}