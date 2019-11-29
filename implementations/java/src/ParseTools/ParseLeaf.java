package ParseTools;
import TokeniserTools.ChamlcToken;

public class ParseLeaf extends ParseNode {
	ChamlcToken value;//The token itself holds the value
	public ParseLeaf(ChamlcToken value,int row, int col) {
		super(value.getName(),row, col);
		this.value=value;
	}
	public ParseLeaf(ChamlcToken value) {
		super(value.getName(),value.row,value.col);
		this.value=value;
	}
	public ParseLeaf(String n,String val,int row, int col) {
		super(n,row,col);
		this.value=new ChamlcToken(n, val, row, col);
	}
	@Override
	public void printAsXML() {
		System.out.printf("<leaf row=\"%s\" col=\"%s\">",row,col);
		value.printAsXML();
		System.out.println("</leaf>");
	}
}
