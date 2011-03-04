package org.modcal;

public class NumericString implements CharSequence {
	
	private final String value;
	
	public NumericString(CharSequence s) {
		String tmp;
		tmp = s.toString();
		Double.valueOf(tmp);
		this.value = tmp.replaceFirst("0+$", "");
	}

	public static boolean isValid(String s) {
		try {
			Double.valueOf(s);
			return true;
		}
		catch (NumberFormatException e) {
			return false;
		}
	}
	
	public static String pad(Number v, int len) {
		len -= 1;
		StringBuilder rv = new StringBuilder();
		if (v.doubleValue() >= 0) rv.append(" ");
		rv.append(v);
		if (rv.length() > len) rv.setLength(len);
		else if (rv.length() < len) for (int i = rv.length(); i <len; i++)
			rv.append(" ");
		return rv.toString();
	}
	
	public char charAt(int index) { return value.charAt(index); }
	public int length() { return value.length(); }

	public CharSequence subSequence(int start, int end) {
		return value.subSequence(start, end);
	}

	public String getValue() { return value; }
	public String toString() { return value.toString(); }

}
