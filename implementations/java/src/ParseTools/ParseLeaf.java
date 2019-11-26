package ParseTools;
import TokeniserTools.ChamlcToken;

public class ParseLeaf extends ParseNode {
	ChamlcToken value;//The token itself holds the value
	public ParseLeaf(ChamlcToken value,int row, int col) {
		super(row, col);
		this.value=value;
	}
	public ParseLeaf(ChamlcToken value) {
		super(value.row,value.col);
		this.value=value;
	}
	public ParseLeaf(String n,String val,int row, int col) {
		super(row, col);
		this.value=new ChamlcToken(n, val, row, col);
	}
	@Override
	public void printAsXML() {
		value.printAsXML();
	}
}
