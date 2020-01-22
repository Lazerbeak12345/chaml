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
	@Override
	public String getAsXML() {
		var a=new StringBuffer();
		//TODO: filter out bad xml
		//a.append("<"+getName()+" src=\""+src+"\" row=\""+row+"\" col=\""+col+"\" start_c=\""+start_c+"\" start_r=\""+start_r+"\""+
		a.append("<"+getName()+" row=\""+row+"\" col=\""+col+"\" start_c=\""+start_c+"\" start_r=\""+start_r+"\""+
		" xmlns:chamlc_token=\"https://github.com/Lazerbeak12345/chaml/blob/master/implementations/java/src/Tokeniser.java\""+
		" xmlns=\"https://github.com/Lazerbeak12345/chaml/blob/master/implementations/java/src/Parser.java\"");
		if (size()==0) System.out.print("/>");
		else{
			a.append(">");//TODO: stop adding ws (especially tabs)
			for (int i=0;i<size();++i) {
				a.append("\n\t"+getChild(i).getAsXML().replaceAll("\\n","\n\t"));
			}
			a.append("</"+ getName() + ">");
		}
		return a.toString();
	}
}