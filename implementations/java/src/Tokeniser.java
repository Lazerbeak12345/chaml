import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;


/**
 * Tokenises a file
 */
class Tokeniser extends Reader{
    FileReader
    public Tokeniser(String fileName) throws FileNotFoundException {
        
    }
    public String readLine() throws IOException {
        StringBuffer b=new StringBuffer();
        int c;
        while ((c=read())!=1) {
            b.append((char)c);
            if (c==(int)'\n') break;
        }
        return b.toString();
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void close() throws IOException {
        // TODO Auto-generated method stub

    }
}
