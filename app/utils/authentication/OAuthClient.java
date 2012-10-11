package utils.authentication;

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

import controllers.Application;
import controllers.UserController;
import controllers.routes;
import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Http.Request;
import play.mvc.Result;
import play.mvc.Results;
import utils.extractor.UserData;
import views.html.oauthResponse;

public class OAuthClient {
	public static final String OAUTH_PROVIDER = "oauth.provider";
	private static final String OAUTH_REQUEST_TOKEN = "oauth.request_token";

	private static final String ERROR = "error";
	private static final String ERROR_REASON = "error_reason";
	private static final String ERROR_DESC = "error_description";

	private static final String VERSION_10 = "1.0";

	
	@SuppressWarnings("serial")
	public static final Map<String, AuthenticationProvider> OAUTH_PROVIDERS = Collections.unmodifiableMap(new HashMap<String, AuthenticationProvider>(){ {
																		put("Facebook", AuthenticationProvider.FACEBOOK);
																		put("Google", AuthenticationProvider.GOOGLE20);
																}});
	
	public Result sendAuthRequest(String authenticationProvider) {
    	Controller.session().put(OAUTH_PROVIDER, authenticationProvider);
    	OAuthService service = getOAuthService(authenticationProvider, true);
    	Token requestToken = null;
    	if(service.getVersion().equals(VERSION_10)) {
    		requestToken = service.getRequestToken();
    		Controller.session().put(OAUTH_REQUEST_TOKEN, requestToken == null ? null : requestToken.getToken());
        	//TODO: Store the token secret as well
    	}
        return Results.redirect(service.getAuthorizationUrl(requestToken));
	}
	
    public Result handleProviderResponse(Request httpRequest) {
    	//TODO: Extract the secret key.. that is going to tell us who the outh provider is gonna be
    	
    	//String oauth_token = form.get(OAuthConstants.TOKEN); Might need this for oauth1.0a
    	
    	DynamicForm form = Controller.form().bindFromRequest();
    	
    	String oauth_code = form.get(OAuthConstants.CODE);
    	
    	if (oauth_code != null && oauth_code.length() > 0) {
    		Verifier verifier = new Verifier(oauth_code);
    		String oauthProvider = Controller.session().get(OAUTH_PROVIDER);
    		OAuthService service = getOAuthService(oauthProvider, false);
    		/**TODO: You must validate the OAuth Token
    		 * TODO : You might want to store this token in db
    		 * See : https://developers.google.com/accounts/docs/OAuth2Login#validatingtoken
    		 */
    		Token accessToken = service.getAccessToken(null, verifier);
    		
    	    UserData userData = getUserData(oauthProvider, service, accessToken);
    	    if (userData.email != null) {
	    	    User currentUser = User.findByEmail(userData.email);
	            if (currentUser == null) {
	            	currentUser = new User(userData);
	            	return UserController.newUserSignup(currentUser);
	            }
	        	return Application.authenticationSuccess(currentUser);
    	    }
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
    
    private OAuthService getOAuthService(String authenticationProvider, boolean scope) {
    	AuthenticationProvider authProvider = OAUTH_PROVIDERS.get(authenticationProvider);
    	ServiceBuilder sb = new ServiceBuilder().provider(authProvider.getApiClass())
						        .apiKey(authProvider.getApiKey())
						        .apiSecret(authProvider.getApiSecret())
						        .callback(Application.domainUrl() + routes.Authentication.handleOAuthProviderResponse().url());
    	if (scope) {
    		sb.scope(authProvider.getScope());
    	}
    	return sb.build();
    }
    
    private UserData getUserData(String oauthProvider, OAuthService service, Token accessToken) {
		AuthenticationProvider oauthServiceProvider = OAUTH_PROVIDERS.get(oauthProvider);
		OAuthRequest request = new OAuthRequest(Verb.GET, oauthServiceProvider.getProtectedResourceUrl());
	    service.signRequest(accessToken, request);
	    Response response = request.send();
	    return oauthServiceProvider.getUserDataExtractor().extractUserData(response.getBody());
    }
}