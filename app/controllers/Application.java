package controllers;

import models.User;
import play.data.Form;
import play.mvc.*;
import views.html.*;


public class Application extends Controller {
	
	public final static String COOKIE_USER_ID = "userId";
	
	public static Result index() {
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
            session(COOKIE_USER_ID, loginForm.get().email);
            return redirect(routes.Application.index());
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
}