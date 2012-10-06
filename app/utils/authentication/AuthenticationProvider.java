package utils.authentication;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.FacebookApi;
import org.scribe.builder.api.GoogleApi20;
import org.scribe.model.Verb;
import org.scribe.utils.*;

import utils.extractor.FacebookUserDataExtractor;
import utils.extractor.GoogleUserDataExtractor;
import utils.extractor.UserDataExtractor;

import controllers.Authentication;

/**
 * The various authentication providers supported by the app.
 * TODO: Move this out of controllers into a different package
 * @author vrajagopal
 *
 */
public enum AuthenticationProvider {
	FACEBOOK(new FacebookApi(), "149484441860781","aae5c8a5610d0ae9da40ae38131ff490", "https://graph.facebook.com/fql?q=" + OAuthEncoder.encode("SELECT username,email,pic,current_location,currency,locale FROM user WHERE uid=me()"), Authentication.EMAIL, new FacebookUserDataExtractor()),//TODO: Don't hardcode here. put the apikey and apisecret in a file
	//FACEBOOK(new FacebookApi(), "149484441860781","aae5c8a5610d0ae9da40ae38131ff490", "https://graph.facebook.com/me?fields=username,email,location,picture,locale,currency", Authentication.EMAIL, new FacebookUserDataExtractor()),//TODO: Don't hardcode here. put the apikey and apisecret in a file
	GOOGLE20(new GoogleApi20(), "777841941314.apps.googleusercontent.com", "sLglUDXngOWRYKyzjCx8sVs9", "https://www.googleapis.com/oauth2/v1/userinfo" , "https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email", new GoogleUserDataExtractor());//TODO: Use GoogleApi20()

	private Api api;
	private final String apiKey;
	private final String apiSecret;
	private final String protectedResourceUrl;
	private final String scope;
	private final UserDataExtractor userDataExtractor;
	

	private AuthenticationProvider(Api api, String apiKey, String apiSecret, String protectedResourceUrl, String scope, UserDataExtractor userDataExtractor) {
		this.api = api;
		this.apiKey = apiKey;
		this.apiSecret = apiSecret;
		this.protectedResourceUrl = protectedResourceUrl;
		this.scope = scope;
		this.userDataExtractor = userDataExtractor;
	}
	
	public Api getApi() {
		return this.api;
	}
	
	public Class<? extends Api> getApiClass() {
		return this.api.getClass();
	}
	
	public String getApiKey() {
		return this.apiKey;
	}
	
	public String getApiSecret() {
		return this.apiSecret;
	}
	
	public String getScope() {
		return this.scope;
	}
	
	public String getAccessTokenEndpoint() {
		return this.api.getAccessTokenEndpoint();
	}
	
	public Verb getAccessTokenVerb() {
		return this.api.getAccessTokenVerb();
	}
	
	public String getProtectedResourceUrl() {
		return this.protectedResourceUrl;
	}
	
	public UserDataExtractor getUserDataExtractor() {
		return this.userDataExtractor;
	}
}