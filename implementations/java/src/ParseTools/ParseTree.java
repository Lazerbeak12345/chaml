package ParseTools;

import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;

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
	public ParseTree(String name,ParseNode child,int row, int col,int start_r,int start_c) {
		this(name,child.row,child.col,child.start_r,child.start_c);
		this.add(child);
	}
	public ParseTree(String name,int row,int col,int start_r,int start_c) {
		super(name,row,col,start_r,start_c);
		children=new ArrayList<>();
	}
	public ParseTree(String name,ParseNode child) {
		this(name,child,child.row,child.col,child.start_r,child.start_c);
	}
	@Override
	public Element getAsXML(Document doc) {
		//TODO: handle for empty value
		//TODO: filter out bad xml
		
		Element elm = doc.createElement(getName());
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
		for (int i=0;i<children.size();++i) {
			elm.appendChild(children.get(i).getAsXML(doc));
		}
		return elm;
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
	/**
	 * Add multiple nodes to the list.
	 * See {@link ParseTree#add(ParseNode)}
	 */
	public void add(ArrayList<ParseNode> items) {
		for (int i=0;i<items.size();++i) {
			add(items.get(i));
		}
	}
	public int size() {return children.size();}
	public ParseNode getChild(int i) {return children.get(i);}
	public static ParseTree toTree(ParseNode node) {
		if (node instanceof ParseTree) {
			return (ParseTree) node;
		}
		return null;
	}
}