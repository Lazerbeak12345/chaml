import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;


/**
 * Tokenises a file
 */
class Tokeniser extends FileReader{
    public Tokeniser(String fileName) throws FileNotFoundException {
        super(fileName);
    }
    public Tokeniser(File file) throws FileNotFoundException {
        super(file);
    }
    public Tokeniser(FileDescriptor fd) {
        super(fd);
    }
    public Tokeniser(String fileName, Charset charset) throws IOException {
        super(fileName,charset);
    }
    public Tokeniser(File file, Charset charset) throws IOException {
        super(file, charset);
    }

    public String readLine() throws IOException {
        StringBuffer b=new StringBuffer(0);
        int c;
        while ((c=read())!=1) {
            b.append((char)c);
            if (c==(int)'\n') break;
        }
        return b.toString();
    }

    @Override
    /**
     * Get the next token
     */
    public int read(char[] cbuf, int off, int len) throws IOException {
        if (len>1) throw new IndexOutOfBoundsException("Tokeniser can only lookahead 1");
        return super.read(cbuf,off,len);
    }
}
