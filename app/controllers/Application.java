package controllers;

import java.io.IOException;

import org.openid4java.consumer.ConsumerException;

import controllers.authentication.openid.OpenIdConsumer;
import controllers.authentication.openid.OpenIdConsumer.OpenIdVerifyResult;
import models.User;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.*;
import views.html.*;


public class Application extends Controller {
	
	private static final String BASE_URL = "http://mighty-island-7343.herokuapp.com";
	
	public final static String COOKIE_USER_ID = "userId";
	private static final String OPEN_ID_IDENTIFIER = "openid_identifier";
	
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
    	String openIdIdentifier = form.get(OPEN_ID_IDENTIFIER);
    	if (openIdIdentifier != null && !openIdIdentifier.isEmpty()) {
    		return new OpenIdConsumer().authRequest(openIdIdentifier);
    	} else {    	
    		return TODO;
    	}
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
}