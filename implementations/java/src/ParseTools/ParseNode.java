package ParseTools;

/**
 * A tree or leaf node
 */
public abstract class ParseNode {
	int row, col, start_r, start_c;

	public ParseNode(String name, int row, int col, int start_r, int start_c) {
		this.row = row;
		this.col = col;
		this.start_r = start_r;
		this.start_c = start_c;
		this.name = name;
	}
	private String name;
	/**
	 * Get the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * Get this ParseNode as an XML String
	 */
	abstract public String getAsXML();
}
