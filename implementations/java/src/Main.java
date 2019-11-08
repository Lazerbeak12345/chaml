public class Main {
    private static int[] version = {0,0,0};
    public static void main(String[] args) throws Exception {
        if (args.length==0) helpText();
        else{
            String filename=args[0];
            FileReaderDummy a=new FileReaderDummy(filename);
            //from testing, they have the same output. That's great!
            a.lineByLine();
            a.charByChar();
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
