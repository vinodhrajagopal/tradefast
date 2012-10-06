package utils.extractor;

import utils.I18nUtils;

import com.google.gson.Gson;

public class GoogleUserDataExtractor implements UserDataExtractor {

	@Override
	public UserData extractUserData(String response) {
		if (response == null || response.isEmpty()) {
			throw new RuntimeException("Cannot extract user data from a null/empty string");
		}
		GoogleJSonData googleUserData = new Gson().fromJson(response, GoogleJSonData.class);
		
		UserData userData = new UserData();
		userData.email = googleUserData.email;
		userData.picture = googleUserData.picture;
		if (googleUserData.locale != null && !googleUserData.locale.isEmpty()) {
			userData.locale = googleUserData.locale.replace("-", "_").trim();
			userData.country = I18nUtils.getCountry(userData.locale);
		}
		return userData;
	}
	
	private class GoogleJSonData {
		public String email;
		public String picture;
		public String locale;
	}

}
