package ParseTools;

import java.util.ArrayList;

public class ParseTreeRoot extends ParseTree {
	String src;

	public ParseTreeRoot(String src, ArrayList<ParseNode> children) {
		super("ROOT",children);
		this.src=src;
	}
	/*public ParseTreeRoot(String src,ParseNode child){
		super("ROOT",child);
		this.src=src;
	}*/
	/*public ParseTreeRoot(String src){
		super("ROOT",0,0,0,0);
		this.src=src;
	}*/
	@Override
	public void printAsXML() {
		//TODO: filter out bad xml
		System.out.printf("<%s src=\"%s\" row=\"%d\" col=\"%d\" start_c=\"%d\" start_r=\"%d\"",getName(),src,row,col,start_r,start_c);
		if (size()==0) System.out.print("/>");
		else{
			System.out.print(">");
			for (int i=0;i<size();++i) {
				getChild(i).printAsXML();
			}
			System.out.print("</"+ getName() + ">");
		}
	}
}