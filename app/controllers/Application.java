package controllers;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.openid4java.consumer.ConsumerException;
import org.scribe.builder.ServiceBuilder;
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
	
	@SuppressWarnings("serial")
	private static final Map<String,String> OPEN_ID_PROVIDERS = Collections.unmodifiableMap(new HashMap<String,String>(){{
																		put("Google", "https://www.google.com/accounts/o8/id");
																		put("Yahoo", "http://me.yahoo.com/");
																		put("OpenID", "");
																}});
	
	@SuppressWarnings("serial")
	private static final Map<String, AuthenticationProvider> OAUTH_PROVIDERS = Collections.unmodifiableMap(new HashMap<String, AuthenticationProvider>(){ {
																		put("FaceBook", AuthenticationProvider.FACEBOOK);
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
    		return authenticateWithOAuthProvider(OAUTH_PROVIDERS.get(authenticationProvider));
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
    
    public static Result xrds() {
    	return ok(xrds.render());
    }
    
    private static Result authenticateWithOAuthProvider(AuthenticationProvider authProvider) {
        OAuthService service = new ServiceBuilder()
							        .provider(authProvider.getApiClass())
							        .apiKey(authProvider.getApiKey())
							        .apiSecret(authProvider.getApiSecret())
							        .callback(routes.Application.authenticateWithOpenId().url())
							        .build();
        return Results.redirect(service.getAuthorizationUrl(null)); // For now use the empty token
    	
    }
}