package ParseTools;
/**
 * A tree or leaf node
 */
public abstract class ParseNode{
	int row,col;
	public ParseNode(int row,int col) {
		this.row=row;
		this.col=col;
	}
	/**
	 * The name that should be printed
	 */
	String name;
	/**
	 * Print this ParseNode as XML
	 */
	abstract public void printAsXML();
}
