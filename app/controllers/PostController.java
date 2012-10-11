package controllers;

import java.util.Calendar;

import models.Post;
import models.User;
import play.data.Form;
import play.mvc.Controller;

import play.mvc.Result;
import views.html.index;
import views.html.postDetail;
import views.html.editPost;
import views.html.userPosts;


public class PostController extends Controller {
	
	public static Result posts() {
		//TODO: here in the form, you should actually try to fill the seller id before sending it
		Post post = new Post();
		User user = UserController.getCurrentUser();
		if (user != null) {
			post.city= user.city;
			post.state = user.state;
			post.country = user.country;
			post.zipcode = user.zipcode;
			post.sellerId = user.userName;			
		}
		//TODO:Use bind() instead of the above to avoid 0.0 in price field
		return ok(index.render(Post.listPosts(), form(Post.class).fill(post), user));
	}
	
	public static Result newPost() {
		Form<Post> filledForm = form(Post.class).bindFromRequest();
		if(filledForm.hasErrors()) {
			//return badRequest(index.render(Post.listPosts(), filledForm, UserController.getCurrentUser()));
			return TODO;
		} else {
			Post newPost = filledForm.get();
			Calendar currentTime = Calendar.getInstance();
			newPost.createdTime = currentTime.getTime();
			currentTime.add(Calendar.HOUR_OF_DAY, newPost.postDuration);
			newPost.endTime = currentTime.getTime();
			Post.create(newPost);
			return redirect(routes.PostController.posts());
		}
	}
	
	public static Result deletePost(Long id) {
		Post.delete(id);
		return posts();
	}
	
	public static Result getPost(Long id) {
		Post post = Post.get(id);
		if (post != null) {
			return ok(postDetail.render(post));
		} else {
			return TODO;
		}
	}
	
	public static Result editPost(Long id) {		
		User currentUser = UserController.getCurrentUser();
		if (currentUser == null) {
			// User not logged in. So redirect to login first
			//String uri = request().uri();
			flash("loginMessage", "You are not logged in");
			return redirect(routes.Application.login());
		}
		Post post = Post.get(id);
		if (post != null) {
			if (!post.sellerId.equals(currentUser.userName)) {
				return TODO; // You cannot edit post which you did not post.
			}
			Form<Post> postForm = form(Post.class).fill(post);
			return ok(editPost.render(postForm, currentUser));
		} else {
			return TODO; //Post does not exist
		}
	}
	
	public static Result savePost() {
		Form<Post> filledForm = form(Post.class).bindFromRequest();// You probably need to have an allowedFields parameter here to avoid Required field exception for selledid field
																	//..Temporarily solved it by putting the seller id on the form.. yuck
		if(filledForm.hasErrors()) {
			return badRequest(editPost.render(filledForm, UserController.getCurrentUser()));
		} else {
 			Post.update(filledForm.get());
			return redirect(routes.PostController.posts());
		}
	}
	
	public static Result userPosts(String userName) {
		return ok(userPosts.render(Post.listPostsCreatedBy(userName), UserController.getCurrentUser()));
	}	
}