package utils.extractor;

import utils.I18nUtils;

import com.google.gson.Gson;

public class FacebookUserDataExtractor implements UserDataExtractor {

	@Override
	public UserData extractUserData(String response) {
		if (response == null || response.isEmpty()) {
			throw new RuntimeException("Cannot extract user data from a null/empty string");
		}
		FacebookJSonData jsonArray = new Gson().fromJson(response, FacebookJSonData.class);
		FacebookUserData fbUserData = jsonArray.data[0];
		UserData userData = new UserData();
		userData.username = fbUserData.username;
		userData.email = fbUserData.email;
		userData.picture = fbUserData.pic;
		if (fbUserData.locale != null && !fbUserData.locale.isEmpty()) {
			userData.locale = fbUserData.locale.replace("-", "_").trim();
			userData.country = I18nUtils.getCountry(userData.locale);
		}
		if (fbUserData.current_location != null) {
			userData.city = fbUserData.current_location.city;
			userData.state = fbUserData.current_location.state;
			userData.country = fbUserData.current_location.country;
			userData.zipcode = fbUserData.current_location.zip;
		}
		if (fbUserData.currency != null) {
			userData.currency = fbUserData.currency.user_currency;
		}
		return userData;
	}

    private class FacebookJSonData {
    	FacebookUserData []data;
    }
    
	private class FacebookUserData {
		public String username;
		public String email;
		public String pic;
		public String locale;
    	public CurrentLocation current_location;
    	public Currency currency;
    	
    	private class CurrentLocation {
    		public String city;
    		public String state;
    		public String country;
    		public String zip;
    	}
    	private class Currency {
    		public String user_currency;
    	}
	}
}
