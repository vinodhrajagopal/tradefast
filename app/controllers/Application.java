package controllers;

import models.User;
import play.data.Form;
import play.mvc.*;
import views.html.*;


public class Application extends Controller {
	
	//private static final String BASE_URL = "http://tradefast.herokuapp.com";//TODO: Read all these from files
	private static final String DOMAIN_URL = "http://192.168.1.19:9000";//TODO: Read all these from files
	
	public final static String COOKIE_USER_ID = "userId";

		
	public static String domainUrl() {
		return DOMAIN_URL;
	}
	
	public static Result index() {
		response().setHeader("X-XRDS-Location", DOMAIN_URL + "/xrds");
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
        	return authenticationSuccess(User.findByEmail(loginForm.get().email));
        }
    }
    
    public static Result authenticationSuccess(User currentUser) {   	
    	if (currentUser != null) {
	        session(COOKIE_USER_ID, currentUser.userName);
	        return redirect(routes.Application.index());
    	} else {
    		Form<User> userForm = form(User.class).fill(currentUser);
    		return ok(userProfile.render(userForm, null));//This is the first time the user is logging in. Ask him to fill in the form
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
    
    public static Result xrds() {
    	return ok(xrds.render());
    }
}