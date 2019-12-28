package TokeniserTools;

public class ChamlcTokenError extends Exception {
	/**
	 * Default serial version ide. I don't know what this is.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * An exception usually involving syntax errors during the tokeniser stage
	 * @param string
	 * @param row
	 * @param col
	 * @param start_r
	 * @param start_c
	 */
	public ChamlcTokenError(String string, int row, int col, int start_r, int start_c) {
		super("The following error was encountered while tokenizing the script:\n===\n\n> "+(string.replace("\n","\n> "))+"\n\nStarting at ["+start_r+","+start_c+"] ranging to ["+row+","+col+"]");
	}
}