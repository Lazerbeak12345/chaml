import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Tokenize a file. Made for the Java CHAML Compiler (jchamlc)
 */
class Tokeniser{
    public static void main(String[] args) {
        if (args.length>1) {
            System.out.println("Not enough args!");
            return;
        }
        try{
            Tokeniser t=new Tokeniser(args[0]);
            //Output CSV file of tokens.
        }catch(FileNotFoundException e) {
            System.out.printf("There is no file by the name %s\n",args[0]);
        }
    }
    /**
     * The filereader that this uses internally.
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
    private void addAnotherToStack() throws IOException {
        backlog.append((char)fr.read());
    }
    private boolean doesStackMatch(ChamlcToken c) {

    }
    private ChamlcToken stackToTok() {

    }
    /**
     * Get the next token.
     * 
     * Continuously pushes chars to a "stack" as long as it still is valid. Once
     * it isn't valid anymore, it then returns the token matched before adding
     * the invalidator, then clears the stack, instantly appending the
     * invalidator so it can be used later.
     */
    public ChamlcToken read() throws IOException{
        boolean matchFound=true;
        ChamlcToken tok=new ChamlcToken(0);
        while (matchFound) {
            addAnotherToStack();
            matchFound=doesStackMatch(tok);
            if (matchFound) {//if it still matches
                tok=stackToTok();
                char temp=backlog.charAt(backlog.length()-1);
                backlog=new StringBuffer(0);
                backlog.append(temp);
            }
        }
        return tok;
    }
}
