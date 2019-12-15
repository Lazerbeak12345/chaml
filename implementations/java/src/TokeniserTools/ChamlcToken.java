package TokeniserTools;
/**
 * ChamlcToken
 */
public class ChamlcToken {
	private int number;
	public int row,col,start_r,start_c;
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
	private String val;//The value associated with the token
	public String getVal() {
		return val;
	}
	/**
	 * Convert a given name into the respective int.
	 * 
	 * NOTE: It's actually really slow, as it's using a linear search.
	 * 
	 * @param name
	 * @return
	 */
	public static int nameToInt(String name) {
		for (int i=0;i<tokens.length;++i) {
			if (name.equals(tokens[i])) return i;
		}
		return -1;
	}
	public ChamlcToken(int number,String val,int row,int col,int start_r,int start_c) {
		this.row=row;
		this.col=col;
		this.start_r=start_r;
		this.start_c=start_c;
		this.number=number;
		this.val=val;
	}
	public ChamlcToken(String n,String val,int row, int col,int start_r,int start_c) {
		this(nameToInt(n),val,row,col,start_r,start_c);
	}
	/**
	 * @return the name
	 */
	public String getName() {
		if (number<0) {
			return "error";//A negative is an error code
		}else return tokens[number];
	}
	/**
	 * @return the number
	 */
	public int getNumber() {
		return number;
	}
	public boolean equals(ChamlcToken obj) {
		return this.getNumber()==obj.getNumber()&&
			this.getVal()==obj.getVal();
	}
	public boolean isErrorCode() {
		return getNumber()<0;
	}
	public void printAsXML() {
		if (val.length()>0) {
			//https://stackoverflow.com/a/46637835
			if (number>=0)System.out.printf("<%s row=\"%d\" col=\"%d\" start_r=\"%d\" start_c=\"%d\">",getName(),row,col,start_r,start_c);
			else System.out.printf("<%s code=\"%d\" row=\"%d\" col=\"%d\" start_r=\"%d\" start_c=\"%d\">",getName(),number*-1,row,col,start_r,start_c);
			for (int i=0;i<val.length();++i) {
				switch(val.charAt(i)) {
					case '<':
						System.out.print("&lt;");
						break;
					case '>':
						System.out.print("&gt;");
						break;
					case '"':
						System.out.print("&quot;");
						break;
					case '\'':
						System.out.print("&apos;");
						break;
					case '&':
						System.out.print("&amp;");
						break;
					default:System.out.print(val.charAt(i));
				}
			}
			System.out.printf("</%s>\n",getName());
		}else System.out.printf("<%s row=\"%d\" col=\"%d\" start_r=\"%d\" start_c=\"%d\"/>\n",getName(),row,col,start_r,start_c);
	}
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
				System.out.print("#<[");
				break;
			case "syntaxExtension":
				System.out.print("#+[");
			case "number":
			case "identifier":
			default:
				System.out.print(val);
				break;
		}
	}
}
