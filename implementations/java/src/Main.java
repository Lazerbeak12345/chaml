import java.io.IOException;

//import java.io.FileNotFoundException;
//import java.io.IOException;

public class Main {
	//private static int[] version = {0,0,0};
	public static void main(String[] args) {
		//if (args.length==0) helpText();
		//else{
			String[]a={"tests/testLib.chaml"};
			Tokeniser.main(a);
			//Parser.main(a);
			/*String filename=args[0];
			try{
				Tokeniser tok=new Tokeniser(filename);
				System.out.println(tok.read());
				System.out.println(tok.read());
				tok.close();
			}catch(FileNotFoundException e) {
				System.out.printf("Couldn't find file \"%s\".\n",filename);
			}catch(IOException e) {
				System.out.printf("IOException with file \"%s\".",filename);
			}*/
		//}
	}

	/**
	 * Print help text
	 *
	private static void helpText() {
		System.out.printf("jchamlc version %d.%d.%d\n\n",version[0],version[1],version[2]);
		System.out.println("No help text yet. Why not add some; this is open source!");
	}//*/
}
