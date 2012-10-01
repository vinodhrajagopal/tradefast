package controllers;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.openid4java.consumer.ConsumerException;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Api;
import org.scribe.model.OAuthConstants;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import controllers.authentication.openid.OpenIdConsumer;
import controllers.authentication.openid.OpenIdConsumer.OpenIdVerifyResult;
import models.User;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.*;
import views.html.*;


public class Application extends Controller {
	
	private static final String BASE_URL = "http://tradefast.herokuapp.com";//TODO: Read all these from files
	
	public final static String COOKIE_USER_ID = "userId";
	private static final String OPEN_ID_URL = "openid_url";
	private static final String AUTHENTICATION_PROVIDER = "authentication_provider";
	private static final String OPEN_ID = "OpenID";
	
	private static final String OAUTH_PROVIDER = "oauth.provider";
	private static final String OAUTH_REQUEST_TOKEN = "oauth.request_token";
	
	//private static final String VERSION_20 = "2.0";
	private static final String VERSION_10 = "1.0";
	
	@SuppressWarnings("serial")
	private static final Map<String,String> OPEN_ID_PROVIDERS = Collections.unmodifiableMap(new HashMap<String,String>(){{
																		put("Google", "https://www.google.com/accounts/o8/id");
																		put("Yahoo", "http://me.yahoo.com/");
																		put("OpenID", "");
																}});
	
	@SuppressWarnings("serial")
	private static final Map<String, AuthenticationProvider> OAUTH_PROVIDERS = Collections.unmodifiableMap(new HashMap<String, AuthenticationProvider>(){ {
																		put("Facebook", AuthenticationProvider.FACEBOOK);
																}});
		
	public static String domainUrl() {
		return BASE_URL;
	}
	
	public static Result index() {
		response().setHeader("X-XRDS-Location", BASE_URL + "/xrds");
		return ItemController.items();
	}
	
    public static class Login {
        public String email;
        public String password;
        
        public String validate() {
            if(User.authenticate(email, password) == null) {
                return "Invalid user or password";
            }
            return null;
        }
    }

    /**
     * Login page.
     */
    public static Result login() {
        return ok(login.render(form(Login.class)));
    }
    
    /**
     * Handle login form submission.
     */
    public static Result authenticate() {
        Form<Login> loginForm = form(Login.class).bindFromRequest();
        if(loginForm.hasErrors()) {
            return badRequest(login.render(loginForm));
        } else {
        	return authenticationSuccess(loginForm.get().email);
        }
    }
    
    private static Result authenticationSuccess(String email) {
    	User currentUser = User.findByEmail(email);
    	if (currentUser != null) {
	        session(COOKIE_USER_ID, currentUser.userName);
	        return redirect(routes.Application.index());
    	} else {
    		return TODO;//This is the first time the user is logging in. Ask him to fill in the form
    	}
    }

    /**
     * Logout and clean the session.
     */
    public static Result logout() {
        session().clear();
        flash("loginMessage", "You've been logged out");
        return redirect(routes.Application.login());
    }	


    public static Result authenticateWithOpenId() throws ConsumerException, IOException {
    	DynamicForm form = form().bindFromRequest();
    	String authenticationProvider = form.get(AUTHENTICATION_PROVIDER);
    	
		if (OPEN_ID_PROVIDERS.keySet().contains(authenticationProvider)) {
        	String openIdIdentifier = OPEN_ID.equals(authenticationProvider) ? form.get(OPEN_ID_URL) : OPEN_ID_PROVIDERS.get(authenticationProvider);
        	if (openIdIdentifier != null && !openIdIdentifier.isEmpty()) {
        		return new OpenIdConsumer().authRequest(openIdIdentifier);
        	} else {    	
        		return TODO;
        	}
    	} else if (OAUTH_PROVIDERS.keySet().contains(authenticationProvider)) {
    		return authenticateWithOAuthProvider(authenticationProvider);
    	}
		return TODO;
    }
    
    public static Result verifyOpenIdProviderResponse() throws ConsumerException {
    	OpenIdVerifyResult result = new OpenIdConsumer().verifyResponse(request());
    	if (result != null && result.getEmail() != null) {
    		return authenticationSuccess(result.getEmail());
    	} else {
    		return TODO;
    	}
    }
    
    public static Result verifyOAuthProviderResponse() {
    	//TODO: Extract the secret key.. that is going to tell us who the outh provider is gonna be
    	DynamicForm form = form().bindFromRequest();
    	
    	//String oauth_token = form.get(OAuthConstants.TOKEN);
    	
    	String oauth_verifier = form.get(OAuthConstants.VERIFIER);
    	
    	String oauthResponseBody = "Didnt get anything yet"; 
    	
    	if (oauth_verifier.length() > 0) {
    		Verifier verifier = new Verifier(oauth_verifier);
    		String oauthProvider = Controller.session().get(OAUTH_PROVIDER);

    		OAuthService service = getOAuthService(oauthProvider);
    		Token accessToken = service.getAccessToken(null, verifier); //TODO : You might want to store this token in db

    		AuthenticationProvider oauthServiceProvider = OAUTH_PROVIDERS.get(oauthProvider);
    		OAuthRequest request = new OAuthRequest(Verb.GET, oauthServiceProvider.getProtectedResourceUrl());
    	    service.signRequest(accessToken, request);
    	    
    	    Response response = request.send();
    	    oauthResponseBody = response.getBody();
    	}
    	return ok(oauthResponse.render(oauthResponseBody));
    }
    
    
    public static Result xrds() {
    	return ok(xrds.render());
    }
    
    private static Result authenticateWithOAuthProvider(String authenticationProvider) {
    	Controller.session().put(OAUTH_PROVIDER, authenticationProvider);
    	OAuthService service = getOAuthService(authenticationProvider);
    	Token requestToken = null;
    	if(service.getVersion().equals(VERSION_10)) {
    		requestToken = service.getRequestToken();
    		Controller.session().put(OAUTH_REQUEST_TOKEN, requestToken == null ? null : requestToken.getToken());
        	//TODO: Store the token secret as well
    	}
    	

        return Results.redirect(service.getAuthorizationUrl(requestToken)); // For now use the empty token
    	
    }
    
    private static OAuthService getOAuthService(String authenticationProvider) {
    	AuthenticationProvider authProvider = OAUTH_PROVIDERS.get(authenticationProvider);
        return new ServiceBuilder()
							        .provider(authProvider.getApiClass())
							        .apiKey(authProvider.getApiKey())
							        .apiSecret(authProvider.getApiSecret())
							        .callback(BASE_URL + routes.Application.verifyOAuthProviderResponse().url())
							        .build();
    }
}