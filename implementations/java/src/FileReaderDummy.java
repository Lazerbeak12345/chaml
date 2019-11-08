import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

class FileReaderDummy{
    FileReaderDummy(String filename) {
        //Based on code from https://www.java2novice.com/java-file-io-operations/read-line-from-file/
        BufferedReader br = null;
        String strLine = "";
        try {
            br = new BufferedReader( new FileReader(filename));
            while( (strLine = br.readLine()) != null){
                System.out.println(strLine);
            }
        } catch (FileNotFoundException e) {
            System.err.printf("Unable to find the file: %s\n",filename);
        } catch (IOException e) {
            System.err.printf("Unable to read the file: %s\n",filename);
        }
    }
}