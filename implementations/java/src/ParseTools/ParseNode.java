package ParseTools;

import java.util.ArrayList;

import TokeniserTools.ChamlcToken;

/**
 * A tree or leaf node
 */
public abstract class ParseNode {
	int row, col, start_r, start_c;
	public static ArrayList<String> nodes;

	public ParseNode(String name, int row, int col, int start_r, int start_c) {
		this.row = row;
		this.col = col;
		this.start_r = start_r;
		this.start_c = start_c;
		this.number = nameToInt(name);
	}

	private int number;// ,tokNumber=0;

	public int getNumber() {
		return number;
	}

	/**
	 * Convert a given name into the respective int.
	 * 
	 * NOTE: It's actually really slow, as it's using a linear search.
	 * 
	 * @param name
	 * @return
	 */
	public static int nameToInt(String name) {
		for (int i=0;i<nodes.size();++i) {
			if (name.equals(nodes.get(i))) return i+ChamlcToken.tokens.length;
		}
		return ChamlcToken.nameToInt(name);
	}
	/**
	 * Get the name
	 */
	public String getName() {
		return intToName(number);
	}
	/**
	 * Convert a given number into the corresponding name.
	 * @param number The number representing the name.
	 * @return The name.
	 */
	public static String intToName(int number) {
		if (number<0) {
			return "ERROR";//A negative is an error code
		}else if(number<ChamlcToken.tokens.length){
			//System.out.print("<!--??-->");
			return ChamlcToken.tokens[number];
		}else return nodes.get(number-ChamlcToken.tokens.length);
	}
	/**
	 * Print this ParseNode as XML
	 */
	abstract public void printAsXML();
}
