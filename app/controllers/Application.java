package controllers;

import models.User;
import play.Play;
import play.Routes;
import play.data.Form;
import play.mvc.*;

import views.html.*;


public class Application extends Controller {
	
	private static final String DOMAIN_URL = Play.application().configuration().getString("domain.url");
	
	public final static String COOKIE_USER_NAME = "username";

		
	public static String domainUrl() {
		return DOMAIN_URL;
	}
	
	public static Result index() {
		response().setHeader("X-XRDS-Location", DOMAIN_URL + "/xrds");
		return PostController.posts();
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
	        session(COOKIE_USER_NAME, currentUser.userName);
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
    
    public static Result javascriptRoutes() {
        response().setContentType("text/javascript");
        return ok(Routes.javascriptRouter("jsRoutes", 
        		routes.javascript.MessageController.sendMessage(),
        		routes.javascript.PostController.deletePhoto()));
    }
    
	public static Long parseLong(String val) {
		return val == null ? null : Long.parseLong(val);
	}

}