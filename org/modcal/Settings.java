package org.modcal;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Settings {
	private static final String BUNDLE_NAME = "modcal"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private Settings() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
