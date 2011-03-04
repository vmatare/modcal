package org.modcal;



/**
 * A map of Strings to {@link NumericString}s, actually a subclass of HashMap.
 * @author Victor Mataré
 *
 */

public class NumericSample extends ParameterSample<NumericString> {

	private static final long serialVersionUID = -7809762664103118012L;
	
	public NumericString put(String key, CharSequence value) {
		return super.put(key, new NumericString(value));
	}

}
