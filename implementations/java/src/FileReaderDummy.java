import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

class FileReaderDummy{
    private String filename;
    FileReaderDummy(String filename) {
        this.filename=filename;
    }
    public void charByChar() {
        //Indirectly based on code from http://download.oracle.com/javase/tutorial/essential/io/charstreams.html
        try{
            FileReader inputStream = new FileReader(filename);
            //FileWriter outputStream = new FileWriter("characteroutput.txt");
            int c;
            while ((c = inputStream.read()) != -1) {
                //outputStream.write(c);
                System.out.print((char)c);
            }
            inputStream.close();//I really want this to be in a finally block
            //outputStream.close();
        }catch (FileNotFoundException e) {
            System.err.printf("Unable to find the file: %s\n",filename);
        }catch (IOException e) {
            System.err.printf("Unable to read the file: %s\n",filename);
        }
    }
    public void lineByLine() {
        //Based on code from https://www.java2novice.com/java-file-io-operations/read-line-from-file/
        String strLine = "";
        try {
            BufferedReader br = new BufferedReader( new FileReader(filename));
            while( (strLine = br.readLine()) != null){
                System.out.println(strLine);
            }
            br.close();
        } catch (FileNotFoundException e) {
            System.err.printf("Unable to find the file: %s\n",filename);
        } catch (IOException e) {
            System.err.printf("Unable to read the file: %s\n",filename);
        }
    }
}