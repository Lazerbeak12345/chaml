package TokeniserTools;

public class ChamlcTokenError extends Exception {
	public ChamlcTokenError(String string) {
		super(string);
	}

	public ChamlcTokenError(String string, int row, int col, int start_r, int start_c) {
		this(string+"\nStarting at ["+start_r+","+start_c+"] ranging to ["+row+","+col+"]");
	}
}