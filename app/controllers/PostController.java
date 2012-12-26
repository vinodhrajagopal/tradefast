package controllers;

import java.io.File;

import models.Post;
import models.PostPhoto;
import models.User;

import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Results;

import play.mvc.Result;
import views.html.index;
import views.html.postDetail;
import views.html.editPost;
import views.html.userPosts;


public class PostController extends Controller {
	
	public static Result posts() {
		//TODO: here in the form, you should actually try to fill the seller id before sending it
		Post post = new Post();
		User user = UserController.loggedInUser();
		if (user != null) {
			post.city= user.city;
			post.state = user.state;
			post.country = user.country;
			post.zipcode = user.zipcode;
			post.createdBy = user.userName;			
		}
		//TODO:Use bind() instead of the above to avoid 0.0 in price field
		return ok(index.render(Post.listPosts(), form(Post.class).fill(post), user));
	}
	
	public static Result savePost() {
		Form<Post> filledForm = form(Post.class).bindFromRequest();
		if(filledForm.hasErrors()) {
			return badRequest(index.render(Post.listPosts(), filledForm, UserController.loggedInUser()));
		}
		//TODO: Check that if the user who tries to save is actually the post creator
		Post post = filledForm.get();
		Post.savePost(post);
		PostPhoto photo = getPhoto();
		if (photo != null) {
			photo.post = post;
			photo.save();
		}
		return redirect(routes.PostController.posts());
	}
		
	public static Result deletePost(Long id) {
		//TODO: Check for permissions before you delete the post. Try to make use of Authentication
		Post.delete(id);
		flash("headerMessage","Your post was successfully deleted");
		return redirect(routes.PostController.posts());
	}
	
	public static Result getPost(Long id) {
		Post post = Post.get(id);
		if (post != null) {
			return ok(postDetail.render(post));
		} else {
			return TODO;
		}
	}
	
	//TODO: Try to use authentication to enforce ownership/login check
	public static Result editPost(Long id) {		
		User currentUser = UserController.loggedInUser();
		if (currentUser == null) {
			// User not logged in. So redirect to login first
			//String uri = request().uri();
			flash("loginMessage", "You are not logged in");
			return redirect(routes.Application.login());
		}
		Post post = Post.get(id);
		if (post != null) {
			if (!post.createdBy.equals(currentUser.userName)) {
				return TODO; // You cannot edit post which you did not post.
			}
			Form<Post> postForm = form(Post.class).fill(post);
			return ok(editPost.render(postForm, currentUser));
		} else {
			return TODO; //Post does not exist
		}
	}
	
	public static Result userPosts(String userName) {
		return ok(userPosts.render(Post.listPostsCreatedBy(userName), UserController.loggedInUser()));
	}	
	
    private static PostPhoto getPhoto() {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart uploadFilePart = body.getFile("postPhoto");
        if (uploadFilePart != null) {
            PostPhoto photo = new PostPhoto();
            photo.name = uploadFilePart.getFilename();
            photo.file = uploadFilePart.getFile();
            return photo;
        } else {
            return null;
        }
    }
    
    public static Result deletePhoto() {
		User loggedInUser = UserController.loggedInUser();		
		if (loggedInUser == null) {
			return unauthorized();
		}
		
    	DynamicForm form = form().bindFromRequest();
    	Long postId = Application.parseLong(form.get("postId"));
		Long photoId = Application.parseLong(form.get("photoId"));
		PostPhoto photo = PostPhoto.findPhoto(photoId);
		if (photo == null) {
			return notFound();
		} 
		
		if (postId.compareTo(photo.post.id) != 0) {
			return unauthorized();
		} 
		
    	if (!loggedInUser.userName.equals(photo.post.createdBy)) {
    		return unauthorized();
    	}
    	
		photo.delete();
		return ok();
    }
    
    //TODO: Need to fix this
    public static Result updatePhoto() {
		User loggedInUser = UserController.loggedInUser();		
		if (loggedInUser == null) {
			return unauthorized();
		}

    	PostPhoto newPhoto = getPhotoFromAjax();
    	if (newPhoto == null) {
    		return Results.badRequest();
    	}
		
    	DynamicForm form = form().bindFromRequest();
    	Long postId = Application.parseLong(form.get("postId"));
		Long photoId = Application.parseLong(form.get("photoId"));
		PostPhoto oldPhoto = PostPhoto.findPhoto(photoId);
		if (oldPhoto == null) {
			return notFound();
		} 
		
		if (postId.compareTo(oldPhoto.post.id) != 0) {
			return unauthorized();
		} 
		
    	if (!loggedInUser.userName.equals(oldPhoto.post.createdBy)) {
    		return unauthorized();
    	}

    	PostPhoto.updatePhoto(oldPhoto, newPhoto);
    	return ok();
    }
    
    private static PostPhoto getPhotoFromAjax() {
    	File file = request().body().asRaw().asFile();
        if (file != null) {
            PostPhoto photo = new PostPhoto();
            photo.name = file.getName();
            photo.file = file;
            return photo;
        } else {
            return null;
        }
    }
}