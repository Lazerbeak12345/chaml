package TokeniserTools;
/**
 * ChamlcToken
 */
public class ChamlcToken {
	private int number;
	public int row,col;
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
		"semicolon",
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
	private String name;
	public ChamlcToken(int number,String val,int row,int col) {
		this.row=row;
		this.col=col;
		this.number=number;
		if (number<0) {
			this.name="error";//A negative is an error code
		}else this.name=tokens[number];
		this.val=val;
	}
	public ChamlcToken(String n,String val,int row, int col) {
		this(nameToInt(n),val,row,col);
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
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
			if (number>=0)System.out.printf("<%s row=\"%d\" col=\"%d\">",name,row,col);
			else System.out.printf("<%s code=\"%d\" row=\"%d\" col=\"%d\">",name,number*-1,row,col);
			for (int i=0;i<val.length();++i) {
				switch(val.charAt(i)) {
					case '<':
						System.out.print("&lt;");
						break;
					case '>':
						System.out.print("&gt;");
						break;
					case '"'://TODO: Add '
						System.out.print("&quot;");
						break;
					case '&':
						System.out.print("&apos;");
						break;
					default:System.out.print(val.charAt(i));
				}
			}
			System.out.printf("</%s>\n",name);
		}else System.out.printf("<%s row=\"%d\" col=\"%d\"/>\n",name,row,col);
	}
	public void printLiteral() {
		switch(name) {
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
			case "semicolon":
				System.out.print(';');
				break;
			case "comma":
				System.out.print(',');
				break;
			case "subitem":
				System.out.print('.');
				break;
			case "import":
				System.out.print("#[");
				break;
			case "syntaxExtension":
				System.out.print("#+");
			case "number":
			case "identifier":
			default:
				System.out.print(val);
				break;
		}
	}
}
