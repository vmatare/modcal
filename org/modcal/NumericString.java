/*
 *   This file is part of ModCal.
 *
 *   ModCal is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   ModCal is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with ModCal.  If not, see <http://www.gnu.org/licenses/>.
 *   
 *   Additional permission under GNU GPL version 3 section 7
 *   
 *   If you modify this Program, or any covered work, by linking or
 *   combining it with Mule ESB (or a modified version of Mule ESB),
 *   containing parts covered by the terms of CPAL, the licensors of
 *   this Program grant you additional permission to convey the
 *   resulting work.
 *   {Corresponding Source for a non-source form of such a combination
 *   shall include the source code for the parts of Mule ESB used as
 *   well as that of the covered work.}
 *   
 */

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
