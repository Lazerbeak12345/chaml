package ParseTools;

import java.util.ArrayList;

public class ParseTree extends ParseNode {
	ArrayList<ParseNode> children;
	public ParseTree(ArrayList<ParseNode> children,int row, int col) {
		super(row, col);
		this.children=children;
	}
	public ParseTree(ParseNode child,int row, int col) {
		super(row, col);
		
	}
	public ParseTree(ParseNode child) {
		super(child.row, child.col);
		this.children.set(0, child);
	}
	@Override
	public void printAsXML() {
		System.out.printf("<%s row=\"%d\" col=\"%d\">",name,row,col);
		for (int i=0;i<children.size();++i) {
			children.get(i).printAsXML();
		}
		System.out.print("</"+name+">");
	}
}