package utils;

import java.util.Locale;

public class I18nUtils {
	/**
	 * @param locale in the form en_US
	 * @return Country name in user readable format
	 */
	public static String getCountry(String localeString) {
		return getLocale(localeString).getDisplayCountry();
	}
	
	public static Locale getLocale(String localeString) {
		if (localeString == null || localeString.isEmpty())
			return Locale.getDefault();
        
		// Extract language
        int languageIndex = localeString.indexOf('_');
        String language = null;
        if (languageIndex == -1) {
            // No further "_" so is "{language}" only
            return new Locale(localeString, "");
        } else {
            language = localeString.substring(0, languageIndex);
        }

        // Extract country
        int countryIndex = localeString.indexOf('_', languageIndex + 1);
        String country = null;
        if (countryIndex == -1) {
            // No further "_" so is "{language}_{country}"
            country = localeString.substring(languageIndex+1);
            return new Locale(language, country);
        } else {
            // Assume all remaining is the variant so is "{language}_{country}_{variant}"
            country = localeString.substring(languageIndex+1, countryIndex);
            String variant = localeString.substring(countryIndex+1);
            return new Locale(language, country, variant);
        }
    }	
}
