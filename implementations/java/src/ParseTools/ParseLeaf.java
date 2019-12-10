package ParseTools;
import TokeniserTools.ChamlcToken;

public class ParseLeaf extends ParseNode {
	String value;
	public ParseLeaf(ChamlcToken value) {
		super(value.getName(),value.row,value.col);
		this.value=value.getVal();
	}
	public int getNumber() {//TODO: remove this code to save on memory later
		return new ChamlcToken(getName(), value, row, col).getNumber();
	}
	@Override
	public void printAsXML() {
		System.out.printf("<leaf row=\"%s\" col=\"%s\">",row,col);
		new ChamlcToken(getName(), value, row, col).printAsXML();
		System.out.println("</leaf>");
	}
}
