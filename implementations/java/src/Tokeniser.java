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
            Tokeniser tr=new Tokeniser(args[0]);
            //Outputs xml
            try {
                while(true) {
                    ChamlcToken tok = tr.read();
                    String name=tok.getName(),
                        val=tok.getVal();
                    if (!tr.isEnd()) {
                        if (val.length()>0) System.out.printf("<%s>%s</%s>\n",name,val,name);
                        else System.out.printf("<%s/>\n",name);
                    }
                    if (tok.getNumber()==-1) return;
                }
            } catch (IOException e) {
                try {
                    tr.close();
                } catch (IOException e1) {
                    System.out.println("\n\nError when attempting to close input file!");
                    e1.printStackTrace();
                }
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
    private ChamlcToken stackToTok() {
        if (backlog.length()==0) return new ChamlcToken(-1, "No chars in stack!");
        ChamlcToken scopeTooWide=new ChamlcToken(-1, "The scope for this token was too wide!");
        switch(backlog.charAt(0)) {
            case '/':
                if (backlog.length()==1) {
                    return new ChamlcToken(-1, "This would be a comment, but it needs more indicators.");
                }
                switch(backlog.charAt(1)) {
                    case '/':
                        StringBuffer b=new StringBuffer();
                        for (int i=2;i<backlog.length();++i) {
                            if (backlog.charAt(i)=='\n'&&i+1<backlog.length()) {//if there is a character after the \n
                                return scopeTooWide;
                            }
                            if (i<backlog.length()-1)b.append(backlog.charAt(i));
                        }
                        return new ChamlcToken("comment",b.toString());
                    case'*':
                        StringBuffer b2=new StringBuffer();
                        for (int i=2;i<backlog.length();++i) {
                            if (backlog.charAt(i)=='*'&&i+1<backlog.length()&&
                                backlog.charAt(i+1)=='/'&&i+2<backlog.length()
                                ) {//if there is a character after the * and the /
                                return scopeTooWide;
                            }
                            if (i<backlog.length()-2) b2.append(backlog.charAt(i));
                        }
                        return new ChamlcToken("multiComment",b2.toString());
                    default: return new ChamlcToken(-1, "Stray / character!");
                }
            case '"':
                StringBuffer b=new StringBuffer();
                if (backlog.length()>1) b.append(backlog.charAt(1));
                for (int i=2;i<backlog.length();++i) {
                    if (backlog.charAt(i)=='"'&&i+1<backlog.length()) {//if there is a character after the "
                        return scopeTooWide;
                    }
                    if (i<backlog.length()-1) b.append(backlog.charAt(i));
                }
                return new ChamlcToken("string",b.toString());
            case '\'':
                ChamlcToken wouldBeChar=new ChamlcToken(-1, "This would be a char, but it's missing things.");
                if (backlog.length()==1) return wouldBeChar;
                char c=backlog.charAt(1);
                if (backlog.length()==2) return wouldBeChar;
                if (backlog.charAt(2)=='\'') {
                    return new ChamlcToken("char",Character.toString(c));
                }
                return scopeTooWide;
            case '{':
                if (backlog.length()==1) return new ChamlcToken("openC","");
                else return scopeTooWide;
            case '}':
                if (backlog.length()==1) return new ChamlcToken("closeC","");
                else return scopeTooWide;
            case '(':
                if (backlog.length()==1) return new ChamlcToken("openP","");
                else return scopeTooWide;
            case ')':
                if (backlog.length()==1) return new ChamlcToken("closeP","");
                else return scopeTooWide;
            case '[':
                if (backlog.length()==1) return new ChamlcToken("openS","");
                else return scopeTooWide;
            case ']':
                if (backlog.length()==1) return new ChamlcToken("closeS","");
                else return scopeTooWide;
            case ' ':case '\t':case '\n':case '\r':
                for (int i=0;i<backlog.length();i++) {
                    if (backlog.charAt(i)!=' '&&
                        backlog.charAt(i)!='\t'&&
                        backlog.charAt(i)!='\n'&&
                        backlog.charAt(i)!='\r') return new ChamlcToken(-1,"Wasn't entirely whitespace!");
                }
                return new ChamlcToken("whitespace","");
            case '=':
                if (backlog.length()==1) {
                    //try {
                        //fr.mark(1);//TODO: fix
                        //char c1= (char) fr.read();
                        //fr.reset();//Go back to where it was marked.
                        //if (c1!='>'&&c1!='<') {
                            return new ChamlcToken("set","");
                        //}else return scopeTooWide;
                    //} catch (IOException e) {
                    //    e.printStackTrace();
                    //}
                }
                switch(backlog.charAt(1)) {
                    case '>':return new ChamlcToken("lambda","");
                    case '<':return new ChamlcToken("return","");
                    default:return scopeTooWide;
                }
            case ';':
                if (backlog.length()==1) return new ChamlcToken("semicolon","");
                else return scopeTooWide;
            case ',':
                if (backlog.length()==1) return new ChamlcToken("comma","");
                else return scopeTooWide;
            case '.':
                if (backlog.length()==1) return new ChamlcToken("subitem","");
                else return scopeTooWide;
            case '0':case '1':case '2':case '3':case '4':case '5':case '6':
            case '7':case '8':case '9':
                StringBuffer b3=new StringBuffer();
                for (int i=0;i<backlog.length();++i) {
                    if (!Character.isDigit(backlog.charAt(i))) {
                        return scopeTooWide;
                    }
                    b3.append(backlog.charAt(i));
                }
                return new ChamlcToken("number",b3.toString());
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
                StringBuffer b4=new StringBuffer();
                for (int i=0;i<backlog.length();++i) {
                    char c1=backlog.charAt(i);
                    if ((!Character.isLetterOrDigit(c1))&&
                        c1!='$'&&
                        c1!='_') {
                        return scopeTooWide;
                    }
                    b4.append(backlog.charAt(i));
                }
                return new ChamlcToken("identifier",b4.toString());
            default:
                if (backlog.charAt(0)==(char) 3||//EOF
                    backlog.charAt(0)==(char) 0xFFFF){//I don't know what this is...
                        endOfFile=true;
                        return new ChamlcToken(-1,"End of file!");
                    }
                return new ChamlcToken(-1, "Unhandled special case! "+backlog.toString());
        }
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
    private void addAnotherToStack() throws IOException {
        backlog.append((char)fr.read());
    }
    private boolean firstRun=true;
    /**
     * Get the next token.
     * 
     * Continuously pushes chars to a "stack" as long as it still is valid. Once
     * it isn't valid anymore, it then returns the token matched before adding
     * the invalidator, then clears the stack, instantly appending the
     * invalidator so it can be used later.
     */
    public ChamlcToken read() throws IOException{
        if (firstRun) {
            addAnotherToStack();
            firstRun=false;
        }
        boolean keepLooking=true;
        ChamlcToken tok=stackToTok();
        while (keepLooking) {
            addAnotherToStack();
            ChamlcToken temp=stackToTok();
            keepLooking=!(temp.isErrorCode()||endOfFile);
            if (keepLooking) {
                tok=temp;
            }
        }
        eat();
        return tok;
    }
    public void close() throws IOException {
        fr.close();
    }
}
