import java.io.FileNotFoundException;

public class Main {
    private static int[] version = {0,0,0};
    public static void main(String[] args) throws Exception {
        if (args.length==0) helpText();
        else{
            String filename=args[0];
  //          try{
                FileReaderDummy f=new FileReaderDummy(filename);
//            }catch(FileNotFoundException e) {
      //          System.out.printf("No file by the name of \"%s\" could be found\n",filename);
    //        }
        }
    }

    /**
     * Print help text
     */
    private static void helpText() {
        System.out.printf("jchamlc version %d.%d.%d\n\n",version[0],version[1],version[2]);
        System.out.println("No help text yet. Why not add some; this is open source!");
    }
}
