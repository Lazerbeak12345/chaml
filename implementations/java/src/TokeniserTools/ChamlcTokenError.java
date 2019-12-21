package TokeniserTools;

public class ChamlcTokenError extends Exception {
	/**
	 * Default serial version ide. I don't know what this is.
	 */
	private static final long serialVersionUID = 1L;
	public ChamlcTokenError(String string, int row, int col, int start_r, int start_c) {
		super("The following error was encountered while tokenizing the script:\n"+string+"\nStarting at ["+start_r+","+start_c+"] ranging to ["+row+","+col+"]");
	}
}