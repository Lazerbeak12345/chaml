package TokeniserTools;

import org.w3c.dom.Node;

//import org.w3c.dom.*;
//import javax.xml.*;
/**
 * A token for the Java CHAML compiler system.
 * 
 * This Java variant is _not_ made for use with CHAML language variants,
 * however, it _is_ designed to test the architecture of the system before it is
 * actually stable.
 * 
 * @author Lazerbeak12345
 */
public class ChamlcToken {
	/** The value that this token corresponds to {@link ChamlcToken.tokens} */
	private int number;
	/** The source-code position of each token. */
	public int row,col,start_r,start_c;
	/** An array of all of the token names, as you would see them in XML */
	public static String[] tokens={
		"comment",//includes the newline
		"import",
		"syntaxExtension",
		"multiComment",
		"string",
		"char",
		"openC",
		"closeC",
		"openP",
		"closeP",
		"openS",
		"closeS",
		"whitespace",
		"lambda",
		"return",
		"equals",
		"overload",
		"statementSeparator",
		"comma",
		"subitem",
		"number",
		"identifier",
	};
	/** The value associated with the token */
	private String val;
	/** Get the value associated with the token */
	public String getVal() {
		return val;
	}
	
	/**
	 * Convert a given name into the respective int.
	 * 
	 * NOTE: It's actually really slow, as it's using a linear search.
	 * 
	 * @param name The name to search for.
	 * @return The integer corresponding to the inputted name.
	 */
	public static int nameToInt(String name) {
		for (int i=0;i<tokens.length;++i) {
			if (name.equals(tokens[i])) return i;
		}
		return -1;
	}
	/**
	 * Make a CHAMLc token based on the index and the position
	 * @param number Identifier number of token type
	 * @param val Contents of the token
	 * @param row Last row of this token in source code
	 * @param col Last column of this token in source code
	 * @param start_r First row of this token in source code
	 * @param start_c First column of this token in source code
	 * @throws KeyException if the given name is not found
	 */
	public ChamlcToken(int number,String val,int row,int col,int start_r,int start_c) {
		this.number=number;
		this.row=row;
		this.col=col;
		this.start_r=start_r;
		this.start_c=start_c;
		this.val=val;
	}
	/**
	 * Make a CHAMLc token based on the name and the position
	 * @param number Identifier number of token type
	 * @param val Contents of the token
	 * @param row Last row of this token in source code
	 * @param col Last column of this token in source code
	 * @param start_r First row of this token in source code
	 * @param start_c First column of this token in source code
	 */
	public ChamlcToken(String n,String val,int row, int col,int start_r,int start_c) {
		this(nameToInt(n),val,row,col,start_r,start_c);
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return tokens[number];
	}
	/**
	 * @return the number
	 */
	public int getNumber() {
		return number;
	}
	/**
	 * Compare if this token matches another token
	 * @param obj
	 * @return
	 */
	public boolean equals(ChamlcToken obj) {
		return this.getNumber()==obj.getNumber()&&
			this.getVal()==obj.getVal();
	}
	/**
	 * Compare if two tokens match
	 * @param obj1 The first token
	 * @param obj2 the second token
	 * @return
	 */
	public static boolean equals(ChamlcToken obj1,ChamlcToken obj2) {
		return obj1.equals(obj2);
	}
	public Node getAsXML() {
		return null;
	}
	/** Get the token as an xml string */
	public String getAsXMLString() {
		var out=new StringBuffer();
		if (val.length()>0) {
			//https://stackoverflow.com/a/46637835
			out.append("<"+getName()+" row=\""+row+"\" col=\""+col+"\" start_r=\""+start_r+"\" start_c=\""+start_c+"\">");
			for (int i=0;i<val.length();++i) {
				switch(val.charAt(i)) {
					case '<':
						out.append("&lt;");
						break;
					case '>':
						out.append("&gt;");
						break;
					case '"':
						out.append("&quot;");
						break;
					case '\'':
						out.append("&apos;");
						break;
					case '&':
						out.append("&amp;");
						break;
					default:out.append(val.charAt(i));
				}
			}
			out.append("</"+getName()+">\n");
		}else out.append("<"+getName()+" row=\""+row+"\" col=\""+col+"\" start_r=\""+start_r+"\" start_c=\""+start_c+"\"/>\n");
		return out.toString();
	}
	/** Print the token as it would show up in the source code */
	public void printLiteral() {
		switch(getName()) {
			case "comment":
				System.out.print("//");
				System.out.println(val);
				break;
			case "multiComment":
				System.out.print("/*");
				System.out.print(val);
				System.out.print("*/");
				break;
			case "string":
				System.out.print('"');
				System.out.print(val);
				System.out.print('"');
				break;
			case "char":
				System.out.print('\'');
				System.out.print(val);
				System.out.print('\'');
				break;
			case "openC":
				System.out.print('{');
				break;
			case "closeC":
				System.out.print('}');
				break;
			case "openP":
				System.out.print('(');
				break;
			case "closeP":
				System.out.print(')');
				break;
			case "openS":
				System.out.print('[');
				break;
			case "closeS":
				System.out.print(']');
				break;
			case "whitespace":
				System.out.print(' ');
				break;
			case "lambda":
				System.out.print("=>");
				break;
			case "return":
				System.out.print("<=");
				break;
			case "equals":
				System.out.print('=');
				break;
			case "overload":
				System.out.print('~');
				break;
			case "statementSeparator":
				System.out.print(';');
				break;
			case "comma":
				System.out.print(',');
				break;
			case "subitem":
				System.out.print('.');
				break;
			case "import":
				System.out.print("#<");
				System.out.print(val);
				System.out.print(">");
				break;
			case "syntaxExtension":
				System.out.print("#+");
				System.out.print(val);
				System.out.print(">");
				break;
			case "number":
			case "identifier":
			default:
				System.out.print(val);
				break;
		}
	}
}
