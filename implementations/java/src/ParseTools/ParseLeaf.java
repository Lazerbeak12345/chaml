package ParseTools;
import TokeniserTools.ChamlcToken;

public class ParseLeaf extends ParseNode {
	String value;
	public ParseLeaf(ChamlcToken value) {
		super(value.getName(),value.row,value.col,value.start_r,value.start_c);
		this.value=value.getVal();
	}
	@Override
	public int getNumber() {//TODO: remove this code to save on memory later?
		return new ChamlcToken(getName(), value, row, col,start_r,start_c).getNumber();
	}
	@Override
	public void printAsXML() {
		System.out.printf("<_LEAF_ row=\"%s\" col=\"%s\"  start_r=\"%d\" start_c=\"%d\" name=\"%s\">",row,col,start_r,start_c,getName());
		//new ChamlcToken(getName(), value, row, col,start_r,start_c).printAsXML();
		System.out.print(value);
		System.out.println("</_LEAF_>");
	}
}
