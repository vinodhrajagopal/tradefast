package controllers;

import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.userDetail;
import views.html.userProfile;

public class UserController extends Controller {
	public static User loggedInUser() {
		User currentUser = null;
		String currentUserId = loggedInUserName();
		if (currentUserId != null) {
			currentUser = User.findByUsername(currentUserId);
		}
		return currentUser;
	}
	
	public static String loggedInUserName() {
		return session(Application.COOKIE_USER_NAME);
	}
	
	public static Result getUser(String userName) {
		User user = User.findByUsername(userName);
		if (user == null) {
			return TODO;
		} else {
			return ok(userDetail.render(user));
		}
	}
	
	public static Result editUser(String userName) {
		User user = User.findByUsername(userName);
		if (user == null) {
			return TODO;//TODO : Check for edit permission
		}
		return editUser(user, false);	
	}
	
	public static Result newUserSignup(User user) {
		return editUser(user, true);
	}
	
	private static Result editUser(User user, boolean newUser) {
		Form<User> userForm = form(User.class).fill(user);
		return ok(userProfile.render(userForm, newUser ? null : user));//This is the first time the user is logging in. Ask him to fill in the form
	}
	
	public static Result saveUser() {
		Form<User> userForm = form(User.class).bindFromRequest();
		if(userForm.hasErrors()) {
			return badRequest(userProfile.render(userForm, null));
		}
		User user = userForm.get();
		user.save();
		if (loggedInUserName() == null) { // This is the first time this user has logged in.
			session(Application.COOKIE_USER_NAME, user.userName);
		}
        return redirect(routes.Application.index());
	}
}