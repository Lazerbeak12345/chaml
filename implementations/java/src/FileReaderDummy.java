import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

class FileReaderDummy{
    FileInputStream input;
    FileReaderDummy(String filename) throws FileNotFoundException {
        input=new FileInputStream(filename);
    }
	public void testFile(String filename) {
        try{
            byte b[]={0,0,0,0,0};
            input.read(b);
            System.out.println(b[5]);
            for (byte i : b) {
                System.out.println(i);
            }
        } catch (IOException e) {
            System.out.println("Byte could not be read!");
            return;
        } finally {
            try{
                input.close();
            } catch (IOException e) {
                System.out.println("File could not be closed!");
            }
        }
    }
}