package ParseTools;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;

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
	public Element getAsXML(Document doc) {
		//TODO: handle for empty value
		//TODO: filter out bad xml
		
		Element elm = doc.createElement(getName());
		//Element elm = doc.createElement("chamlc_token:"+getName());
		Attr attr = doc.createAttribute("row");
		attr.setValue(String.valueOf(row));
		elm.setAttributeNode(attr);
		doc.createAttribute("col");
		attr.setValue(String.valueOf(col));
		elm.setAttributeNode(attr);
		doc.createAttribute("start_r");
		attr.setValue(String.valueOf(start_r));
		elm.setAttributeNode(attr);
		doc.createAttribute("start_c");
		attr.setValue(String.valueOf(start_c));
		elm.setAttributeNode(attr);
		elm.appendChild(doc.createTextNode(value));

		return elm;
	}
}
