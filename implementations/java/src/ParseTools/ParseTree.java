package ParseTools;

import java.util.ArrayList;

public class ParseTree extends ParseNode {
	private ArrayList<ParseNode> children;

	public ParseTree(String name, ArrayList<ParseNode> children, int row, int col, int start_r, int start_c) {
		super(name,row,col,start_r,start_c);
		this.children=new ArrayList<>();
		for(ParseNode node:children) {
			add(node);
		}
	}
	public ParseTree(String name,ArrayList<ParseNode> children) {
		this(name,children,
			children.get(children.size()-1).row,
			children.get(children.size()-1).col,
			children.get(0).start_r,
			children.get(0).start_c);
	}
	/*public ParseTree(String name,ParseNode child,int row, int col,int start_r,int start_c) {
		super(name,row,col,start_r,start_c);
		children=new ArrayList<>();
		this.add(child);
	}*/
	/*public ParseTree(String name,int row,int col,int start_r,int start_c) {
		super(name,row,col,start_r,start_c);
		children=new ArrayList<>();
	}*/
	/*public ParseTree(String name,ParseNode child) {
		this(name,child,child.row,child.col,child.start_r,child.start_c);
	}*/
	@Override
	public String getAsXML() {
		if (children.size()==0)
			return "<"+getName()+" row=\""+row+"\" col=\""+col+"\" start_c=\""+start_c+"\" start_r=\""+start_r+"\"/>";
		else{
			var a=new StringBuffer();
			a.append("<"+getName()+" row=\""+row+"\" col=\""+col+"\" start_c=\""+start_c+"\" start_r=\""+start_r+"\">");
			for (int i=0;i<children.size();++i) {
				a.append(children.get(i).getAsXML());
			}
			a.append("</"+ getName() + ">");
			return a.toString();
		}
	}
	/**
	 * Add a node to the list, updating the row,col,start_r,&start_col
	 * @param node the node to be added
	 * @return whatever {@link ArrayList#add(Object)} returns
	 */
	public boolean add(ParseNode node) {
		if (node.row>row||(node.row==row&&node.col>col)) {
			row=node.row;
			col=node.col;
		}else if (node.start_r<start_r||(node.start_r==start_r&&node.start_r>start_r)) {
			start_r=node.start_r;
			start_c=node.start_c;
		}
		return children.add(node);
	}
	public int size() {return children.size();}
	public ParseNode getChild(int i) {return children.get(i);}
}