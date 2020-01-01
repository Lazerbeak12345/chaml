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
	public String getAsXML() {
		var a=new StringBuffer();
		//TODO: filter out bad xml
		a.append("<"+getName()+" src=\""+src+"\" row=\""+row+"\" col=\""+col+"\" start_c=\""+start_c+"\" start_r=\""+start_r+"\"");
		if (size()==0) System.out.print("/>");
		else{
			a.append(">");
			for (int i=0;i<size();++i) {
				a.append(getChild(i).getAsXML());
			}
			a.append("</"+ getName() + ">");
		}
		return a.toString();
	}
}