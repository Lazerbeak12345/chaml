package ParseTools;

import java.util.ArrayList;

public class ParseTree extends ParseNode {
	ArrayList<ParseNode> children;
	public ParseTree(String name,ArrayList<ParseNode> children,int row, int col) {
		super(name,row, col);
		this.children=children;
	}
	public ParseTree(String name,ArrayList<ParseNode> children) {
		super(name,children.get(0).row,children.get(0).col);
		this.children=children;
	}
	public ParseTree(String name,ParseNode child,int row, int col) {
		super(name,row, col);
		children=new ArrayList<>();
		children.add(child);
	}
	public ParseTree(String name,int row, int col) {
		super(name,row, col);
		children=new ArrayList<>();
	}
	public ParseTree(String name,ParseNode child) {
		super(name,child.row, child.col);
		this.children=new ArrayList<ParseNode>();
		//this.children.set(0, child);
		this.children.add(child);
	}
	@Override
	public void printAsXML() {
		if (children.size()==0)
			System.out.printf("<%s row=\"%d\" col=\"%d\"/>",name,row,col);
		else{
			System.out.printf("<%s row=\"%d\" col=\"%d\">",name,row,col);
			for (int i=0;i<children.size();++i) {
				children.get(i).printAsXML();
			}
			System.out.print("</"+name+">");
		}
	}
	/**
	 * Add a node to the list
	 * @param node the node to be added
	 * @return whatever {@link ArrayList#add(Object)} returns
	 */
	public boolean add(ParseNode node) {
		return children.add(node);
	}
}