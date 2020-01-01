package ParseTools;

import TokeniserTools.ChamlcToken;

public class ParseLeaf extends ParseNode {
	String value;
	public ParseLeaf(ChamlcToken value) {
		super(value.getName(),value.row,value.col,value.start_r,value.start_c);
		this.value=value.getVal();
	}
	/*@Override
	public int getNumber() {//TO DO: remove this code to save on memory later?
		return new ChamlcToken(getName(), value, row, col,start_r,start_c).getNumber();
	}*/
	@Override
	public String getAsXML() {
		//TODO: handle for empty value
		//TODO: filter out bad xml
		return "<_LEAF_ row=\""+row+"\" col=\""+col+"\" start_r=\""+start_r
		+"\" start_c=\""+start_c+"\" name=\""+getName()+
		"\" number=\""+getNumber()+"\">"+
			value+
		"</_LEAF_>";
	}
}
