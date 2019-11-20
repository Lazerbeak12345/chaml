/**
 * ChamlcToken
 */
public class ChamlcToken {
	private int value;
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
		"set",
		"semicolon",
		"comma",
		"subitem",
		"number",
		"identifier",
	};
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
	ChamlcToken(int value) {
		this.value=value;
		this.name=tokens[value];
	}
	ChamlcToken(String n) {
		this(nameToInt(n));
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}
	public boolean equals(ChamlcToken obj) {
		return this.getValue()==obj.getValue();
	}
}
