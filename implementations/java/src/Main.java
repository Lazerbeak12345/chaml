public class Main {
    private static int[] version = {0,0,0};
    public static void main(String[] args) {
        if (args.length==0) helpText();
    }
    private static void helpText() {
        System.out.printf("jchamlc version %d.%d.%d\n\n",version[0],version[1],version[2]);
        System.out.println("No help text yet. Why not add some; this is open source!");
    }
}
