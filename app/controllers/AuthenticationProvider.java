package controllers;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.FacebookApi;
import org.scribe.builder.api.GoogleApi;

public enum AuthenticationProvider {
	FACEBOOK(FacebookApi.class, "149484441860781","aae5c8a5610d0ae9da40ae38131ff490"),//TODO: Don't hardcode here. put the apikey and apisecret in a file
	GOOGLE(GoogleApi.class, "", "");

	private final Class<? extends Api> apiClass;
	private final String apiKey;
	private final String apiSecret;
	
	private AuthenticationProvider(Class<? extends Api> apiClass, String apiKey, String apiSecret) {
		this.apiClass = apiClass;
		this.apiKey = apiKey;
		this.apiSecret = apiSecret;
	}
	
	public Class<? extends Api> getApiClass() {
		return this.apiClass;
	}
	
	public String getApiKey() {
		return this.apiKey;
	}
	
	public String getApiSecret() {
		return this.apiSecret;
	}
}
