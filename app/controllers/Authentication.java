package controllers;

import java.io.IOException;

import org.openid4java.consumer.ConsumerException;

import play.mvc.Controller;
import play.mvc.Result;


import utils.authentication.OAuthClient;
import utils.authentication.OpenIdConsumer;

public class Authentication extends Controller {
	private static final String AUTHENTICATION_PROVIDER = "authentication_provider";
	public static final String EMAIL = "email";
	
    public static Result authenticate() throws ConsumerException, IOException {
    	String authenticationProvider = form().bindFromRequest().get(AUTHENTICATION_PROVIDER);
		
    	if (OAuthClient.OAUTH_PROVIDERS.keySet().contains(authenticationProvider)) {
    		return authenticateWithOAuthProvider(authenticationProvider);
    	}    	
		if (OpenIdConsumer.OPEN_ID_PROVIDERS.keySet().contains(authenticationProvider)) {
			return authenticateWithOpenIdProvider(authenticationProvider);
    	} 

		return TODO;
    }
    
    private static Result authenticateWithOAuthProvider(String authenticationProvider) {
    	return new OAuthClient().sendAuthRequest(authenticationProvider);
    }
    
    private static Result authenticateWithOpenIdProvider(String authenticationProvider) throws ConsumerException, IOException {
		return new OpenIdConsumer().authRequest(authenticationProvider);
    }
    
    public static Result handleOpenIdProviderResponse() throws ConsumerException {
    	return new OpenIdConsumer().handleProviderResponse(request());
    }
    
    public static Result handleOAuthProviderResponse() {
    	return new OAuthClient().handleProviderResponse(request());
    }
}