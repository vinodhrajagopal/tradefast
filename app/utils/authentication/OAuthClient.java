package utils.authentication;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


import models.User;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthConstants;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.google.gson.Gson;

import controllers.Application;
import controllers.Authentication;
import controllers.UserController;
import controllers.routes;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Controller.*;
import play.mvc.Result;
import play.mvc.Results;
import views.html.oauthResponse;
import views.html.userProfile;

public class OAuthClient {
	public static final String OAUTH_PROVIDER = "oauth.provider";
	private static final String OAUTH_REQUEST_TOKEN = "oauth.request_token";

	private static final String ERROR = "error";
	private static final String ERROR_REASON = "error_reason";
	private static final String ERROR_DESC = "error_description";

	//private static final String VERSION_20 = "2.0";
	private static final String VERSION_10 = "1.0";

	
	@SuppressWarnings("serial")
	public static final Map<String, AuthenticationProvider> OAUTH_PROVIDERS = Collections.unmodifiableMap(new HashMap<String, AuthenticationProvider>(){ {
																		put("Facebook", AuthenticationProvider.FACEBOOK);
																}});
	
	public Result authRequest(String authenticationProvider) {
    	Controller.session().put(OAUTH_PROVIDER, authenticationProvider);
    	OAuthService service = getOAuthService(authenticationProvider);
    	Token requestToken = null;
    	if(service.getVersion().equals(VERSION_10)) {
    		requestToken = service.getRequestToken();
    		Controller.session().put(OAUTH_REQUEST_TOKEN, requestToken == null ? null : requestToken.getToken());
        	//TODO: Store the token secret as well
    	}
        return Results.redirect(service.getAuthorizationUrl(requestToken));
	}
	
    public Result verifyResponse(DynamicForm form) throws UnsupportedEncodingException {
    	//TODO: Extract the secret key.. that is going to tell us who the outh provider is gonna be
    	
    	//String oauth_token = form.get(OAuthConstants.TOKEN); Might need this for oauth1.0a
    	
    	String oauth_code = form.get(OAuthConstants.CODE);
    	
    	if (oauth_code != null && oauth_code.length() > 0) {
    		Verifier verifier = new Verifier(oauth_code);
    		String oauthProvider = Controller.session().get(OAUTH_PROVIDER);

    		OAuthService service = getOAuthService(oauthProvider);
    		Token accessToken = service.getAccessToken(null, verifier); //TODO : You might want to store this token in db

    		AuthenticationProvider oauthServiceProvider = OAUTH_PROVIDERS.get(oauthProvider);

    		//For facebook, using fql.. TODO: This has to be rewritten in a generic way
    		OAuthRequest request = new OAuthRequest(Verb.GET, oauthServiceProvider.getProtectedResourceUrl());
    		request.addQuerystringParameter("q", "SELECT username,email,pic,current_location,currency FROM user WHERE uid=me()");
    	    service.signRequest(accessToken, request);
    	    Response response = request.send();
    	    
    	    Data dataArr = new Gson().fromJson(response.getBody(), Data.class);
    	    UserData userData = dataArr.data[0];
//    	    return Results.ok(oauthResponse.render(response.getBody() + "<<<<<<>>>>>>\n" + 
//    	    		" Currency " +userData.currency.user_currency +
//    	    		" Picture "+userData.pic + " Email"+ userData.email));
    	    User currentUser = User.findByEmail(userData.email);
            if (currentUser == null) {
            	//Fill it up with data from auth provider
            	currentUser = new User(userData);
            	return UserController.newUserSignup(currentUser);

            }
        	return Application.authenticationSuccess(currentUser);
    	    
    	} else {
    		//Probably an error
    	    String error = form.get(ERROR);
    	    if(error != null && !error.isEmpty()) {
	    	    String errorDesc = form.get(ERROR_DESC);
	    	    String errorReason = form.get(ERROR_REASON);
	    	    return Results.ok(oauthResponse.render(error + " " + errorReason + " " + errorDesc));
    	    }
    	}
    	return Results.ok(oauthResponse.render(""));
    }

    private OAuthService getOAuthService(String authenticationProvider) {
    	AuthenticationProvider authProvider = OAUTH_PROVIDERS.get(authenticationProvider);
        return new ServiceBuilder().provider(authProvider.getApiClass())
							        .scope(Authentication.EMAIL)
							        .apiKey(authProvider.getApiKey())
							        .apiSecret(authProvider.getApiSecret())
							        .callback(Application.domainUrl() + routes.Authentication.verifyOAuthProviderResponse().url())
							        .build();
    }
    
    class Data {
    	UserData []data;
    }
    
    public static class UserData {
    	public String username;
    	public String email;
    	public String pic;
    	public CurrentLocation current_location;
    	public Currency currency;
    	
    	public class CurrentLocation {
    		public String city;
    		public String state;
    		public String country;
    		public String zip;
    	}
    	public class Currency {
    		public String user_currency;
    	}
    }
}
