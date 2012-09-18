package controllers;

import models.User;
import play.mvc.Controller;

public class UserController extends Controller {
	public static User getCurrentUser() {
		User currentUser = null;
		String currentUserId = getCurrentUserId();
		if (currentUserId != null) {
			currentUser = User.findByEmail(currentUserId);
		}
		return currentUser;
	}
	
	public static String getCurrentUserId() {
		return session(Application.COOKIE_USER_ID);
	}
}