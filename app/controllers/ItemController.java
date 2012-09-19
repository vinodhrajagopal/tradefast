package controllers;

import java.util.Calendar;

import models.Item;
import play.data.Form;
import play.mvc.Controller;

import play.mvc.Result;
import views.html.index;
import views.html.itemDetail;
import views.html.editItem;
import views.html.userPosts;


public class ItemController extends Controller {
	
	public static Result items() {
		//TODO: here in the form, you should actually try to fill the seller id before sending it
		return ok(index.render(Item.listItems(), form(Item.class), UserController.getCurrentUser()));
	}
	
	public static Result newItem() {
		Form<Item> filledForm = form(Item.class).bindFromRequest();
		if(filledForm.hasErrors()) {
			return badRequest(index.render(Item.listItems(), filledForm, UserController.getCurrentUser()));
		} else {
			Item newItem = filledForm.get();
			Calendar currentTime = Calendar.getInstance();
			newItem.createdTime = currentTime.getTime();
			currentTime.add(Calendar.HOUR_OF_DAY, newItem.saleDuration);
			newItem.endTime = currentTime.getTime();
			Item.create(newItem);
			return redirect(routes.ItemController.items());
		}
	}
	
	public static Result buyItem(Long id) {
		return TODO;
	}
	
	public static Result deleteItem(Long id) {
		Item.delete(id);
		return items();
	}
	
	public static Result getItem(Long id) {
		Item item = Item.get(id);
		if (item != null) {
			return ok(itemDetail.render(item));
		} else {
			return TODO;
		}
	}
	
	public static Result editItem(Long id) {
		String userId = UserController.getCurrentUserId();
		if (userId == null) {
			// User not logged in. So redirect to login first
			//String uri = request().uri();
			flash("loginMessage", "You are not logged in");
			return redirect(routes.Application.login());
		}
		Item item = Item.get(id);
		if (item != null) {
			if (!item.sellerId.equals(userId)) {
				return TODO; // You cannot edit item which you did not post.
			}
			Form<Item> itemForm = form(Item.class).fill(item);
			return ok(editItem.render(itemForm));
		} else {
			return TODO; //Item does not exist
		}
	}
	
	public static Result saveItem() {
		Form<Item> filledForm = form(Item.class).bindFromRequest();// You probably need to have an allowedFields parameter here to avoid Required field exception for selledid field
																	//..Temporarily solved it by putting the selled id on the form.. yuck
		if(filledForm.hasErrors()) {
			return badRequest(editItem.render(filledForm));
		} else {
 			Item.update(filledForm.get());
			return redirect(routes.ItemController.items());
		}
	}
	
	public static Result userPosts(String userId) {
		return ok(userPosts.render(Item.listItemsPostedBy(UserController.getCurrentUserId()), UserController.getCurrentUser()));
		//return ok(index.render(Item.listItems(), Item.listItemsPostedBy(UserController.getCurrentUserId()), form(Item.class), UserController.getCurrentUser()));
	}	
}