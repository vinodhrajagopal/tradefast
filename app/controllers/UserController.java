package controllers;

import models.Item;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class UserController extends Controller {
	public static User getCurrentUser() {
		User currentUser = null;
		String currentUserId = getCurrentUserId();
		if (currentUserId != null) {
			currentUser = User.findByUsername(currentUserId);
		}
		return currentUser;
	}
	
	public static String getCurrentUserId() {
		return session(Application.COOKIE_USER_ID);
	}
	
	public static Result saveUser() {
		Form<User> userForm = form(User.class).bindFromRequest();
		if(userForm.hasErrors()) {
			return badRequest(userProfile.render(userForm, null));
		}
		User user = userForm.get();
		user.save();
		if (getCurrentUserId() == null) { // This is the first time this user has logged in.
			session(Application.COOKIE_USER_ID, user.userName);
		}
        return redirect(routes.Application.index());
	}
}