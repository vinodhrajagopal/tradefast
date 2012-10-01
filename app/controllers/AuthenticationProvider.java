package controllers;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.DefaultApi10a;
import org.scribe.builder.api.DefaultApi20;
import org.scribe.builder.api.FacebookApi;
import org.scribe.builder.api.GoogleApi;
import org.scribe.model.Verb;

/**
 * The various authentication providers supported by the app.
 * TODO: Move this out of controllers into a different package
 * @author vrajagopal
 *
 */
public enum AuthenticationProvider {
	FACEBOOK(new FacebookApi(), "149484441860781","aae5c8a5610d0ae9da40ae38131ff490", "https://graph.facebook.com/me"),//TODO: Don't hardcode here. put the apikey and apisecret in a file
	GOOGLE(new GoogleApi(), "", "", "");

	private Api api;
	private final String apiKey;
	private final String apiSecret;
	private final String protectedResourceUrl;
	

	private AuthenticationProvider(Api api, String apiKey, String apiSecret, String protectedResourceUrl) {
		this.api = api;
		this.apiKey = apiKey;
		this.apiSecret = apiSecret;
		this.protectedResourceUrl = protectedResourceUrl;
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
	
	public String getAccessTokenEndpoint() {
		if (api.getClass().isAssignableFrom(DefaultApi10a.class)) {
			return ((DefaultApi10a)api).getAccessTokenEndpoint();
		} else if (api.getClass().isAssignableFrom(DefaultApi20.class)) {
			return ((DefaultApi20)api).getAccessTokenEndpoint();
		}
		return null;
	}
	
	public Verb getAccessTokenVerb() {
		if (api.getClass().isAssignableFrom(DefaultApi10a.class)) {
			return ((DefaultApi10a)api).getAccessTokenVerb();
		} else if (api.getClass().isAssignableFrom(DefaultApi20.class)) {
			return ((DefaultApi20)api).getAccessTokenVerb();
		}
		return null;
	}
	
	public String getProtectedResourceUrl() {
		return this.protectedResourceUrl;
	}
}
