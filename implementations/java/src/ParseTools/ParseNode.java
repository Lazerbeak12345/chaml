package ParseTools;
/**
 * A tree or leaf node
 */
public abstract class ParseNode{
	int row,col;
	public ParseNode(String name,int row,int col) {
		this.row=row;
		this.col=col;
		this.name=name;//TODO: Have the nodes numbered like tokens are
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
