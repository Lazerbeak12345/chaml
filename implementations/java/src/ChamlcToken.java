/**
 * ChamlcToken
 */
public class ChamlcToken {
	private int number;
	public static String[] tokens={
		"comment",
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
	ChamlcToken(int number,String val) {
		this.number=number;
		if (number<0) {
			this.name="Error"+Integer.toString(number);//A negative is an error code
		}else this.name=tokens[number];
		this.val=val;
	}
	ChamlcToken(String n,String val) {
		this(nameToInt(n),val);
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
}
