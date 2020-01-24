package ParseTools;

import java.util.ArrayList;

public class ParseTreeRoot extends ParseTree {
	String src;

	public ParseTreeRoot(String src, ArrayList<ParseNode> children) {
		super("ROOT",children);
		this.src=src;
	}
	public ParseTreeRoot(ArrayList<ParseNode> children) {
		super("ROOT",children);
		this.src="";
	}
	public ParseTreeRoot(String src,ParseNode child){
		super("ROOT",child);
		this.src=src;
	}
	/*public ParseTreeRoot(String src){
		super("ROOT",0,0,0,0);
		this.src=src;
	}*/
}